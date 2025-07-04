package com.okuread.db.data

data class DictionaryEntry(
    val pos: String = "", //Noun, verb, adj etc.
    val word: String,
    val sentences: List<ExampleSentence> = emptyList(),
    val meanings: List<List<String?>?> = emptyList(),
    val links: List<String?> = emptyList(), // Link to another word
    val okuDialect: String? = null
)

data class ExampleSentence(
    val text: String? = "",
    val translation: String? = ""
)

/**
 * Only used to insert values
 */
data class DictionaryInsert(
    val word: String,
    val json: String,
    val dialect: String = ""
)