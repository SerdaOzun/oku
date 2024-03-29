package database

import database.OkuTextEntity.references
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * DB TABLE
 */
object OkuTextEntity : LongIdTable() {
    val title: Column<String> = varchar("title", 200)
    val body: Column<String> = text("body")
    val timestamp_created: Column<LocalDateTime> = datetime("timestamp_created")
    val timestamp_finished: Column<LocalDateTime?> = datetime("timestamp_finished").nullable()
    val languageId: Column<Int> = integer("language_id").references(LanguageEntity.id)
}

data class OkuText(
    val id: Long?,
    val title: String,
    val body: String,
    val timestampCreated: LocalDateTime,
    val timestampFinished: LocalDateTime?,
    val languageId: Int
) {
    constructor(resultRow: ResultRow) : this(
        id = resultRow[OkuTextEntity.id].value,
        title = resultRow[OkuTextEntity.title],
        body = resultRow[OkuTextEntity.body],
        timestampCreated = resultRow[OkuTextEntity.timestamp_created],
        timestampFinished = resultRow[OkuTextEntity.timestamp_finished],
        languageId = resultRow[OkuTextEntity.languageId]
    )
}

fun OkuTextEntity.getAllTexts(): List<OkuText> = transaction {
    OkuTextEntity.selectAll().toList().map(::OkuText)
}

fun OkuTextEntity.insertText(okuText: OkuText): Long = transaction {
    OkuTextEntity.insertAndGetId {
        it[title] = okuText.title
        it[body] = okuText.body
        it[timestamp_created] = LocalDateTime.now()
        it[languageId] = okuText.languageId
    }.value
}
