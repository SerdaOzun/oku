package com.okuread.screens.frequency

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


enum class FrequencyListHeaders(val label: String, val weight: Float) {
    FINISHED("", 0.05f),
    NAME("Name", 0.35f),
    DATE_CREATED("Created", 0.15f),
    KNOWN_WORDS("Known %", 0.15f),
    UNIQUE_WORDS("Unique", 0.15f),
    TOTAL_WORDS("Total", 0.15f);
}

fun FrequencyListHeaders.toTableColumn(): Column<out Any> = when (this) {
    FrequencyListHeaders.NAME -> OkuTextEntity.title
    FrequencyListHeaders.DATE_CREATED -> OkuTextEntity.timestamp_created
    FrequencyListHeaders.KNOWN_WORDS -> OkuTextEntity.percentageKnown
    FrequencyListHeaders.UNIQUE_WORDS -> OkuTextEntity.uniqueWordsCount
    else -> OkuTextEntity.title
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FrequencyTableHeaders(
    modifier: Modifier,
    listSorting: FrequencyListSorting,
    setListSorting: (FrequencyListSorting) -> Unit
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        FrequencyListHeaders.entries.forEach { currentHeader ->
            if (currentHeader == FrequencyListHeaders.FINISHED) {
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

