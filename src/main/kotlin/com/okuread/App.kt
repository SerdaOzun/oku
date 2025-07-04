package com.okuread

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.okuread.navigation.NavigationBar
import com.okuread.navigation.OkuNavHost
import com.okuread.services.SettingsService
import com.okuread.ui.components.DialogWithCancel
import moe.tlaster.precompose.navigation.rememberNavigator

@Composable
@Preview
fun App(settingsVm: SettingsService = getKoinInstance()) {
    val navigator = rememberNavigator()
    var showTrialDialog by remember { mutableStateOf(!settingsVm.licenseActivated) }

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Scaffold(
            modifier = Modifier.widthIn(0.dp, 1600.dp),
            topBar = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    NavigationBar(navigator)
                }
            })
        {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center
            ) {
                OkuNavHost(navigator)
            }
        }
    }

    if (!settingsVm.licenseActivated) {
        if (showTrialDialog) {
            DialogWithCancel(
                modifier = Modifier.width(400.dp),
                message = "Trial",
                content = { Text("You are using a test version of Oku and can only save two texts.\n" +
                        "Activate your license key in the settings screen") },
                onDismiss = {
                    showTrialDialog = false
                }
            )
        }
    }
}

