package com.okuread.db.repositories

import com.okuread.db.util.OkuLanguage
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.time.LocalDate

/**
 * DB TABLE
 */
object InstalledDictionaries : LongIdTable("InstalledDictionaries") {
    val languageId: Column<Int> = integer("language_id").references(LanguageEntity.id)
    val updatedAt: Column<String> = text("updated_at")
}

fun InstalledDictionaries.insertDictionary(okuLanguage: OkuLanguage) = transaction {
    upsert {
        val langId = LanguageEntity.getId(okuLanguage)

        it[languageId] = langId
        it[updatedAt] = LocalDate.now().toString()
    }
}

fun InstalledDictionaries.getInstalledDictionaries(): List<Pair<OkuLanguage, LocalDate>> = transaction {
    join(
        otherTable = LanguageEntity,
        joinType = JoinType.INNER,
        onColumn = languageId,
        otherColumn = LanguageEntity.id
    ).select(languageId, updatedAt, LanguageEntity.language).withDistinct().map {
        Pair(
            OkuLanguage.valueByLabel(it[LanguageEntity.language]),
            LocalDate.parse(it[updatedAt])
        )
    }
}

fun InstalledDictionaries.isInstalled(okuLanguage: OkuLanguage): Boolean = transaction {
    join(
        otherTable = LanguageEntity,
        joinType = JoinType.INNER,
        onColumn = languageId,
        otherColumn = LanguageEntity.id
    ).select(languageId).withDistinct().where { LanguageEntity.language eq okuLanguage.label }.empty().not()
}

fun InstalledDictionaries.delete(okuLanguage: OkuLanguage) = transaction {
    val langId = LanguageEntity.getId(okuLanguage)
    transaction {
        deleteWhere { languageId eq langId }
    }
}
