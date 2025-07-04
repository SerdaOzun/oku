package com.okuread.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.ui.theme.spacing

interface LabelEnum {
    val label: String
}

@Composable
fun <T : LabelEnum> DefaultNavBar(modifier: Modifier, enumEntries: List<T>, selectedEntry: T, onClick: (T) -> Unit) {
    Column(modifier) {
        enumEntries.forEach { entry ->
            Row(
                Modifier.fillMaxWidth().clickable { onClick(entry) }
                    .background(if (selectedEntry == entry) WhiteTheme.layerAccent01 else Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    if (selectedEntry == entry) {
                        Divider(modifier = Modifier.fillMaxHeight().width(5.dp), color = WhiteTheme.focus)
                    }
                    Text(entry.label, modifier = Modifier.padding(MaterialTheme.spacing.small), fontSize = 16.sp)
                }
            }
        }
    }
}
