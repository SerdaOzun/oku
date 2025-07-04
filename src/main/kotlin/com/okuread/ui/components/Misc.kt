package com.okuread.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import com.okuread.ui.theme.spacing


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FontSizeChanger(fontSize: TextUnit, text: String = "Font size:", changeFontSize: (TextUnit) -> Unit) {

    Row(Modifier.padding(top = MaterialTheme.spacing.small), verticalAlignment = Alignment.CenterVertically) {
        Text(text, modifier = Modifier.padding(end = MaterialTheme.spacing.smaller))
        Icon(
            Icons.Default.KeyboardArrowUp,
            "",
            modifier = Modifier.onClick {
                if (fontSize < 40.sp) changeFontSize(
                    TextUnit(
                        value = fontSize.value + 1f,
                        TextUnitType.Sp
                    )
                )
            }
        )
        Text(
            fontSize.value.toString(),
            modifier = Modifier.padding(start = MaterialTheme.spacing.smaller, end = MaterialTheme.spacing.smaller)
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            "",
            modifier = Modifier.onClick {
                if (fontSize > 10.sp) changeFontSize(
                    TextUnit(
                        value = fontSize.value - 1f,
                        TextUnitType.Sp
                    )
                )
            }
        )
    }
}