package com.okuread.db.util

import androidx.compose.ui.graphics.Color

enum class WordStatus(val label: String, val statuscode: Int, val color: Color, val nextStageOnClick: Int) {
    UNKNOWN("Unknown", 0, Color.Yellow, 1),
    LEARNING("Learning", 1, Color.Green, 2),
    KNOWN("Known", 2, Color.Transparent, 0),
    IGNORED("Ignored", 3, Color.LightGray, 0);

    companion object {
        fun byStatuscode(statuscode: Int) = entries.first { it.statuscode == statuscode }
    }
}