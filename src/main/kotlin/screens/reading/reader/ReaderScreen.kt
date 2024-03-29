package screens.reading.reader

import Globals
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import database.OkuWord
import database.util.WordStatus
import moe.tlaster.precompose.navigation.Navigator
import screens.reading.ReadingViewModel
import textprocessing.processOkuText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReaderScreen(navigator: Navigator, readingViewModel: ReadingViewModel = Globals.readingViewModel) {

    FlowRow {
        processOkuText(readingViewModel.currentOkuText!!).forEach {
            WordItem(OkuWord(null, it, WordStatus.UNKNOWN, 1))
        }
    }

}

@Composable
private fun WordItem(word: OkuWord) {
    var color by remember { mutableStateOf(Color.Transparent) }
    ClickableText(AnnotatedString(word.word), modifier = Modifier.background(color), onClick = {
        color = Color.DarkGray
    })
}