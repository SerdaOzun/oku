package com.okuread.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.okuread.db.data.OkuText
import com.okuread.db.data.OkuTextListItem
import com.okuread.db.data.OkuWord
import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import com.okuread.db.util.WordStatus
import com.okuread.util.isSkippableWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.exposed.sql.Column
import org.koin.core.component.KoinComponent
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Handles all activity related to loading Okutexts for the homescreen and frequencylist screen
 */
class TextListService : ViewModel(), KoinComponent {

    val maxItemsPerPage = 30
    var numberOfPages by mutableStateOf(1)
    var currentPage: Int by mutableStateOf(1)

    //Reload texts when this value changes
    var triggerTextReload by mutableStateOf(0)

    val okuTexts = mutableStateListOf<OkuTextListItem>()

    fun loadTexts(
        search: String,
        okuLanguage: OkuLanguage?,
        isFromFrequencyAnalysis: Boolean,
        orderByColumn: Column<out Any>,
        orderAscending: Boolean
    ) {
        okuTexts.clear()

        numberOfPages = ceil(OkuTextEntity.getCount(isFromFrequencyAnalysis).toDouble() / maxItemsPerPage).toInt().let {
            if (it == 0) 1 else it
        }

        if (currentPage > numberOfPages) currentPage = numberOfPages

        val offset = when {
            currentPage == 1 -> 0
            else -> {
                val tmpOffset = (currentPage - 1) * maxItemsPerPage
                if (tmpOffset == 0) maxItemsPerPage else tmpOffset
            }
        }

        OkuTextEntity.getTextListItems(
            search = search,
            filteredLanguage = okuLanguage ?: OkuLanguage.ALL,
            isFromFrequencyAnalysis = isFromFrequencyAnalysis,
            orderByColumn = orderByColumn,
            orderAscending = orderAscending,
            limit = maxItemsPerPage,
            offset = offset.toLong(),
        ).let {
            okuTexts.addAll(it)
        }

        updateTextMetrics()
    }

    /**
     * Flag texts containing the given okuwords to recalculate their metrics
     */
    fun flagTextsForMetricUpdate(okuwords: List<OkuWord>) = viewModelScope.launch(Dispatchers.IO) {
        OkuTextEntity.flagTextsForMetricUpdate(okuwords)
    }

    /**
     * Updates visible Texts that need updated word metrics
     */
    private fun updateTextMetrics() = viewModelScope.launch(Dispatchers.IO) {
        var reloadNeeded = false

        okuTexts.filter { it.updateMetrics == true }.mapNotNull { it.id }
            .let { OkuTextEntity.getOkuTexts(it) }
            .forEach { okuText ->
                updateWordMetrics(okuText)
                reloadNeeded = true
            }

        if (reloadNeeded) {
            triggerTextReload++
        }
    }

    private fun updateWordMetrics(okuText: OkuText) {
        //1. Get all okuWords that are present in the text
        //Do not count spaces and line separators as words that have to be learned.
        val uniqueWordsInText = OkuWordEntity.getWordsByFilter(okuText.language) {
            OkuWordEntity.id inList (okuText.okuWordIdSet ?: emptySet())
        }.filterNot { w -> w.isSkippableWord() }

        //2. Calculate the percentage of known words
        val percentageKnownWords = floor(
            uniqueWordsInText.count { uniqueWord ->
                uniqueWord.status == WordStatus.KNOWN || uniqueWord.status == WordStatus.IGNORED
            }.toDouble() / uniqueWordsInText.size * 100
        ).toInt()

        val uniqueWordsSize = uniqueWordsInText.size
        val totalWords = okuText.wordList.filterNot { it.isSkippableWord() }.count()

        val updatedOkuText = okuText.copy(
            uniqueWordsCount = uniqueWordsSize,
            percentageKnown = percentageKnownWords,
            totalWordsCount = totalWords
        )

        OkuTextEntity.updateWordMetrics(updatedOkuText)
    }

    fun deleteText(okuText: OkuTextListItem) {
        okuText.id?.let { id ->
            okuTexts.removeIf { it.id == okuText.id }
            val wordList = OkuTextEntity.getText(okuText.id)!!.wordList
            OkuTextEntity.deleteText(id)
            OkuWordEntity.removeWordOccurrences(wordList, okuText.language)
            triggerTextReload++
        }
    }
}