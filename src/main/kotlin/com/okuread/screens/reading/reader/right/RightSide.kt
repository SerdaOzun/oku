package com.okuread.screens.reading.reader.right

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.db.data.OkuWord
import com.okuread.db.repositories.InstalledDictionaries
import com.okuread.db.repositories.isInstalled
import com.okuread.screens.reading.reader.right.containers.DictionaryContainer
import com.okuread.screens.reading.reader.right.containers.SelectedSentenceContainer
import com.okuread.screens.reading.reader.right.containers.SelectedWordContainer
import com.okuread.screens.reading.reader.right.containers.SettingsContainer
import com.okuread.services.ReadingService
import com.okuread.ui.theme.spacing
import kotlinx.coroutines.delay
import moe.tlaster.precompose.navigation.Navigator

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RightSide(
    modifier: Modifier,
    navigator: Navigator,
    readingService: ReadingService,
    selectedWord: OkuWord?,
    onSelectedWord: (OkuWord) -> Unit,
    selectedSentence: String,
    fontSize: TextUnit,
    onFontChange: (TextUnit) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedWord by remember { mutableStateOf(false) }
    var copiedSentence by remember { mutableStateOf(false) }
    var dictionaryInstalled by remember { mutableStateOf(InstalledDictionaries.isInstalled(readingService.currentOkuText!!.okuText.language)) }

    LaunchedEffect(copiedWord, copiedSentence) {
        delay(2000)
        copiedWord = false
        copiedSentence = false
    }

    Column(
        modifier.padding(MaterialTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (selectedSentence.isNotBlank()) {
            SelectedSentenceContainer(
                Modifier.fillMaxWidth()
                    .border(1.dp, WhiteTheme.borderSubtle00)
                    .padding(start = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small),
                clipboardManager,
                selectedSentence,
                copiedSentence,
                setCopiedSentence = { copiedSentence = it }
            )
        } else {
            SelectedWordContainer(
                modifier = Modifier.fillMaxWidth()
                    .border(1.dp, WhiteTheme.borderSubtle00)
                    .padding(start = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small),
                clipboardManager,
                readingService,
                selectedWord,
                copiedWord,
                setCopiedWord = { copiedWord = it },
                onSelectedWord = onSelectedWord
            )
        }

        DictionaryContainer(
            modifier = Modifier.weight(1f).fillMaxWidth()
                .border(1.dp, WhiteTheme.borderSubtle00),
            selectedWord = selectedWord?.word,
            okuLanguage = selectedWord?.language,
            dictionaryInstalled = dictionaryInstalled,
        )

        SettingsContainer(
            modifier = Modifier.fillMaxWidth(),
            navigator = navigator,
            readingService = readingService,
            fontSize = fontSize,
            onFontChange = onFontChange
        )
    }
}