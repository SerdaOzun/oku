package com.okuread.screens.stats.wordslistTable

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.db.util.WordStatus
import com.okuread.screens.stats.StatsSubScreen
import com.okuread.screens.stats.wordslistTable.table.WordListTableHeaders
import com.okuread.screens.stats.wordslistTable.table.getWordListComparator
import com.okuread.screens.stats.wordslistTable.table.tableToolbar
import com.okuread.screens.stats.wordslistTable.table.wordListTable
import com.okuread.services.StatsService
import com.okuread.services.StatsWordListData
import com.okuread.ui.theme.spacing
import com.okuread.util.isSkippableWord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

@Composable
fun WordsListView(
    statsService: StatsService
) {
    var searchFilter by remember { mutableStateOf("") }
    var filteredWordList by remember { mutableStateOf(emptyList<StatsWordListData>()) }
    var changedLanguage by remember { mutableStateOf(0) }
    val state = rememberLazyListState()

    LaunchedEffect(statsService.statsState.filteredLanguage) {
        delay(25)
        statsService.refreshWordList()
        changedLanguage++
    }

    LaunchedEffect(statsService.statsState.subScreen, changedLanguage) {
        filteredWordList = fetchWordList(statsService, searchFilter)
        state.animateScrollToItem(0)
    }

    LaunchedEffect(
        statsService.wordListSorting,
        statsService.wordList,
        searchFilter
    ) {
        filteredWordList = fetchWordList(statsService, searchFilter)
    }

    Column(modifier = Modifier.fillMaxSize().padding(start = MaterialTheme.spacing.medium)) {

        tableToolbar(
            modifier = Modifier.weight(0.1f),
            searchFilter = searchFilter,
            setSearchFilter = { searchFilter = it }
        )

        WordListTableHeaders(
            modifier = Modifier.weight(0.05f).fillMaxWidth()
                .padding(end = MaterialTheme.spacing.medium)
                .background(WhiteTheme.layerAccent01)
                .padding(MaterialTheme.spacing.small),
            statsService
        )

        Box(modifier = Modifier.weight(0.85f).fillMaxWidth()) {
            if (filteredWordList.isNotEmpty()) {
                wordListTable(filteredWordList, state, statsService.statsState.filteredLanguage!!)
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

private fun List<StatsWordListData>.filterWords(searchFilter: String) =
    filterNot { w -> w.okuWord.word.isSkippableWord() }
        .filter { it.okuWord.word.lowercase().contains(searchFilter) }


private suspend fun CoroutineScope.fetchWordList(
    statsService: StatsService,
    searchFilter: String
): List<StatsWordListData> {
    delay(25)
    if (statsService.statsState.filteredLanguage != null) {
        return statsService.wordList
            .filter {
                when (statsService.statsState.subScreen) {
                    StatsSubScreen.WORDS_KNOWN -> it.okuWord.status == WordStatus.KNOWN
                    StatsSubScreen.WORDS_LEARNING -> it.okuWord.status == WordStatus.LEARNING
                    StatsSubScreen.WORDS_IGNORED -> it.okuWord.status == WordStatus.IGNORED
                    else -> true
                }
            }
            .filterWords(searchFilter)
            .sortedWith(getWordListComparator(statsService))
    }

    return emptyList()
}