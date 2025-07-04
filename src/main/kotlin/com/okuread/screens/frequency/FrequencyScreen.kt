package com.okuread.screens.frequency

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.screens.reading.newText.CreateTextScreen
import com.okuread.ui.components.DefaultNavBar
import com.okuread.ui.components.LabelEnum
import com.okuread.ui.theme.spacing
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun FrequencyScreen(navigator: Navigator) {

    var currentView by remember { mutableStateOf(FrequencyView.ANALYSIS_LIST) }

    Row(Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(0.2f).padding(end = MaterialTheme.spacing.small)) {
            DefaultNavBar<FrequencyView>(Modifier, FrequencyView.entries, currentView) { currentView = it }
        }

        Divider(
            modifier = Modifier.fillMaxHeight().width(1.dp),
            thickness = 1.dp,
            color = WhiteTheme.borderSubtle00
        )

        Row(Modifier.weight(0.8f).padding(start = MaterialTheme.spacing.small)) {
            when (currentView) {
                FrequencyView.NEW_TEXT -> {
                    CreateTextScreen(navigator = navigator, okuTextId = null, isFrequencyAnalysis = true)
                }

                FrequencyView.ANALYSIS_LIST -> {
                    FrequencyListScreen()
                }
            }
        }
    }
}

enum class FrequencyView(override val label: String) : LabelEnum {
    ANALYSIS_LIST("Analyzed Texts"), NEW_TEXT("New Entry")
}