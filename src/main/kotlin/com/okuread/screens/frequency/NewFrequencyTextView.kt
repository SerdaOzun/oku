package com.okuread.screens.frequency

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.okuread.db.data.OkuText
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.db.repositories.getTrialFrequencyTextLimit
import com.okuread.db.util.OkuLanguage
import com.okuread.getKoinInstance
import com.okuread.services.ReadingService
import com.okuread.services.SettingsService
import com.okuread.textprocessing.processOkuText
import com.okuread.ui.components.Combobox
import com.okuread.ui.theme.spacing
import java.time.LocalDateTime

@Composable
fun NewFrequencyTextView(
    modifier: Modifier,
    language: OkuLanguage,
    onLanguageSelect: (OkuLanguage) -> Unit,
    readingService: ReadingService = getKoinInstance(),
    settings: SettingsService = getKoinInstance()
) {
    var title: String by remember { mutableStateOf("") }
    var body: String by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        //Title + Language
        Row(
            Modifier.weight(0.1f).widthIn(0.dp, 1000.dp).padding(bottom = MaterialTheme.spacing.small)
                .height(IntrinsicSize.Min)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.weight(0.55f),
                maxLines = 1,
                placeholder = { Text(text = "Title") })
            Combobox(
                modifier = Modifier.weight(0.4f).padding(start = MaterialTheme.spacing.small),
                language,
                OkuLanguage.entries.filterNot { it == OkuLanguage.ALL },
                onClick = { onLanguageSelect(it) }
            )
        }

        //Text
        Row(Modifier.weight(0.8f).fillMaxSize().widthIn(0.dp, 1000.dp)) {
            TextField(
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.fillMaxSize(),
                minLines = 20,
                placeholder = { Text(text = "Text") })
        }

        //Buttons
        Row(
            Modifier.weight(0.1f).widthIn(0.dp, 1000.dp).padding(top = MaterialTheme.spacing.medium),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                enabled = title.isNotEmpty() && body.isNotEmpty() && !settings.disableFrequencyTextCreation,
                onClick = {
                    val okuText = OkuText(
                        id = null,
                        title = title.trim(),
                        body = body,
                        wordList = processOkuText(body, language),
                        timestampCreated = LocalDateTime.now(),
                        timestampFinished = null,
                        language = language,
                        isFromFrequencyAnalysis = true
                    )
                    //Insert into database and load into memory
                    readingService.insertText(okuText)
                    title = ""
                    body = ""
                    settings.disableFrequencyTextCreation = !settings.licenseActivated && OkuTextEntity.getTrialFrequencyTextLimit().size >= 2
                }
            ) {
                Text("Analyze")
            }
        }

    }

}
