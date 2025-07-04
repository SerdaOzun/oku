package com.okuread

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.gabrieldrn.carbon.CarbonDesignSystem
import com.gabrieldrn.carbon.foundation.color.Gray100Theme
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.db.repositories.*
import com.okuread.state.GlobalState
import kotlinx.coroutines.delay
import moe.tlaster.precompose.PreComposeApp
import org.flywaydb.core.Flyway
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory
import java.awt.Dimension

const val isDemo = false
const val version = "1.5.0"
val loggerRoot = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger


fun main() {
    initializeApp()

    application {
        LaunchedEffect(GlobalState.windowState.size) {
            delay(100)
            SettingsEntity.upsert(OkuSetting.WindowSize(GlobalState.windowState.size))
        }

        Window(
            onCloseRequest = ::exitApplication,
            state = GlobalState.windowState,
            title = "Oku"
        ) {
            window.minimumSize = Dimension(960, 800)
            PreComposeApp {
                CarbonDesignSystem(theme = WhiteTheme, uiShellInlineTheme = Gray100Theme) {
                    App()
                }
            }
        }
    }
}


private fun initializeApp() {
    loggerRoot.level = Level.INFO
    loggerRoot.info("Starting application")

    DbSettings.db // initialize exposed db connection
    if (isDemo) {
        startKoin { modules(appModule) }
        runMigrations()
//        deleteDatabase()
        populateDatabaseWithTestData()
    } else {
        startKoin { modules(appModule) }
        loggerRoot.info("Koin modules loaded")
        runMigrations() // flyway
    }
    loggerRoot.info("Database migration complete")
}

private fun runMigrations() {
    val flyway = Flyway.configure().dataSource("jdbc:sqlite:${getDatabaseDirectory()}?foreign_keys=on", "", "")
        .baselineOnMigrate(true)
        .load()

    flyway.migrate()
}