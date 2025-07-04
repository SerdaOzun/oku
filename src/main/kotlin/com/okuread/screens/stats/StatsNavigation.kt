package com.okuread.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabrieldrn.carbon.dropdown.Dropdown
import com.gabrieldrn.carbon.dropdown.base.DropdownOption
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.db.util.OkuLanguage
import com.okuread.db.util.WordStatus
import com.okuread.services.SettingsService
import com.okuread.services.StatsService
import com.okuread.ui.theme.spacing
import com.okuread.util.isSkippableWord

@Composable
fun StatsNavigation(
    modifier: Modifier, statsService: StatsService, settingsService: SettingsService, onClick: (StatsState) -> Unit
) {

    val languagesBeingLearned by remember {
        mutableStateOf(
            statsService.getLanguagesBeingLearned()
                .mapIndexed { index, language -> index to DropdownOption(value = language.label) }.toMap()
        )
    }
    var filteredLanguage by remember {
        mutableStateOf(
            if (settingsService.defaultLanguage != OkuLanguage.ALL) {
                languagesBeingLearned.values.indexOfFirst { it.value == settingsService.defaultLanguage.label }.let {
                    if (it != -1) it else 0
                }
            } else {
                0
            }
        )
    }

    LaunchedEffect(true) {
        statsService.statsState = statsService.statsState.copy(
            filteredLanguage = languagesBeingLearned[filteredLanguage]?.value?.let { OkuLanguage.valueByLabel(it) }
        )
    }

    Column(modifier = modifier) {
        //Language
        if (statsService.statsState.filteredLanguage != null) {
            Dropdown(
                placeholder = "",
                options = languagesBeingLearned,
                selectedOption = filteredLanguage,
                onOptionSelected = {
                    filteredLanguage = it
                    onClick(
                        statsService.statsState.copy(
                            filteredLanguage = languagesBeingLearned[filteredLanguage]?.value?.let {
                                OkuLanguage.valueByLabel(it)
                            }
                        ))
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.small),
                label = "Language",
            )
        }
        NavigationItem(
            buttonLabel = "Graphs",
            isSelected = statsService.statsState.subScreen == StatsSubScreen.GRAPHS,
            wordCount = null
        ) {
            onClick(statsService.statsState.copy(subScreen = StatsSubScreen.GRAPHS))
        }
        NavigationItem(
            buttonLabel = "All Words",
            isSelected = statsService.statsState.subScreen == StatsSubScreen.ALL_WORDS,
            wordCount = statsService.wordList.count { !it.okuWord.word.isSkippableWord() }.toString(),
        ) {
            onClick(statsService.statsState.copy(subScreen = StatsSubScreen.ALL_WORDS))
        }

        NavigationItem(
            buttonLabel = "Words Learning",
            isSelected = statsService.statsState.subScreen == StatsSubScreen.WORDS_LEARNING,
            wordCount = statsService.wordList.count { it.okuWord.status == WordStatus.LEARNING }.toString(),
        ) {
            onClick(statsService.statsState.copy(subScreen = StatsSubScreen.WORDS_LEARNING))
        }

        NavigationItem(
            buttonLabel = "Words Known",
            isSelected = statsService.statsState.subScreen == StatsSubScreen.WORDS_KNOWN,
            wordCount = statsService.wordList.count { it.okuWord.status == WordStatus.KNOWN }.toString(),
        ) {
            onClick(statsService.statsState.copy(subScreen = StatsSubScreen.WORDS_KNOWN))
        }

        NavigationItem(
            buttonLabel = "Words Ignored",
            isSelected = statsService.statsState.subScreen == StatsSubScreen.WORDS_IGNORED,
            wordCount = statsService.wordList.count { it.okuWord.status == WordStatus.IGNORED }.toString(),
        ) {
            onClick(statsService.statsState.copy(subScreen = StatsSubScreen.WORDS_IGNORED))
        }

    }
}

/**
 * @param buttonLabel
 * @param isSelected
 * @param onClick showGraph? oder Liste ansonsten
 */
@Composable
private fun NavigationItem(
    buttonLabel: String,
    isSelected: Boolean,
    wordCount: String?,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }
            .background(if (isSelected) WhiteTheme.layerAccent01 else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Divider(modifier = Modifier.fillMaxHeight().width(5.dp), color = WhiteTheme.focus)
            }
            Text(buttonLabel, modifier = Modifier.padding(MaterialTheme.spacing.small), fontSize = 16.sp)
            if (wordCount != null) {
                Text(wordCount, fontSize = 12.sp)
            }
        }
    }
}