package screens.reading.textlist

import Globals
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import database.OkuText
import database.OkuTextEntity
import database.getAllTexts
import moe.tlaster.precompose.navigation.Navigator
import navigation.Screen
import screens.reading.ReadingViewModel
import ui.theme.spacing

@Composable
fun TextListScreen(navigator: Navigator, readingVM: ReadingViewModel = Globals.readingViewModel) {

    val okuTexts by remember { mutableStateOf(OkuTextEntity.getAllTexts()) }

    Column {
        Row(modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium)) {
            Button(onClick = { navigator.navigate(Screen.CreateTextScreen.name) }) {
                Text("New")
            }
            Text("My Texts")
        }

        LazyColumn {
            items(okuTexts) {
                TextItem(it, navigator, readingVM)
            }
        }
    }


}

@Composable
private fun TextItem(okuText: OkuText, navigator: Navigator, readingVM: ReadingViewModel) {
    Row(modifier = Modifier.fillMaxWidth().clickable {
        readingVM.updateCurrentText(okuText)
        navigator.navigate(Screen.ReaderScreen.name)
    }) {
        Text(okuText.title)
    }
}