package com.okuread.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.okuread.db.data.OkuText
import com.okuread.db.data.OkuTextListItem
import com.okuread.db.data.OkuWord
import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import com.okuread.db.util.WordStatus
import com.okuread.util.isLineBreak
import com.okuread.util.isSkippableWord
import moe.tlaster.precompose.viewmodel.ViewModel
import org.jetbrains.exposed.sql.lowerCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime

/**
 * Handles all tasks related to a single text. Be it inserting, updating or reading one
 */
class ReadingService : ViewModel(), KoinComponent {

    private val textListService: TextListService by inject()

    /**
     * =============================================================================================
     * ======================================= Current OkuText =====================================
     * =============================================================================================
     */

    var currentOkuText: CurrentOkuText? by mutableStateOf(null)

    //Each list is one line of the text containing the words for that line
    var okuWordsSublist by mutableStateOf(emptyList<List<Pair<String, Int>>>())

    val wordsPerPage = 1000
    var currentPage: Int by mutableStateOf(1)
    var numberOfPages: Int by mutableStateOf(1)

    //lines to display on the current page
    var lineOffset: IntRange by mutableStateOf(0..1)

    /**
     * How much width space (in dp) a word takes up
     * key = word, value = width in dp (considers fontsize and upper/lowercase)
     */
    var wordDpMap: MutableMap<String, Dp> by mutableStateOf(mutableMapOf())

    /**
     * Holds the word frequency ranking map for a selected language
     * key = word in lowercase, value = frequency rank (can be the same for multiple words)
     */
    var frequencyRankingMap: MutableMap<String, Int> by mutableStateOf(mutableMapOf())

    //size of frequency rank for the given text langauge
    var numberOfFrequencyRanks by mutableStateOf(0)

    /**
     * =============================================================================================
     * ========================================== FUNCTIONS ========================================
     * =============================================================================================
     */

    fun getOkuTextById(id: Long): OkuText? = OkuTextEntity.getText(id)

    /**
     * Gets dictionary entries grouped by dialect
     */
    fun getDictionaryEntry(okuWord: OkuWord) = Dictionary.selectWord(okuWord.language, okuWord.word)
    fun getDictionaryEntry(okuLanguage: OkuLanguage, word: String) = Dictionary.selectWord(okuLanguage, word)
    fun getDictionaryEntries(okuLanguage: OkuLanguage, words: List<String>) = Dictionary.selectWords(okuLanguage, words)

    fun calculateFrequencyRankingForLanguage(language: OkuLanguage) {
        frequencyRankingMap.clear()

        var rank = 1
        OkuWordEntity.getWordsByFilter(okuLanguage = language)
            .filterNot { w -> w.isSkippableWord() || w.ignoreForFrequencyRanking == true }
            .sortedByDescending { it.occurrenceCount }
            .groupBy { it.occurrenceCount }
            .map {
                it.value.forEach { word ->
                    frequencyRankingMap[word.word.lowercase()] = rank
                }
                rank++
            }
        numberOfFrequencyRanks = frequencyRankingMap.values.max()
    }

    /**
     * Diese Funktion
     * 1. Speichert den Text in der Datenbank
     * 2. Zerlegt den Text in Wörter und speichert diese in der Datenbank
     */
    fun insertText(okuText: OkuText, okutextOld: OkuText? = null): Long {
        //Remove words from the previous version of the text, if exists
        okutextOld?.let {
            OkuWordEntity.removeWordOccurrences(it.wordList, it.language)
        }

        //reinsert the text
        val newId = OkuTextEntity.insertText(okuText)
        val okuTextWithId = okuText.copy(id = newId)

        OkuWordEntity.saveWords(
            wordList = okuTextWithId.wordList,
            okuLanguage = okuTextWithId.language
        )

        //Zudem Okutext updaten mit den im Text enthaltenen OkuWords
        val okuWordsInText = OkuWordEntity.getWordsByFilter(okuTextWithId.language) {
            OkuWordEntity.word.lowerCase() inList okuText.wordList.map { w -> w.lowercase() }
        }.mapNotNull { it.id }.toSet()
        val updatedOkuText = okuTextWithId.copy(okuWordIdSet = okuWordsInText)
        OkuTextEntity.insertText(updatedOkuText)

        return newId
    }

    fun loadTextForReaderscreen(okuText: OkuText?) {
        okuText?.let { text ->
            changeCurrentOkuText(text)
        }
    }

    fun loadTextForReaderscreen(okuTextListItem: OkuTextListItem) {
        OkuTextEntity.getText(okuTextListItem.id!!)?.let { text ->
            changeCurrentOkuText(text)
        }
    }

