package com.okuread.ui.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.okuread.ui.theme.spacing

@Composable
fun <T> Combobox(
    modifier: Modifier,
    selectedOption: T,
    allOptions: List<T>,
    onClick: (T) -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.heightIn(0.dp, 56.dp).fillMaxHeight().clickable(onClick = { expanded = true })
            .bottomBorder(1.dp, MaterialTheme.colors.onBackground)
            .onSizeChanged { size = it },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            selectedOption.toString(),
            modifier = Modifier.weight(0.9f).padding(start = 10.dp)
        )
        Icon(
            if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
            "",
            modifier = Modifier.weight(0.1f)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(size.width.dp)
        ) {
            Box(modifier = Modifier.size(size.width.dp, 300.dp)) {
                val state = rememberLazyListState()

                LazyColumn(modifier = Modifier.fillMaxSize().padding(end = MaterialTheme.spacing.medium), state) {
                    items(allOptions) { opt ->
                        DropdownMenuItem(onClick = {
                            onClick(opt)
                            expanded = false
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = opt.toString(), color = MaterialTheme.colors.onBackground)
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }
}
