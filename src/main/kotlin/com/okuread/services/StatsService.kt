package com.okuread.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.okuread.db.data.OkuWord
import com.okuread.db.repositories.LanguageEntity
import com.okuread.db.repositories.OkuWordEntity
import com.okuread.db.repositories.getLanguagesBeingLearnedAll
import com.okuread.db.repositories.getWordsByFilter
import com.okuread.db.util.OkuLanguage
import com.okuread.screens.stats.StatsState
import com.okuread.screens.stats.wordslistTable.table.WordListHeaders
import com.okuread.screens.stats.wordslistTable.table.WordListSorting
import org.jetbrains.exposed.sql.Op
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StatsService : KoinComponent {
    private val readingService by inject<ReadingService>()
    private val languagesLearning: List<OkuLanguage> get() = getLanguagesBeingLearned()

    var statsState by mutableStateOf(StatsState(filteredLanguage = languagesLearning.firstOrNull()))
    var wordListSorting by mutableStateOf(WordListSorting(WordListHeaders.RANK, true))

    var wordList by mutableStateOf(listOf<StatsWordListData>())

    fun refreshWordList() {
        if (statsState.filteredLanguage != null) {
            readingService.calculateFrequencyRankingForLanguage(statsState.filteredLanguage!!)

            wordList = OkuWordEntity.getWordsByFilter(statsState.filteredLanguage!!) { Op.TRUE }
                .map { okuWord ->
                    StatsWordListData(
                        frequencyRank = readingService.frequencyRankingMap[okuWord.word.lowercase()],
                        okuWord = okuWord
                    )
                }
        }
    }

    fun getLanguagesBeingLearned() = LanguageEntity.getLanguagesBeingLearnedAll()
}

data class StatsWordListData(
    val frequencyRank: Int?,
    val okuWord: OkuWord,
)