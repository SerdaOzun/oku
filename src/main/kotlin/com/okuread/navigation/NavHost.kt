package com.okuread.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.okuread.screens.frequency.FrequencyScreen
import com.okuread.screens.home.HomeScreen
import com.okuread.screens.plugins.PluginsScreen
import com.okuread.screens.reading.newText.CreateTextScreen
import com.okuread.screens.reading.reader.ReaderScreen
import com.okuread.screens.reading.textlist.ReadingListScreen
import com.okuread.screens.settings.SettingsScreen
import com.okuread.screens.stats.StatsScreen
import com.okuread.ui.theme.spacing
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.transition.NavTransition

@Composable
fun OkuNavHost(navigator: Navigator) {
    NavHost(
        modifier = Modifier.padding(MaterialTheme.spacing.small),
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = Screen.ReadingListScreen.name
    ) {
        scene(Screen.HomeScreen.name) {
            HomeScreen(navigator)
        }
        scene(Screen.ReadingListScreen.name) {
            ReadingListScreen(navigator)
        }
        scene(Screen.ReaderScreen.name) {
            ReaderScreen(navigator)
        }
        scene("${Screen.CreateTextScreen.name}/{id}?") { backStackEntry ->
            val id: Long? = backStackEntry.path<Long>("id")
            CreateTextScreen(navigator, id)
        }
        scene(Screen.Stats.name) {
            StatsScreen()
        }
        scene(Screen.Settings.name) {
            SettingsScreen()
        }
        scene(Screen.WordFrequency.name) {
            FrequencyScreen(navigator)
        }
        scene(Screen.PluginsScreen.name) {
            PluginsScreen()
        }
    }
}