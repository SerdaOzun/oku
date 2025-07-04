package com.okuread.screens.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.okuread.getKoinInstance
import com.okuread.screens.stats.wordsGraphs.OkuGraphsView
import com.okuread.screens.stats.wordslistTable.WordsListView
import com.okuread.services.SettingsService
import com.okuread.services.StatsService

@Composable
fun StatsScreen(
    statsService: StatsService = getKoinInstance(),
    settingsService: SettingsService = getKoinInstance()
) {

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            StatsNavigation(Modifier.weight(0.2f), statsService, settingsService) { updatedState ->
                statsService.statsState = updatedState
            }
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp), thickness = 1.dp)

            Column(modifier = Modifier.weight(0.80f)) {

                when (statsService.statsState.subScreen) {
                    StatsSubScreen.TEXTS -> Text("Coming soon") //todo
                    StatsSubScreen.GRAPHS -> OkuGraphsView(statsService)
                    StatsSubScreen.ALL_WORDS, StatsSubScreen.WORDS_KNOWN, StatsSubScreen.WORDS_LEARNING, StatsSubScreen.WORDS_IGNORED -> {
                        WordsListView(statsService)
                    }
                }
            }
        }
    }

}