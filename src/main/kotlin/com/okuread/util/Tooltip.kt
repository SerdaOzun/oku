package com.okuread.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicTooltip(
    text: String,
    content: @Composable () -> Unit
) {

    TooltipArea(
        tooltip = {
            Surface(
                modifier = Modifier.shadow(4.dp),
                color = Color.White,
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(10.dp)
                )
            }
        },
        delayMillis = 600,
        tooltipPlacement = TooltipPlacement.CursorPoint(
            alignment = Alignment.BottomEnd
        )
    ) {
        content()
    }

}