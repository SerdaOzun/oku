package com.okuread.db.repositories

import com.okuread.db.util.OkuLanguage
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * DB TABLE
 */
object LanguageEntity : IntIdTable() {
    val language: Column<String> = varchar("language", 25).uniqueIndex()
}

private val languageIdCache = mutableMapOf<OkuLanguage, Int>()

/**
 * Important: Only to be used within transaction block
 */
fun LanguageEntity.getId(language: OkuLanguage): Int {
    return languageIdCache[language] ?: LanguageEntity
        .select(id)
        .where { LanguageEntity.language eq language.label }
        .single()
        .let { it[id] }.value
        .also {
            languageIdCache[language] = it
        }
}

fun LanguageEntity.getLanguagesBeingLearned(isFrequencyAnalysis: Boolean): List<OkuLanguage> = transaction {
    join(OkuTextEntity, JoinType.INNER, onColumn = LanguageEntity.id, otherColumn = OkuTextEntity.languageId)
        .selectAll()
        .where { OkuTextEntity.frequency_analysis eq isFrequencyAnalysis }
        .map { OkuLanguage.valueByLabel(it[language]) }.distinct()
}

/**
 * Includes reading + frequencu texts
 */
fun LanguageEntity.getLanguagesBeingLearnedAll(): List<OkuLanguage> = transaction {
    join(OkuTextEntity, JoinType.INNER, onColumn = LanguageEntity.id, otherColumn = OkuTextEntity.languageId)
        .selectAll()
        .toList()
        .map { OkuLanguage.valueByLabel(it[language]) }.distinct()
}

fun LanguageEntity.getOkuLanguagesForIds(ids: List<Int>): List<OkuLanguage> = transaction {
    selectAll().where { LanguageEntity.id inList ids }.map { OkuLanguage.valueByLabel(it[language]) }.distinct()
}
