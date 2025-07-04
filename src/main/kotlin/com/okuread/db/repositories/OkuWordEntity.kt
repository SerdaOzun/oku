package com.okuread.db.repositories

import com.okuread.db.data.OkuWord
import com.okuread.db.repositories.OkuWordEntity.join
import com.okuread.db.repositories.OkuWordEntity.languageId
import com.okuread.db.util.OkuLanguage
import com.okuread.db.util.WordStatus
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * DB TABLE
 */
object OkuWordEntity : LongIdTable() {
    val word: Column<String> = text("word")
    val wordstatus: Column<Int> = integer("wordstatus")
    val learning_start: Column<LocalDateTime?> = datetime("learning_start").nullable()
    val learning_finished: Column<LocalDateTime?> = datetime("learning_finished").nullable()
    val languageId: Column<Int> = integer("language_id").references(LanguageEntity.id)
    val occurrence_count: Column<Int> = integer("occurrence_count")
    val ignoreForFrequencyRanking: Column<Boolean> = bool("ignore_for_frequency_ranking").default(false)
}

private val joinedWordLanguageTable = { okuLanguage: OkuLanguage ->
    join(
        otherTable = LanguageEntity,
        joinType = JoinType.INNER,
        onColumn = languageId,
        otherColumn = LanguageEntity.id,
        additionalConstraint = { LanguageEntity.language eq okuLanguage.label })
}

fun OkuWordEntity.getWordsByFilter(
    okuLanguage: OkuLanguage,
    filter: SqlExpressionBuilder.() -> Op<Boolean> = { Op.TRUE }
): List<OkuWord> = transaction {
    val sqlTable = join(
        otherTable = LanguageEntity,
        joinType = JoinType.INNER,
        onColumn = languageId,
        otherColumn = LanguageEntity.id,
        additionalConstraint = { LanguageEntity.language eq okuLanguage.label }
    )

    sqlTable.selectAll().where(filter).toList().map(::OkuWord)
}


/**
 * Alle Wörter aus der Liste welche noch nicht in der Datenbank sind dort abspeichern
 */
fun OkuWordEntity.saveWords(wordList: List<String>, okuLanguage: OkuLanguage) = transaction {
    val sqlTable = joinedWordLanguageTable(okuLanguage)

    val wordsLowercase = wordList.map { it.lowercase() }

    //Save new words first to the database and do not count occurrence
    (wordsLowercase.distinct() - sqlTable.select(word).where { word.lowerCase() inList wordsLowercase }
        .map { it[word].lowercase() }.toSet())
        .map { OkuWord(null, it, WordStatus.UNKNOWN, null, null, okuLanguage, 0) }
        .let { batchInsertWords(it) }

    //efficiently update occurrenceCount
    val wordsOccurrenceCount = wordList.map { it.lowercase() }.groupingBy { it }.eachCount()

    wordsOccurrenceCount.forEach { (w, occurrenceChange) ->
        updateOccurrenceCount(sqlTable, w, occurrenceChange, okuLanguage)
    }
}

private fun OkuWordEntity.batchInsertWords(wordList: List<OkuWord>) = transaction {
    if (wordList.isEmpty()) return@transaction
    val langId = LanguageEntity.getId(wordList.first().language)

    batchInsert(wordList) {
        this[word] = it.word.lowercase()
        this[wordstatus] = it.status.statuscode
        this[languageId] = langId
        this[occurrence_count] = it.occurrenceCount
    }
}

fun OkuWordEntity.batchUpdateWords(okuWords: List<OkuWord>) = transaction {
    val langId = LanguageEntity.getId(okuWords.first().language)

    batchUpsert(okuWords, onUpdateExclude = listOf(OkuWordEntity.id, word, languageId)) { okuWord ->
        this[OkuWordEntity.id] = okuWord.id!!
        this[word] = okuWord.word.lowercase()
        this[wordstatus] = okuWord.status.statuscode
        this[learning_start] = okuWord.learningStart
        this[learning_finished] = okuWord.learningFinished
        this[languageId] = langId
        this[occurrence_count] = okuWord.occurrenceCount
    }
}

fun OkuWordEntity.updateWord(okuWord: OkuWord) = transaction {
    OkuWordEntity.upsert(onUpdateExclude = listOf(word, languageId), where = { OkuWordEntity.id eq okuWord.id }) {
        val langId = LanguageEntity.getId(okuWord.language)

        it[id] = okuWord.id!!
        it[word] = okuWord.word.lowercase()
        it[wordstatus] = okuWord.status.statuscode
        it[learning_start] = okuWord.learningStart
        it[learning_finished] = okuWord.learningFinished
        it[languageId] = langId
        it[occurrence_count] = okuWord.occurrenceCount
    }
}

/**
 * Wenn ein Text gelöscht wird, dann auch die occurrences der Wörter löschen.
 */
fun OkuWordEntity.removeWordOccurrences(wordList: List<String>, okuLanguage: OkuLanguage) = transaction {
    val sqlTable = joinedWordLanguageTable(okuLanguage)

    val wordsLowercase = wordList.map { it.lowercase() }.groupingBy { it }.eachCount()

    wordsLowercase.forEach { (word, occurrence) ->
        updateOccurrenceCount(sqlTable, word, occurrence.unaryMinus(), okuLanguage)
    }
}

private fun OkuWordEntity.updateOccurrenceCount(
    sqlTable: Join,
    w: String,
    occurrenceCountChange: Int,
    okuLanguage: OkuLanguage
) {
    val langId = LanguageEntity.getId(okuLanguage)

    //1. Get OkuWord from db
    //lowercase prüfen, weil zu oft z.B. satzanfang und ähnliches sowieso das gleiche wort gemeint ist. Wörter wie im Deutschen, wo es mal
    //ein Wort mit groß und kleinschreibung mit unterschiedlicher Bedeutung geben kann sind selten
    val okuWord = sqlTable.select(id, occurrence_count).where { languageId eq langId and (word.lowerCase() eq w) }
        .singleOrNull()

    if (okuWord == null) return

    //2. Get its id and current occurrence count
    val (id, oldOccurrenceCount) = okuWord.let { Pair(it[id], it[occurrence_count]) }
    val newOccurenceCount = oldOccurrenceCount.plus(occurrenceCountChange)

    //3. Only update occurrence count as long as it still 0 or higher
    if (newOccurenceCount >= 0) {
        update(where = { OkuWordEntity.id eq id }) {
            it[occurrence_count] = oldOccurrenceCount.plus(occurrenceCountChange)
        }
    }
}

/**
 * @param okuWord
 * @param consider whether the word should be used for the frequency ranking
 */
fun OkuWordEntity.updateFrequencyRankingConsideration(okuWord: OkuWord, consider: Boolean) = transaction {
    OkuWordEntity.update(where = { OkuWordEntity.id eq okuWord.id }) {
        it[ignoreForFrequencyRanking] = consider
    }
}