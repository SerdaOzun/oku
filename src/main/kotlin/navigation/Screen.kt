package navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val label: String,
    val icon: ImageVector,
    val navItem: Boolean = true
) {
    HomeScreen(
        label = "Home",
        icon = Icons.Filled.Home
    ),
    TextListScreen(
        label = "Texts",
        icon = Icons.Filled.Search
    ),
    ReaderScreen(
        label = "Reader",
        icon = Icons.Filled.Edit,
        navItem = false
    ),
    CreateTextScreen(
        label = "CreateText",
        icon = Icons.Filled.Create,
        navItem = false
    )
}