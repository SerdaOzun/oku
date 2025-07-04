package com.okuread.services

import com.okuread.db.repositories.Dictionary
import com.okuread.db.repositories.selectWord
import com.okuread.db.repositories.selectWords
import com.okuread.db.util.OkuLanguage
import org.koin.core.component.KoinComponent

class DictionaryService: KoinComponent {

    /**
     * Gets dictionary entries grouped by dialect
     */
    fun getDictionaryEntry(okuLanguage: OkuLanguage, word: String) = Dictionary.selectWord(okuLanguage, word)
    fun getDictionaryEntries(okuLanguage: OkuLanguage, words: List<String>) = Dictionary.selectWords(okuLanguage, words)
}