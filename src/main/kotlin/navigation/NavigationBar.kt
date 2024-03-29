package navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.first
import moe.tlaster.precompose.navigation.Navigator
import ui.theme.fontSize
import ui.theme.spacing

@Composable
fun NavigationBar(navigator: Navigator) {

    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(MaterialTheme.spacing.extraSmall)
    ) {
        Screen.entries.filter { it.navItem }.forEachIndexed { index, screen ->
            Row(modifier = Modifier.fillMaxHeight().clickable {
                selectedIndex = index
                navigator.navigate(screen.name)
            }) {
                Text(
                    text = screen.label,
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                    fontSize = MaterialTheme.fontSize.large,
                    modifier = Modifier.padding(MaterialTheme.spacing.small)
                )
            }
        }
    }
}