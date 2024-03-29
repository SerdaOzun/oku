import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import database.*
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.rememberNavigator
import navigation.NavigationBar
import navigation.OkuNavHost
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import java.time.LocalDateTime

@Composable
@Preview
fun App() {
    val navigator = rememberNavigator()

    Scaffold(topBar = {
        NavigationBar(navigator)
    }) {
        OkuNavHost(navigator)
    }
}

fun main() = application {
    initializeDatabase()

    Window(onCloseRequest = ::exitApplication) {
        PreComposeApp {
            MaterialTheme {
                App()
            }
        }
    }
}

private fun initializeDatabase() {
    DbSettings.db
    transaction {
        //Create Tables
        SchemaUtils.drop(OkuTextEntity, OkuSentence, OkuWordEntity, LanguageEntity, OkuWord_Sentence)
        SchemaUtils.create(OkuTextEntity, OkuSentence, OkuWordEntity, LanguageEntity, OkuWord_Sentence)

        LanguageEntity.insert {
            it[language] = "English"
        }

        val okuText = OkuText(
            null,
            "Test Title",
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
            LocalDateTime.now(),
            null,
            1
        )
        OkuTextEntity.insertText(okuText)
    }
}