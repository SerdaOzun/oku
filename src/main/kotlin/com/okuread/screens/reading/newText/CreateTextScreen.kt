package com.okuread.screens.reading.newText

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.dropdown.Dropdown
import com.gabrieldrn.carbon.dropdown.base.DropdownOption
import com.gabrieldrn.carbon.textinput.TextArea
import com.gabrieldrn.carbon.textinput.TextInput
import com.okuread.db.data.OkuText
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.db.repositories.getTrialFrequencyTextLimit
import com.okuread.db.repositories.getTrialTextLimit
import com.okuread.db.util.OkuLanguage
import com.okuread.getKoinInstance
import com.okuread.navigation.Screen
import com.okuread.services.ReadingService
import com.okuread.services.SettingsService
import com.okuread.textprocessing.processOkuText
import com.okuread.ui.theme.spacing
import kotlinx.coroutines.delay
import moe.tlaster.precompose.navigation.Navigator
import java.time.LocalDateTime

@Composable
fun CreateTextScreen(
    navigator: Navigator,
    okuTextId: Long?,
    isFrequencyAnalysis: Boolean = false,
    readingService: ReadingService = getKoinInstance(),
    settingsVm: SettingsService = getKoinInstance()
) {
    var title: String by remember { mutableStateOf("") }
    var okutextOld: OkuText? by remember { mutableStateOf(null) }
    var body: String by remember { mutableStateOf("") }
    val allLanguages by remember {
        mutableStateOf(
            OkuLanguage.entries
                .filterNot { it == OkuLanguage.ALL }
                .mapIndexed { index, language -> index to DropdownOption(value = language.label) }
                .toMap()
        )
    }
    var language: Int by remember {
        mutableStateOf(
            if (settingsVm.defaultLanguage != OkuLanguage.ALL) {
                allLanguages.values.indexOfFirst { it.value == settingsVm.defaultLanguage.label }
            } else {
                0
            }
        )
    }
    var selectedOkuLanguage: OkuLanguage by remember {
        mutableStateOf(OkuLanguage.entries.filterNot { it == OkuLanguage.ALL }.first())
    }

    //check if user is limited by trial
    var isLimited: Boolean by remember {
        mutableStateOf(
            (isFrequencyAnalysis && settingsVm.disableFrequencyTextCreation)
                    || (!isFrequencyAnalysis && !settingsVm.licenseActivated && OkuTextEntity.getTrialTextLimit().size >= 2)
        )
    }

    var textWasSaved by remember { mutableStateOf(false) }

    LaunchedEffect(textWasSaved) {
        if (textWasSaved == true) {
            isLimited = (isFrequencyAnalysis && settingsVm.disableFrequencyTextCreation)
                    || (!isFrequencyAnalysis && !settingsVm.licenseActivated && OkuTextEntity.getTrialTextLimit().size >= 2)
            delay(3000)
            textWasSaved = false
        }
    }

    LaunchedEffect(true) {
        if (okuTextId == null) return@LaunchedEffect
        readingService.getOkuTextById(okuTextId)?.let { okuText ->
            title = okuText.title
            body = okuText.body
            okutextOld = okuText
            language = allLanguages.values.indexOfFirst { it == DropdownOption(value = okuText.language.label) }
        }
    }

    LaunchedEffect(language) {
        allLanguages[language]?.value?.let {
            selectedOkuLanguage = OkuLanguage.valueByLabel(it)
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        //Title + Language
        Row(
            Modifier.weight(0.1f)
                .padding(bottom = MaterialTheme.spacing.small, end = MaterialTheme.spacing.small)
                .height(IntrinsicSize.Min)
        ) {
            TextInput(
                label = "Title",
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.padding(end = MaterialTheme.spacing.small).weight(0.6f)
            )

            Dropdown(
                modifier = Modifier.weight(0.4f),
                placeholder = "",
                options = allLanguages,
                selectedOption = language,
                onOptionSelected = { language = it },
                label = "Language",
            )
        }

        //Text
        Row(Modifier.weight(0.8f).padding(bottom = MaterialTheme.spacing.small)) {
            TextArea(
                label = "Text",
                value = body,
                onValueChange = { body = it },
                modifier = Modifier.padding(end = MaterialTheme.spacing.small).fillMaxSize(),
                minLines = 80,
            )
        }

        //Buttons
        Row(
            Modifier.weight(0.1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                label = "Save",
                isEnabled = title.isNotBlank() && body.isNotBlank() && !isLimited
                        && (body != okutextOld?.body || title != okutextOld?.title || okutextOld?.language != selectedOkuLanguage),
                onClick = {
                    var okuText = OkuText(
                        id = okuTextId,
                        title = title.trim(),
                        body = body,
                        wordList = processOkuText(body, selectedOkuLanguage),
                        timestampCreated = LocalDateTime.now(),
                        timestampFinished = null,
                        language = selectedOkuLanguage,
                        isFromFrequencyAnalysis = isFrequencyAnalysis
                    )

                    //2. (Re)Insert text
                    okuText = okuText.copy(id = readingService.insertText(okuText, okutextOld))

                    if (!isFrequencyAnalysis) {
                        readingService.loadTextForReaderscreen(okuText)
                        navigator.navigate(Screen.ReaderScreen.name)
                    } else {
                        title = ""
                        body = ""
                        settingsVm.disableFrequencyTextCreation =
                            !settingsVm.licenseActivated && OkuTextEntity.getTrialFrequencyTextLimit().size >= 2
                    }

                    textWasSaved = true
                },
                modifier = Modifier.padding(end = MaterialTheme.spacing.small),
            )

            Button(
                label = "Cancel",
                onClick = { navigator.navigate(Screen.ReadingListScreen.name) },
                buttonType = ButtonType.Secondary,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small),
            )

            if (textWasSaved) {
                Text("Saved", textAlign = TextAlign.Start, color = Color.Blue)
            }
        }

    }

}