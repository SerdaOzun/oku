package com.okuread.db.repositories

import com.google.gson.Gson
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import com.okuread.db.data.DictionaryEntry
import com.okuread.db.data.DictionaryInsert
import com.okuread.db.util.OkuLanguage
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.StringReader

/**
 * DB TABLE
 */
object Dictionary : Table("Dictionary") {
    val word: Column<String> = text("word")
    val languageId: Column<Int> = integer("language_id").references(LanguageEntity.id)
    val json: Column<String> = text("json_data")
    val dialect: Column<String> = text("dialect")
}

/**
 * Currently only required to use when selecting words
 * TODO I need to have a single source of truth for which language maps to which language
 */
private fun getRightDictionaryLanguage(okuLanguage: OkuLanguage): OkuLanguage = when(okuLanguage) {
    OkuLanguage.BOSNIAN, OkuLanguage.SERBIAN, OkuLanguage.MONTENEGRIN, OkuLanguage.CROATIAN -> OkuLanguage.SERBIAN
    OkuLanguage.MOLDOVAN, OkuLanguage.ROMANIAN -> OkuLanguage.ROMANIAN
    else -> okuLanguage
}

fun Dictionary.selectWord(okuLanguage: OkuLanguage, word: String): List<DictionaryEntry> = transaction {
    val gson = Gson()
    val langId = LanguageEntity.getId(getRightDictionaryLanguage(okuLanguage))
    select(json, dialect).where { languageId eq langId and (Dictionary.word.lowerCase() eq word.lowercase()) }
        .toList().map {
            val reader = JsonReader(StringReader(it[json])).apply { strictness = Strictness.LENIENT }
            val entry = gson.fromJson<DictionaryEntry>(reader, DictionaryEntry::class.java)
            entry.copy(okuDialect = it[dialect])
        }
}

fun Dictionary.selectWords(okuLanguage: OkuLanguage, words: List<String>): List<DictionaryEntry> = transaction {
    val gson = Gson()
    val langId = LanguageEntity.getId(getRightDictionaryLanguage(okuLanguage))
    select(json, dialect).where { languageId eq langId and (word.lowerCase() inList words) }
        .toList().map {
            val reader = JsonReader(StringReader(it[json])).apply { strictness = Strictness.LENIENT }
            val entry = gson.fromJson<DictionaryEntry>(reader, DictionaryEntry::class.java)
            entry.copy(okuDialect = it[dialect])
        }
}

fun Dictionary.insertData(okuLanguage: OkuLanguage, data: List<DictionaryInsert>) = transaction {
    val langId = LanguageEntity.getId(okuLanguage)

    batchInsert(data) { row ->
        this[word] = row.word
        this[languageId] = langId
        this[json] = row.json
        this[dialect] = row.dialect
    }
}

fun Dictionary.delete(okuLanguage: OkuLanguage) = transaction {
    val langId = LanguageEntity.getId(okuLanguage)
    transaction {
        deleteWhere { languageId eq langId }
    }
}
