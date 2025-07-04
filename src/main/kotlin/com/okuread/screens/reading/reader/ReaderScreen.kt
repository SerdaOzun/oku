package com.okuread.screens.reading.reader

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.button.IconButton
import com.okuread.db.data.OkuWord
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.db.repositories.updateCurrentPage
import com.okuread.db.util.LanguageType
import com.okuread.getKoinInstance
import com.okuread.screens.reading.reader.left.ContentLazyColumn
import com.okuread.screens.reading.reader.left.WordItem
import com.okuread.screens.reading.reader.right.RightSide
import com.okuread.services.ReadingService
import com.okuread.services.SettingsService
import com.okuread.state.GlobalState
import com.okuread.ui.theme.spacing
import com.okuread.util.isLineBreak
import kotlinx.coroutines.delay
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ReaderScreen(
    navigator: Navigator,
    readingService: ReadingService = getKoinInstance(),
    settingsVm: SettingsService = getKoinInstance()
) {
    var fontSize by remember { mutableStateOf(settingsVm.defaultFontSize) }
    var selectedWord by remember { mutableStateOf<OkuWord?>(null) }
    var selectedWordIndex by remember { mutableStateOf(-1) }

    var selectedSentence by remember { mutableStateOf<String>("") }
    var rightClickIndex by remember { mutableStateOf(-1) }

    //Ein Satz besteht aus den Wörtern zwischen dem Index von Links und Rechtsklick
    //So wird kein Dragging benötigt, sondern einfach das erste und letzte Word eines Satzes markieren
    LaunchedEffect(selectedWordIndex, rightClickIndex) {
        selectedSentence = ""
        if (selectedWordIndex < rightClickIndex) {
            selectedSentence = readingService.currentOkuText!!.okuText.wordList
                .filterIndexed { index, _ -> index in selectedWordIndex..rightClickIndex }
                .joinToString(separator = "")
        }
    }

    LaunchedEffect(GlobalState.windowState.size) {
        delay(100)
        //change fontsize to trigger redrawing text to fit to the new size of the window
        fontSize = TextUnit(
            value = fontSize.value + 1f,
            TextUnitType.Sp
        )
        delay(1)
        fontSize = TextUnit(
            value = fontSize.value - 1f,
            TextUnitType.Sp
        )
    }

    Row(modifier = Modifier.widthIn(0.dp, 1300.dp).fillMaxSize()) {

        val scrollState = rememberLazyListState()

        val rtlLanguage by remember { mutableStateOf(readingService.currentOkuText!!.okuText.language.languageType == LanguageType.ARABIC) }

        CompositionLocalProvider(LocalLayoutDirection provides if (rtlLanguage) LayoutDirection.Rtl else LayoutDirection.Ltr) {
            //Title + Text
            Column(modifier = Modifier.weight(0.7f).padding(MaterialTheme.spacing.medium)) {
                //Title
                Row(Modifier.weight(0.05f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = readingService.currentOkuText!!.okuText.title,
                        modifier = Modifier.weight(0.05f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                }

                //Text
                Box(modifier = Modifier.weight(0.95f)) {
                    ContentLazyColumn(
                        readingService,
                        scrollState,
                        fontSize,
                        rtlLanguage
                    ) { originalIndex, okuWord, wordOriginal ->
                        when {
                            okuWord.word.isLineBreak() -> {
                                Text(okuWord.word, lineHeight = fontSize)
                            }

                            okuWord.word.isBlank() -> {
                                Text(okuWord.word)
                            }

                            else -> WordItem(
                                originalWord = wordOriginal,
                                word = okuWord,
                                readingService = readingService,
                                fontSize = fontSize,
                                isSelected = originalIndex == selectedWordIndex,
                                wordIndex = originalIndex,
                                rightClickIndex = rightClickIndex,
                                onClick = { word, wordIndex ->
                                    selectedWord = word
                                    selectedWordIndex = wordIndex
                                },
                                onRightClick = { rightClickIndex = it },
                            )
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = scrollState)
                    )
                }

                Pagination(Modifier, readingService)
            }
        }

        //Fontsizechanger + selected word + translation
        RightSide(
            modifier = Modifier.weight(0.3f),
            navigator = navigator,
            readingService = readingService,
            selectedWord = selectedWord,
            onSelectedWord = { selectedWord = it },
            selectedSentence = selectedSentence,
            fontSize = fontSize
        ) {
            fontSize = it
        }
    }
}

@Composable
private fun Pagination(modifier: Modifier, readingService: ReadingService) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            iconPainter = painterResource("icons/minus.svg"),
            isEnabled = readingService.currentPage > 1,
            onClick = {
                readingService.currentPage--
                OkuTextEntity.updateCurrentPage(readingService.currentOkuText?.okuText?.id, readingService.currentPage)
            },
            buttonType = ButtonType.Primary,
        )
        Text(
            text = readingService.currentPage.toString() + "/" + readingService.numberOfPages.toString(),
            fontSize = 18.sp,
            modifier = Modifier.padding(end = MaterialTheme.spacing.small, start = MaterialTheme.spacing.small)
        )
        IconButton(
            iconPainter = painterResource("icons/plus.svg"),
            isEnabled = readingService.currentPage < readingService.numberOfPages,
            onClick = {
                readingService.currentPage++
                OkuTextEntity.updateCurrentPage(readingService.currentOkuText?.okuText?.id, readingService.currentPage)
            },
            buttonType = ButtonType.Primary,
        )
    }
}
