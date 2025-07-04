package com.okuread.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.okuread.ui.theme.fontSize
import com.okuread.ui.theme.spacing
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun NavigationBar(navigator: Navigator) {

    var selectedIndex by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).background(color = Color.Black),
        horizontalArrangement = Arrangement.Start
    ) {
        Screen.entries.filter { it.navItem }.forEachIndexed { index, screen ->
            Row(
                modifier = Modifier.fillMaxHeight().clickable {
                    selectedIndex = index
                    navigator.navigate(screen.name)
                }.width(IntrinsicSize.Max).padding(start = MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                screen.imgResource?.let { rsc ->
                    Icon(painterResource(rsc), null, modifier = Modifier.size(20.dp), tint = Color.White)
                }
                Text(
                    text = screen.label,
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                    fontSize = MaterialTheme.fontSize.default,
                    modifier = Modifier.padding(
                        start = MaterialTheme.spacing.smaller,
                        top = MaterialTheme.spacing.small,
                        bottom = MaterialTheme.spacing.small,
                        end = MaterialTheme.spacing.small,
                    ),
                    color = Color.White
                )
            }
        }
    }
}