package com.okuread.screens.stats

import com.okuread.db.util.OkuLanguage

enum class StatsSubScreen {
    GRAPHS,
    ALL_WORDS,
    WORDS_LEARNING,
    WORDS_KNOWN,
    WORDS_IGNORED,
    TEXTS
}

data class StatsState(
    val subScreen: StatsSubScreen = StatsSubScreen.GRAPHS,
    val filteredLanguage: OkuLanguage?
)