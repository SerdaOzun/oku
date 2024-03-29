package database.util

import androidx.compose.ui.graphics.Color

enum class WordStatus(val statuscode: Int, val color: Color = Color.LightGray) {
    UNKNOWN(0),
    LEARNING(1),
    KNOWN(2),
    IGNORED(3);

    companion object {
        fun byStatuscode(statuscode: Int) = values().first { it.statuscode == statuscode }
    }
}