package com.okuread.db.repositories

import androidx.compose.ui.unit.DpSize
import com.okuread.db.util.OkuLanguage
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

/**
 * DB TABLE
 */
object SettingsEntity : Table() {
    val key: Column<String> = text("key").uniqueIndex()
    val value: Column<String> = text("value")
}

sealed interface OkuSetting {
    data class DefaultLanguage(val okuLanguage: OkuLanguage) : OkuSetting
    data class DefaultFontSize(val fontSize: Float) : OkuSetting
    data class LicenseStatus(val activated: Boolean) : OkuSetting
    data class LicensedEmail(val email: String) : OkuSetting
    data class LicenseKey(val licenseKey: String) : OkuSetting
    data class WindowSize(val size: DpSize) : OkuSetting
}

inline fun <reified T> SettingsEntity.getSetting(): String? = transaction {
    SettingsEntity.select(value).where {
        key eq T::class.simpleName!!
    }.singleOrNull()?.let { it[value] }
}

fun SettingsEntity.upsert(okuSetting: OkuSetting) = transaction {
    upsert {
        it[key] = okuSetting::class.simpleName!!
        it[value] = when (okuSetting) {
            is OkuSetting.DefaultLanguage -> okuSetting.okuLanguage.label
            is OkuSetting.DefaultFontSize -> okuSetting.fontSize.toString()
            is OkuSetting.LicensedEmail -> okuSetting.email
            is OkuSetting.LicenseStatus -> okuSetting.activated.toString()
            is OkuSetting.LicenseKey -> okuSetting.licenseKey
            is OkuSetting.WindowSize -> okuSetting.size.let { "${it.width.value};${it.height.value}" }
        }
    }
}

