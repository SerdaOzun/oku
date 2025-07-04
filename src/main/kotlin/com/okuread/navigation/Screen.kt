package com.okuread.navigation

enum class Screen(
    val label: String,
    val imgResource: String? = null,
    val navItem: Boolean = true
) {
    HomeScreen(
        label = "Home",
        navItem = false
    ),
    ReadingListScreen(
        label = "Reading",
        imgResource = "icons/book-open-text.svg"
    ),
    ReaderScreen(
        label = "Reader",
        navItem = false
    ),
    WordFrequency(
        label = "Word Frequency",
        imgResource = "icons/audio-lines.svg"
    ),
    Stats(
        label = "Stats",
        imgResource = "icons/bar-chart-4.svg"
    ),
    CreateTextScreen(
        label = "CreateText",
        navItem = false
    ),
    Settings(
        "Settings",
        imgResource = "icons/settings.svg"
    ),
    PluginsScreen(
        label = "Plugins",
        imgResource = "icons/plug.svg"
    )
}