package database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * DB TABLE
 */
object LanguageEntity : IntIdTable() {
    val language: Column<String> = varchar("language", 25).uniqueIndex()
}