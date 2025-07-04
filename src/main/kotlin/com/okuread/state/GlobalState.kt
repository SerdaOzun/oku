package com.okuread.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import com.okuread.db.repositories.OkuSetting
import com.okuread.db.repositories.SettingsEntity
import com.okuread.db.repositories.getSetting
import com.okuread.loggerRoot

object GlobalState {
    val windowState by mutableStateOf(
        WindowState(
            size = SettingsEntity.getSetting<OkuSetting.WindowSize>()?.let {
                try {
                    val sizes = it.split(";")
                    DpSize(sizes.first().toFloat().dp, sizes.last().toFloat().dp)
                } catch (e: Exception) {
                    loggerRoot.error("Couldn't parse window size", e)
                    null
                }
            } ?: DpSize(960.dp, 800.dp))
    )
}