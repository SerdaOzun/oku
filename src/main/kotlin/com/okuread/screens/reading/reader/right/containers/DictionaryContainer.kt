package com.okuread.screens.reading.reader.right.containers

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonSize
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.gabrieldrn.carbon.textinput.TextInput
import com.okuread.db.data.DictionaryEntry
import com.okuread.db.util.OkuLanguage
import com.okuread.getKoinInstance
import com.okuread.services.DictionaryService
import com.okuread.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DictionaryContainer(
    modifier: Modifier,
    dictService: DictionaryService = getKoinInstance<DictionaryService>(),
    selectedWord: String?,
    okuLanguage: OkuLanguage?,
    dictionaryInstalled: Boolean,
    blackBorder: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()

    var dictionaryEntry by remember { mutableStateOf(listOf<DictionaryEntry>()) }
    var searchedWord by remember { mutableStateOf<String?>(null) }
    var searchedNewWord by remember { mutableStateOf(0) }

    LaunchedEffect(selectedWord) {
        searchedWord = selectedWord
        if (searchedWord != null && okuLanguage != null) {
            dictionaryEntry = dictService.getDictionaryEntry(okuLanguage, searchedWord!!)
        }
    }

    LaunchedEffect(searchedNewWord) {
        delay(50)
        if (searchedWord != null && okuLanguage != null) {
            dictionaryEntry = dictService.getDictionaryEntry(okuLanguage, searchedWord!!)
        }
    }

    Column(
        modifier.border(2.dp, if (blackBorder) WhiteTheme.layer01 else Color.Transparent)
            .padding(start = MaterialTheme.spacing.smallest, end = MaterialTheme.spacing.smallest),
        horizontalAlignment = Alignment.Start
    ) {
        val state = rememberLazyListState()

        if (dictionaryInstalled) {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small).fillMaxSize(),
                    state = state
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextInput(
                                label = "Dictionary Search",
                                value = searchedWord ?: "",
                                onValueChange = {
                                    searchedWord = it
                                    searchedNewWord++
                                },
                                modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                            )
                        }
                    }

                    if (dictionaryEntry.isNotEmpty()) {
                        itemsIndexed(dictionaryEntry) { index, dictEntry ->
                            DictionaryItem(
                                selectedWord = dictEntry.word,
                                dictService = dictService,
                                okuLanguage = okuLanguage,
                                index = index,
                                dictEntry = dictEntry
                            ) { clickedWord ->
                                dictionaryEntry = clickedWord.first
                                searchedWord = clickedWord.second
                                coroutineScope.launch {
                                    state.animateScrollToItem(0)
                                }
                            }
                        }
                    } else {
                        item {
                            Text("No dictionary entry found", textAlign = TextAlign.Center)
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        } else {
            Text(
                "Please install the dictionary for $okuLanguage from the Plugins tab.",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
private fun DictionaryItem(
    selectedWord: String?,
    dictService: DictionaryService,
    okuLanguage: OkuLanguage?,
    index: Int,
    dictEntry: DictionaryEntry,
    onClick: (Pair<List<DictionaryEntry>, String>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(bottom = MaterialTheme.spacing.small).background(WhiteTheme.layer01)
    ) {
        Text(
            "${index + 1} ${dictEntry.pos}" +
                    if (!dictEntry.okuDialect.isNullOrBlank()) " (${dictEntry.okuDialect})" else "",
            fontWeight = FontWeight.Bold, fontSize = 16.sp
        )

        if (dictEntry.meanings.any { !it.isNullOrEmpty() }) {
            Text("Meanings: ", fontWeight = FontWeight.Bold)
        }
        dictEntry.meanings.mapNotNull { it }.forEachIndexed { index, meaning ->
            Text(
                "- " + meaning.filterNot { it.isNullOrBlank() }.joinToString(", "),
                modifier = Modifier.padding(start = MaterialTheme.spacing.small)
            )
        }

        if (dictEntry.sentences.isNotEmpty()) {
            Text("Example Sentences: ", fontWeight = FontWeight.Bold)
            dictEntry.sentences.filter { it.text != null }.forEachIndexed { index, sentence ->
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.small)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ex: ",
                            modifier = Modifier.weight(0.12f).padding(start = MaterialTheme.spacing.small),
                            fontStyle = FontStyle.Italic
                        )
                        Text(
                            text = sentence.text!!,
                            modifier = Modifier.weight(0.88f).padding(start = MaterialTheme.spacing.small)
                        )
                    }
                    sentence.translation?.let {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "tr: ",
                                modifier = Modifier.weight(0.12f).padding(start = MaterialTheme.spacing.small),
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                text = it,
                                modifier = Modifier.weight(0.88f).padding(start = MaterialTheme.spacing.small)
                            )
                        }
                    }
                }
            }
        }

        if (okuLanguage != null) {
            val relatedTerms = dictEntry.links.mapNotNull { it }.distinct()
                .filterNot { it.lowercase() == selectedWord?.lowercase() }
                .map { it to dictService.getDictionaryEntry(okuLanguage, it) }
                .filter { it.second.isNotEmpty() }


            if (relatedTerms.isNotEmpty()) {
                Text("Possibly related: ", fontWeight = FontWeight.Bold)
                relatedTerms.forEach { (link, newDictEntry) ->
                    if (link.isNotBlank()) {
                        Button(
                            link, buttonType = ButtonType.Ghost, buttonSize = ButtonSize.Small,
                            onClick = {
                                onClick(Pair(newDictEntry, link))
                            }
                        )
                    }
                }
            }
        }
    }
}
