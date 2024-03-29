package database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/**
 * DB TABLE
 */
object OkuWord_Sentence : Table() {
    val okuWord: Column<Long> = (long("okuWord").references(OkuWordEntity.id))
    val okuSentence: Column<Long> = (long("okuSentence").references(OkuSentence.id))
}