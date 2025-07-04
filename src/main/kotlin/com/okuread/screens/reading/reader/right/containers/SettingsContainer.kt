package com.okuread.screens.reading.reader.right.containers

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonSize
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.navigation.Screen
import com.okuread.screens.reading.reader.right.TutorialDialogContent
import com.okuread.services.ReadingService
import com.okuread.ui.components.DialogWithCancel
import com.okuread.ui.components.FontSizeChanger
import com.okuread.ui.theme.spacing
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun SettingsContainer(
    modifier: Modifier,
    navigator: Navigator,
    readingService: ReadingService,
    fontSize: TextUnit,
    onFontChange: (TextUnit) -> Unit
) {
    var showTutorialDialog by remember { mutableStateOf(false) }

    Column(
        modifier.fillMaxWidth().border(1.dp, WhiteTheme.borderSubtle00)
            .padding(start = MaterialTheme.spacing.small),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            "Settings",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = MaterialTheme.spacing.small, bottom = MaterialTheme.spacing.small)
        )

        FontSizeChanger(fontSize) { onFontChange(it) }

        Button(
            label = "I know the remaining words!",
            onClick = {
                //Update all unknown words of the current page to known
                readingService.okuWordsSublist.subList(
                    readingService.lineOffset.first.coerceAtMost(readingService.okuWordsSublist.size),
                    readingService.lineOffset.last.coerceAtMost(readingService.okuWordsSublist.size),
                ).flatMap { it }.map { it.first }.let { readingService.updateUnknownWordsToKnown(it) }
            },
            buttonType = ButtonType.Primary,
            buttonSize = ButtonSize.Small,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallest)
        )

        Button(
            label = "Edit text",
            onClick = { navigator.navigate("${Screen.CreateTextScreen.name}/${readingService.currentOkuText?.okuText?.id ?: ""}") },
            buttonType = ButtonType.Primary,
            buttonSize = ButtonSize.Small,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallest)
        )

        Button(
            label = "Help",
            onClick = { showTutorialDialog = true },
            buttonType = ButtonType.Ghost,
            buttonSize = ButtonSize.Small
        )

        if (showTutorialDialog) {
            DialogWithCancel(
                message = "Help",
                content = { TutorialDialogContent(Modifier.size(600.dp, 400.dp)) },
                onDismiss = { showTutorialDialog = false })
        }
    }
}
