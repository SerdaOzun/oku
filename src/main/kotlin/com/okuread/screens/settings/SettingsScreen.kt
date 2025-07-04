package com.okuread.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.getKoinInstance
import com.okuread.screens.settings.views.LicensesView
import com.okuread.screens.settings.views.generalView
import com.okuread.screens.settings.views.infoView
import com.okuread.services.SettingsService
import com.okuread.ui.components.DefaultNavBar
import com.okuread.ui.components.LabelEnum
import com.okuread.ui.theme.spacing

@Composable
fun SettingsScreen(settingsVm: SettingsService = getKoinInstance()) {
    var currentView by remember { mutableStateOf(SettingsView.GENERAL) }

    Row(Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(0.2f).padding(end = MaterialTheme.spacing.small)) {
            DefaultNavBar<SettingsView>(Modifier, SettingsView.entries, currentView) { currentView = it }
        }

        Divider(
            modifier = Modifier.fillMaxHeight().width(1.dp),
            thickness = 1.dp,
            color = WhiteTheme.borderSubtle00
        )

        Row(Modifier.weight(0.8f).padding(start = MaterialTheme.spacing.small)) {
            when (currentView) {
                SettingsView.GENERAL -> {
                    generalView(settingsVm)
                }

                SettingsView.INFO -> {
                    infoView(settingsVm)
                }

                SettingsView.LICENSES -> {
                    LicensesView()
                }
            }
        }
    }
}

enum class SettingsView(override val label: String) : LabelEnum {
    GENERAL("General"),
    INFO("Info"),
    LICENSES("Licenses")
}