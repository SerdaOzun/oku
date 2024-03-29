package database

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * DB TABLE
 */
object OkuSentence: LongIdTable() {
    val sentence: Column<String> = text("sentence")
    val text_id: Column<Long?> = long("text_id").references(OkuTextEntity.id, onDelete = ReferenceOption.SET_NULL).nullable()
}