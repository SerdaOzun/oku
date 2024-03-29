package navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import screens.home.HomeScreen
import screens.reading.newText.CreateTextScreen
import screens.reading.reader.ReaderScreen
import screens.reading.textlist.TextListScreen
import ui.theme.spacing

@Composable
fun OkuNavHost(navigator: Navigator) {
    NavHost(
        modifier = Modifier.padding(MaterialTheme.spacing.extraSmall),
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = Screen.HomeScreen.name
    ) {
        scene(Screen.HomeScreen.name) {
            HomeScreen(navigator)
        }
        scene(Screen.TextListScreen.name) {
            TextListScreen(navigator)
        }
        scene(Screen.ReaderScreen.name) {
            ReaderScreen(navigator)
        }
        scene(Screen.CreateTextScreen.name) {
            CreateTextScreen(navigator)
        }
    }

}