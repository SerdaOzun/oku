package com.okuread.screens.settings.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.dropdown.Dropdown
import com.gabrieldrn.carbon.dropdown.base.DropdownOption
import com.okuread.db.repositories.OkuSetting
import com.okuread.db.repositories.SettingsEntity
import com.okuread.db.repositories.upsert
import com.okuread.db.util.OkuLanguage
import com.okuread.services.SettingsService
import com.okuread.ui.components.FontSizeChanger
import com.okuread.ui.theme.spacing

@Composable
fun generalView(settingsService: SettingsService) {
    val languagesBeingLearned by remember {
        mutableStateOf(
            OkuLanguage.entries.filterNot { it == OkuLanguage.ALL }
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

    Column(modifier = Modifier.fillMaxSize()) {
        //Main Language
        Row(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dropdown(
                placeholder = "",
                options = languagesBeingLearned,
                selectedOption = filteredLanguage,
                onOptionSelected = {
                    filteredLanguage = it
                    languagesBeingLearned[filteredLanguage]?.value?.let {
                        OkuLanguage.valueByLabel(it)
                    }?.let { newDefaultLanguage ->
                        SettingsEntity.upsert(OkuSetting.DefaultLanguage(newDefaultLanguage))
                        settingsService.defaultLanguage = newDefaultLanguage
                    }
                },
                modifier = Modifier.width(200.dp).padding(end = MaterialTheme.spacing.small),
                label = "Main language learning",
            )
        }

        //Default Font size
        FontSizeChanger(settingsService.defaultFontSize, "Default font size:") {
            SettingsEntity.upsert(OkuSetting.DefaultFontSize(it.value))
            settingsService.defaultFontSize = it
        }
    }
}