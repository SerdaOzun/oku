package com.okuread.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun RowScope.TableCell(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        modifier = modifier,//.padding(MaterialTheme.spacing.small),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RowScope.TableCellWithIcon(
    text: String,
    showIcon: Boolean = false,
    ascending: Boolean = true,
    modifier: Modifier = Modifier.background(Color.Blue)
) {
    Row(modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            modifier = Modifier,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        if (showIcon) {
            Icon(
                if (ascending) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                "",
                modifier = Modifier
            )
        }
    }
}
