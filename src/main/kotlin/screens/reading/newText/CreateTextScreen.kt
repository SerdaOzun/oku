package screens.reading.newText

import Globals
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import database.OkuText
import database.OkuTextEntity
import moe.tlaster.precompose.navigation.Navigator
import navigation.Screen
import screens.reading.ReadingViewModel
import java.time.LocalDateTime

@Composable
fun CreateTextScreen(navigator: Navigator, readingViewModel: ReadingViewModel = Globals.readingViewModel) {
    var title: String by remember { mutableStateOf("") }
    var body: String by remember { mutableStateOf("") }

    Column {
        TextField(value = title, onValueChange = { title = it })
        TextField(value = body, onValueChange = { body = it })

        Button(onClick = {
            val okuText = OkuText(null, title, body, LocalDateTime.now(), null, 1)
            readingViewModel.insertText(okuText)
            navigator.navigate(Screen.TextListScreen.name)
        }) {
            Text("Save")
        }
    }

}