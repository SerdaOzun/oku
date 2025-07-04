@file:OptIn(ExperimentalFoundationApi::class)

package com.okuread.screens.stats.wordslistTable.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.gabrieldrn.carbon.textinput.TextInput
import com.okuread.db.repositories.InstalledDictionaries
import com.okuread.db.repositories.OkuWordEntity
import com.okuread.db.repositories.isInstalled
import com.okuread.db.repositories.updateFrequencyRankingConsideration
import com.okuread.db.util.OkuLanguage
import com.okuread.getKoinInstance
import com.okuread.screens.reading.reader.right.containers.DictionaryContainer
import com.okuread.services.StatsService
import com.okuread.services.StatsWordListData
import com.okuread.ui.components.bottomBorder
import com.okuread.ui.theme.spacing
import com.okuread.util.BasicTooltip

@Composable
fun tableToolbar(
    modifier: Modifier,
    searchFilter: String,
    setSearchFilter: (String) -> Unit
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max).fillMaxWidth().padding(bottom = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.Start,
    ) {
        TextInput(
            label = "Search",
            value = searchFilter,
            onValueChange = setSearchFilter,
            modifier = Modifier.padding(end = MaterialTheme.spacing.small).width(300.dp)
        )
    }
}

@Composable
fun wordListTable(
    filteredWordList: List<StatsWordListData>,
    state: LazyListState,
    okuLanguage: OkuLanguage
) {
    var dictionaryInstalled by remember { mutableStateOf(InstalledDictionaries.isInstalled(okuLanguage)) }
    var selectedWord by remember { mutableStateOf("") }

    LaunchedEffect(okuLanguage) {
        selectedWord = ""
    }

    LazyColumn(Modifier.fillMaxSize().padding(end = MaterialTheme.spacing.medium), state = state) {
        itemsIndexed(
            items = filteredWordList,
            key = { _, statsWord -> statsWord.hashCode() }) { _, statsWord ->
            wordListItem(
                statsWord,
                okuLanguage,
                dictionaryInstalled,
                isSelected = (selectedWord.isNotBlank() && selectedWord == statsWord.okuWord.word)
            ) {
                selectedWord = statsWord.okuWord.word
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun wordListItem(
    statsWord: StatsWordListData,
    okuLanguage: OkuLanguage,
    dictionaryInstalled: Boolean,
    isSelected: Boolean,
    statsService: StatsService = getKoinInstance<StatsService>(),
    onClick: (StatsWordListData) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .onClick { onClick(statsWord) }
                .background(WhiteTheme.layer01)
                .bottomBorder(1.dp, WhiteTheme.borderSubtle00)
                .padding(
                    top = MaterialTheme.spacing.small,
                    start = MaterialTheme.spacing.small,
                    bottom = MaterialTheme.spacing.small
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Frequency rank
            Text(
                modifier = Modifier.weight(WordListHeaders.RANK.weight),
                text = statsWord.frequencyRank?.toString() ?: "",
            )
            //Word
            Row(modifier = Modifier.weight(WordListHeaders.WORD.weight)) {
                SelectionContainer { Text(text = statsWord.okuWord.word) }
            }
            //Date started learning
            Text(
                modifier = Modifier.weight(WordListHeaders.DATE_LEARNED.weight),
                text = statsWord.okuWord.learningStart?.toLocalDate()?.toString() ?: ""
            )
            //Date finished learning
            Text(
                modifier = Modifier.weight(WordListHeaders.DATE_KNOWN.weight),
                text = statsWord.okuWord.learningFinished?.toLocalDate()?.toString() ?: ""
            )
            //Status
            Text(modifier = Modifier.weight(WordListHeaders.STATUS.weight), text = statsWord.okuWord.status.label)
            //Occurrence Count
            Text(
                modifier = Modifier.weight(WordListHeaders.OCCURRENCES.weight),
                text = statsWord.okuWord.occurrenceCount.toString()
            )
            //Icon buttons
            Row(modifier = Modifier.weight(WordListHeaders.ICONS.weight)) {
                val ignored = statsWord.okuWord.ignoreForFrequencyRanking
                val tooltipMessage = if (ignored) {
                    "Consider word again for frequency ranking"
                } else {
                    "Ignore word for frequency ranking"
                }

                BasicTooltip(tooltipMessage) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                        IconButton(
                            modifier = Modifier.size(24.dp),
                            onClick = {
                                OkuWordEntity.updateFrequencyRankingConsideration(statsWord.okuWord, !ignored)
                                statsService.refreshWordList()
                            }) { Icon(if (!ignored) Icons.Filled.Clear else Icons.Filled.Add, "") }
                    }
                }
            }
        }
    }

    if (isSelected) {
        DictionaryContainer(
            modifier = Modifier.height(300.dp).fillMaxWidth()
                .border(1.dp, WhiteTheme.borderSubtle00),
            selectedWord = statsWord.okuWord.word,
            okuLanguage = okuLanguage,
            dictionaryInstalled = dictionaryInstalled,
            blackBorder = true
        )
    }
}
