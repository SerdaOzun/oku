package com.okuread.db.repositories

import androidx.compose.runtime.Immutable
import com.okuread.db.data.OkuText
import com.okuread.db.data.OkuTextListItem
import com.okuread.db.data.OkuWord
import com.okuread.db.util.OkuLanguage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * DB TABLE
 */
object OkuTextEntity : LongIdTable() {
    val title: Column<String> = varchar("title", 200)
    val body: Column<String> = text("body")
    val wordList: Column<String> = text("wordList") // all words in the texts
    val okuWordSet: Column<String?> = text("okuWordSet").nullable() //all OkuWord Ids in the text
    val timestamp_created: Column<LocalDateTime> = datetime("timestamp_created")
    val timestamp_finished: Column<LocalDateTime?> = datetime("timestamp_finished").nullable()
    val languageId: Column<Int> = integer("language_id").references(LanguageEntity.id)
    val frequency_analysis: Column<Boolean> =
        bool("frequency_analysis") // Is the text for reading or for frequency Analysis
    val currentPage: Column<Int> = integer("current_page").default(1)
    val updateMetrics: Column<Boolean> =
        bool("update_metrics").default(true) // Is the text for reading or for frequency Analysis
    val uniqueWordsCount: Column<Int> = integer("unique_words_count").default(-1)
    val percentageKnown: Column<Int> = integer("percentage_known").default(-1)
    val totalWordsCount: Column<Int> = integer("total_words_count").default(-1)
}

/**
 * Text currently opened for reading
 */
@Immutable
data class CurrentOkuText(
    val okuText: OkuText,
    val okuWords: Map<String, OkuWord> //Every unique okuWord in the text. Key: string of word
)

fun OkuTextEntity.getTrialTextLimit(): List<Long> = transaction {
    select(OkuTextEntity.id).where { frequency_analysis eq false }.limit(2).toList().map { it[OkuTextEntity.id].value }
}

fun OkuTextEntity.getTrialFrequencyTextLimit(): List<Long> = transaction {
    select(OkuTextEntity.id).where { frequency_analysis eq true }.limit(2).toList().map { it[OkuTextEntity.id].value }
}

fun OkuTextEntity.getAllIds(): List<Long> = transaction {
    select(OkuTextEntity.id).toList().map { it[OkuTextEntity.id].value }
}

/**
 * @return count of all okutexts
 */
fun OkuTextEntity.getCount(fromFrequencyAnalysis: Boolean): Int = transaction {
    selectAll().where { frequency_analysis eq fromFrequencyAnalysis }.count().toInt()
}

/**
 * Get OkuText by its Id
 * @param id
 */
fun OkuTextEntity.getText(id: Long): OkuText? = transaction {
    join(LanguageEntity, JoinType.INNER, onColumn = languageId, otherColumn = LanguageEntity.id)
        .selectAll().where { OkuTextEntity.id eq id }.singleOrNull()
        ?.let(::OkuText)
}

/**
 * Get OkuTextListItems
 */
fun OkuTextEntity.getTextListItems(
    search: String,
    filteredLanguage: OkuLanguage,
    isFromFrequencyAnalysis: Boolean,
    orderByColumn: Column<out Any>,
    orderAscending: Boolean,
    limit: Int,
    offset: Long
): List<OkuTextListItem> =
    transaction {
        val query = join(LanguageEntity, JoinType.INNER, onColumn = languageId, otherColumn = LanguageEntity.id)
            .selectAll()

        if (filteredLanguage != OkuLanguage.ALL) {
            val langId = LanguageEntity.getId(filteredLanguage)
            query.andWhere { LanguageEntity.id eq langId }
        }

        query.andWhere { frequency_analysis eq isFromFrequencyAnalysis }

        if (search.isNotBlank()) {
            query.andWhere { title.lowerCase() like "%${search.lowercase()}%" }
        }

        query.orderBy(orderByColumn, if (orderAscending) SortOrder.ASC else SortOrder.DESC)
            .limit(limit, offset)
            .map(::OkuTextListItem)
    }

fun OkuTextEntity.insertText(okuText: OkuText): Long = transaction {
    val langId = LanguageEntity.getId(okuText.language)

    if (okuText.id != null) {
        update(where = { OkuTextEntity.id eq okuText.id }) {
            it[title] = okuText.title
            it[body] = okuText.body
            it[wordList] = Json.encodeToString(okuText.wordList)
            it[okuWordSet] = Json.encodeToString(okuText.okuWordIdSet)
            it[timestamp_created] = LocalDateTime.now()
            it[languageId] = langId
            it[frequency_analysis] = okuText.isFromFrequencyAnalysis
            it[currentPage] = 1
            it[updateMetrics] = true
        }
        okuText.id
    } else {
        insertAndGetId {
            it[title] = okuText.title
            it[body] = okuText.body
            it[wordList] = Json.encodeToString(okuText.wordList)
            it[okuWordSet] = Json.encodeToString(okuText.okuWordIdSet)
            it[timestamp_created] = LocalDateTime.now()
            it[languageId] = langId
            it[frequency_analysis] = okuText.isFromFrequencyAnalysis
            it[currentPage] = 1
        }.value
    }
}

fun OkuTextEntity.flagTextsForMetricUpdate(okuwords: List<OkuWord>) = transaction {
    if (okuwords.isEmpty()) return@transaction

    val langId = LanguageEntity.getId(okuwords.first().language)
    val okuTextsToUpdate =
        select(OkuTextEntity.id, okuWordSet).where { languageId eq langId and (updateMetrics eq false) }
            .toList()
            .filter {
                it[okuWordSet]?.let { Json.decodeFromString<Set<Long>>(it) }
                    ?.any { it in okuwords.map { it.id } } == true
            }
            .map { it[OkuTextEntity.id].value }

    update(where = { OkuTextEntity.id inList okuTextsToUpdate }) {
        it[updateMetrics] = true
    }
}

/**
 * Get Okutexts
 */
fun OkuTextEntity.getOkuTexts(ids: List<Long>): List<OkuText> =
    transaction {
        join(LanguageEntity, JoinType.INNER, onColumn = languageId, otherColumn = LanguageEntity.id)
            .selectAll().where { OkuTextEntity.id inList ids }
            .map(::OkuText)
    }

fun OkuTextEntity.updateWordMetrics(okuText: OkuText) = transaction {
    if (okuText.id != null) {
        update(where = { OkuTextEntity.id eq okuText.id }) {
            it[uniqueWordsCount] = okuText.uniqueWordsCount
            it[percentageKnown] = okuText.percentageKnown
            it[totalWordsCount] = okuText.totalWordsCount
            it[updateMetrics] = false
        }
    }
}

fun OkuTextEntity.updateCurrentPage(id: Long?, page: Int) = transaction {
    if (id != null) {
        update(where = { OkuTextEntity.id eq id }) {
            it[currentPage] = page
        }
    }
}

fun OkuTextEntity.deleteText(okuTextId: Long) = transaction {
    deleteWhere { id eq okuTextId }
}
