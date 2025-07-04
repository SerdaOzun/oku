package com.okuread.screens.stats.wordslistTable.table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.okuread.services.StatsService
import com.okuread.services.StatsWordListData
import com.okuread.ui.components.TableCellWithIcon

enum class WordListHeaders(val label: String, val weight: Float) {
    RANK("Rank", 0.10f),
    WORD("Word", 0.25f),
    DATE_LEARNED("Started", 0.18f),
    DATE_KNOWN("Finished", 0.17f),
    STATUS("Status", 0.15f),
    OCCURRENCES("Count", 0.10f),
    ICONS("", 0.05f), ;
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordListTableHeaders(modifier: Modifier, statsService: StatsService) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        WordListHeaders.entries.forEach { currentHeader ->
            Row(
                modifier = Modifier.fillMaxSize().weight(currentHeader.weight)
                    .onClick {
                        statsService.wordListSorting = statsService.wordListSorting.copy(
                            column = currentHeader,
                            ascending = !statsService.wordListSorting.ascending
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCellWithIcon(
                    text = currentHeader.label,
                    showIcon = currentHeader == statsService.wordListSorting.column,
                    ascending = statsService.wordListSorting.ascending,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

data class WordListSorting(val column: WordListHeaders, val ascending: Boolean)

fun getWordListComparator(
    statsService: StatsService
): Comparator<StatsWordListData> {
    statsService.wordListSorting.run {
        return when (column) {
            WordListHeaders.RANK, WordListHeaders.ICONS -> if (ascending) {
                compareBy<StatsWordListData> { it.frequencyRank == null }.thenBy { it.frequencyRank }
            } else {
                compareBy<StatsWordListData> { it.frequencyRank == null }.thenByDescending { it.frequencyRank }
            }
            WordListHeaders.WORD -> if (ascending) compareBy { it.okuWord.word.lowercase() } else compareByDescending { it.okuWord.word.lowercase() }
            WordListHeaders.DATE_LEARNED -> if (ascending) compareBy { it.okuWord.learningStart } else compareByDescending { it.okuWord.learningStart }
            WordListHeaders.DATE_KNOWN -> if (ascending) compareBy { it.okuWord.learningFinished } else compareByDescending { it.okuWord.learningFinished }
            WordListHeaders.STATUS -> if (ascending) compareBy { it.okuWord.status.label } else compareByDescending { it.okuWord.status.label }
            WordListHeaders.OCCURRENCES -> if (ascending) compareBy { it.okuWord.occurrenceCount } else compareByDescending { it.okuWord.occurrenceCount }
        }
    }
}
