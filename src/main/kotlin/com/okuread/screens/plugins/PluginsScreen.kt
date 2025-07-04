package com.okuread.screens.plugins

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.ui.components.DefaultNavBar
import com.okuread.ui.components.LabelEnum
import com.okuread.ui.theme.spacing

@Composable
fun PluginsScreen() {
    var currentView by remember { mutableStateOf(PluginsView.DICTIONARY) }

    Row(Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(0.2f).padding(end = MaterialTheme.spacing.small)) {
            DefaultNavBar<PluginsView>(Modifier, PluginsView.entries, currentView) { currentView = it }
        }

        Divider(
            modifier = Modifier.fillMaxHeight().width(1.dp),
            thickness = 1.dp,
            color = WhiteTheme.borderSubtle00
        )

        Row(Modifier.weight(0.8f).padding(start = MaterialTheme.spacing.small)) {
            when (currentView) {
                PluginsView.DICTIONARY -> {
                    DictionariesView()
                }
            }
        }
    }
}

enum class PluginsView(override val label: String) : LabelEnum {
    DICTIONARY("Dictionaries")
}