    private fun changeCurrentOkuText(text: OkuText) {
        okuWordsSublist = emptyList()
        currentPage = text.currentPage
        currentOkuText = CurrentOkuText(
            okuText = text,
            okuWords = OkuWordEntity.getWordsByFilter(text.language) {
                OkuWordEntity.word.lowerCase() inList text.wordList.map { it.lowercase() }
            }.distinct().associateBy { it.word.lowercase() }
        )
        calculateFrequencyRankingForLanguage(currentOkuText!!.okuText.language)
    }

    fun updateUnknownWordsToKnown(words: List<String>) {
        //Add the new dates to the words whose status changed and save the status in the database
        val okuwordsWithDate = currentOkuText?.okuWords
            ?.filter { it.value.status == WordStatus.UNKNOWN }
            ?.filter { it.key in words.map { it.lowercase() } }
            ?.map { changeDateOfOkuword(it.value.copy(status = WordStatus.KNOWN)) }

        if (okuwordsWithDate.isNullOrEmpty()) {
            return
        }

        OkuWordEntity.batchUpdateWords(okuwordsWithDate)

        //Update the words in the current text in order to reflect the changes
        val updatedMap = currentOkuText!!.okuWords.toMutableMap()
        okuwordsWithDate.forEach { updatedMap[it.word.lowercase()] = it }
        currentOkuText = currentOkuText!!.copy(okuWords = updatedMap)

        textListService.flagTextsForMetricUpdate(okuwordsWithDate)
    }

    fun updateWord(okuWord: OkuWord) {
        val okuWordWithDate = changeDateOfOkuword(okuWord)
        OkuWordEntity.updateWord(okuWordWithDate)

        val updatedMap = currentOkuText!!.okuWords.toMutableMap()
        updatedMap[okuWord.word.lowercase()] = okuWordWithDate
        currentOkuText = currentOkuText!!.copy(okuWords = updatedMap)

        textListService.flagTextsForMetricUpdate(listOf(okuWordWithDate))
    }

    /**
     * Update the date of an OkuWord according to its status
     */
    private fun changeDateOfOkuword(okuWord: OkuWord): OkuWord {
        return when (okuWord.status) {
            WordStatus.UNKNOWN -> okuWord.copy(learningStart = null, learningFinished = null)
            WordStatus.LEARNING -> okuWord.copy(learningStart = LocalDateTime.now(), learningFinished = null)
            WordStatus.KNOWN -> {
                if (okuWord.learningStart == null) {
                    okuWord.copy(learningStart = LocalDateTime.now(), learningFinished = LocalDateTime.now())
                } else {
                    okuWord.copy(learningFinished = LocalDateTime.now())
                }
            }

            else -> okuWord
        }
    }

    fun getLanguagesBeingLearned(isFrequencyAnalysis: Boolean) =
        listOf(OkuLanguage.ALL) + LanguageEntity.getLanguagesBeingLearned(isFrequencyAnalysis).sortedBy { it.label }

    /**
     * Calculates how many words can be placed in a single row and creates a list of sublists accordingly
     * Store word and its index in the text
     * @return list of lists for each row. The lists contain a Pair of the word + its index in the text
     */
    fun createChunkedWordlist(maxWidth: Dp): List<List<Pair<String, Int>>> {
        val result = mutableListOf<List<Pair<String, Int>>>()
        var currentLineChunk = mutableListOf<Pair<String, Int>>()
        var currentLineLength = 0.dp

        currentOkuText!!.okuText.wordList.forEachIndexed { index, string ->
            val wordLength = wordDpMap[string]!!

            when {
                string.isLineBreak() -> {
                    //1. finish the current line with the linebreak
                    currentLineChunk.add(Pair(string, index))
                    result.add(currentLineChunk)

                    //2. Create a new empty line chunk for the next upcoming words
                    currentLineChunk = mutableListOf()
                    currentLineLength = wordLength
                }

                currentLineLength + wordLength < maxWidth || string.isLineBreak() -> {
                    //when the word fits into the line, add it
                    currentLineChunk.add(Pair(string, index))
                    currentLineLength += wordLength
                }

                else -> {
                    //When the line is too full for the string, finish it
                    result.add(currentLineChunk)
                    //Start a new line chunk with the current string
                    currentLineChunk = mutableListOf(Pair(string, index))
                    currentLineLength = wordLength
                }

            }
        }

        // Add the last chunk if it's not empty
        if (currentLineChunk.isNotEmpty()) {
            result.add(currentLineChunk)
        }

        return result
    }
}