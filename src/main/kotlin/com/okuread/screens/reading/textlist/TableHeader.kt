package com.okuread.screens.reading.textlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.ui.components.TableCellWithIcon
import org.jetbrains.exposed.sql.Column


enum class ReadingHeaders(val label: String, val weight: Float) {
    FINISHED("", 0.05f),
    NAME("Name", 0.50f),
    DATE_CREATED("Created", 0.15f),
    KNOWN_WORDS("Known %", 0.10f),
    UNIQUE_WORDS("Unique Words", 0.2f);
}

fun ReadingHeaders.toTableColumn(): Column<out Any> = when (this) {
    ReadingHeaders.NAME -> OkuTextEntity.title
    ReadingHeaders.DATE_CREATED -> OkuTextEntity.timestamp_created
    ReadingHeaders.KNOWN_WORDS -> OkuTextEntity.percentageKnown
    ReadingHeaders.UNIQUE_WORDS -> OkuTextEntity.uniqueWordsCount
    else -> OkuTextEntity.title
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Headers(
    modifier: Modifier,
    listSorting: ReadingListSorting,
    setListSorting: (ReadingListSorting) -> Unit
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        ReadingHeaders.entries.forEach { currentHeader ->
            if (currentHeader == ReadingHeaders.FINISHED) {
                Spacer(modifier = Modifier.weight(currentHeader.weight))
            } else {
                Row(
                    modifier = Modifier.fillMaxSize().weight(currentHeader.weight)
                        .onClick {
                            setListSorting(
                                listSorting.copy(
                                    column = currentHeader,
                                    ascending = !listSorting.ascending
                                )
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCellWithIcon(
                        text = currentHeader.label,
                        showIcon = currentHeader == listSorting.column,
                        ascending = listSorting.ascending,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

