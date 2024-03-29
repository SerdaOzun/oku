package database

import database.util.WordStatus
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

/**
 * DB TABLE
 */
object OkuWordEntity : LongIdTable() {
    val word: Column<String> = text("word")
    val wordstatus: Column<Int> = integer("wordstatus")
    val languageId: Column<Int> = integer("language_id").references(LanguageEntity.id)
}

data class OkuWord(
    val id: Long?,
    val word: String,
    val status: WordStatus,
    val languageId: Int
) {
    constructor(resultRow: ResultRow) : this(
        id = resultRow[OkuWordEntity.id].value,
        word = resultRow[OkuWordEntity.word],
        status = WordStatus.byStatuscode(resultRow[OkuWordEntity.wordstatus]),
        languageId = resultRow[OkuWordEntity.languageId]
    )
}


fun OkuWordEntity.getWordByValue(word: String): OkuWord = transaction {
    OkuWordEntity.selectAll().where { OkuWordEntity.word eq word }.single().let(::OkuWord)
}

fun OkuWordEntity.insert(okuWord: OkuWord) = transaction {
    OkuWordEntity.upsert {
        it[word] = okuWord.word
        it[wordstatus] = okuWord.status.statuscode
    }
}




