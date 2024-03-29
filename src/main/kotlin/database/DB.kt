package database

import org.jetbrains.exposed.sql.Database

object DbSettings {
    val db by lazy {
        Database.connect("jdbc:sqlite:data.db?foreign_keys=on", "org.sqlite.JDBC")
    }
}