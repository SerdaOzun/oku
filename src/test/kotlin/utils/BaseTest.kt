package utils

import com.okuread.appModule
import com.okuread.db.repositories.*
import com.okuread.runMigrations
import okhttp3.OkHttpClient
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

@Test
open class BaseTest : KoinComponent {
    val client by lazy { OkHttpClient() }

    @BeforeClass
    fun beforeClass() {
        isTesting = true
        DbSettings.db // initialize exposed db connection
        runMigrations()
        startKoin { modules(appModule) }
    }

    @AfterClass(alwaysRun = true)
    open fun afterClass() {
        stopKoin()
        transaction {
            OkuTextEntity.deleteAll()
            OkuWordEntity.deleteAll()
            SettingsEntity.deleteAll()
        }
    }

}