package com.okuread.screens.frequency

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.button.IconButton
import com.gabrieldrn.carbon.dropdown.Dropdown
import com.gabrieldrn.carbon.dropdown.base.DropdownOption
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.gabrieldrn.carbon.textinput.TextInput
import com.okuread.db.data.OkuTextListItem
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.db.repositories.getTrialFrequencyTextLimit
import com.okuread.db.util.OkuLanguage
import com.okuread.getKoinInstance
import com.okuread.services.ReadingService
import com.okuread.services.SettingsService
import com.okuread.services.TextListService
import com.okuread.ui.components.bottomBorder
import com.okuread.ui.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun FrequencyListScreen(
    textListService: TextListService = getKoinInstance(),
    readingService: ReadingService = getKoinInstance()
) {
    var selectedId by remember { mutableStateOf(-1L) }
    var searchFilter by remember { mutableStateOf("") }
    var listSorting by remember { mutableStateOf(FrequencyListSorting(FrequencyListHeaders.DATE_CREATED, false)) }

    val languagesBeingLearned by remember {
        mutableStateOf(
            readingService.getLanguagesBeingLearned(isFrequencyAnalysis = true)
                .mapIndexed { index, language -> index to DropdownOption(value = language.label) }.toMap()
        )
    }
    var filteredLanguage by remember { mutableStateOf<Int>(0) }

    LaunchedEffect(
        listSorting,
        searchFilter,
        filteredLanguage,
        textListService.triggerTextReload
    ) {
        delay(100)
        val okuLang = languagesBeingLearned[filteredLanguage]?.value?.let {
            OkuLanguage.valueByLabel(it)
        }
        textListService.loadTexts(
            search = searchFilter,
            okuLanguage = okuLang,
            isFromFrequencyAnalysis = true,
            orderByColumn = listSorting.column.toTableColumn(),
            orderAscending = listSorting.ascending
        )
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        val state = rememberLazyListState()

        Row(
            modifier = Modifier.height(IntrinsicSize.Max).padding(bottom = MaterialTheme.spacing.small),
            horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically
        ) {
            tableToolbar(
                searchFilter = searchFilter,
                filteredLanguage = filteredLanguage,
                languagesBeingLearned = languagesBeingLearned,
                selectedOkuText = textListService.okuTexts.firstOrNull { it.id == selectedId },
                textListService = textListService,
                setSearchFilter = { searchFilter = it },
                setFilteredLanguage = { filteredLanguage = it }
            )
        }

        FrequencyTableHeaders(
            modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth()
                .padding(end = MaterialTheme.spacing.medium)
                .background(WhiteTheme.layerAccent01)
                .padding(MaterialTheme.spacing.small),
            listSorting = listSorting,
            setListSorting = { listSorting = it }
        )

        Box(
            modifier = Modifier.weight(0.8f).fillMaxWidth().padding(bottom = MaterialTheme.spacing.medium),
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(end = MaterialTheme.spacing.medium), state = state) {
                itemsIndexed(items = textListService.okuTexts, key = { _, text -> text.id!! }) { _, it ->
                    TextItem(
                        modifier = Modifier,
                        okuText = it,
                        isSelected = selectedId == it.id
                    ) { ot ->
                        selectedId = ot.id!!
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

        Pagination(Modifier, textListService)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TextItem(
    modifier: Modifier = Modifier,
    okuText: OkuTextListItem,
    isSelected: Boolean,
    onClick: (OkuTextListItem) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clickable(onClick = { onClick(okuText) })
            .fillMaxHeight()
            .background(if (isSelected) WhiteTheme.layerSelected01 else WhiteTheme.layer01)
            .bottomBorder(1.dp, WhiteTheme.borderSubtle00)
            .padding(
                top = MaterialTheme.spacing.small,
                start = MaterialTheme.spacing.small,
                bottom = MaterialTheme.spacing.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "", modifier = Modifier.weight(FrequencyListHeaders.FINISHED.weight))
        Text(text = okuText.title, modifier = Modifier.weight(FrequencyListHeaders.NAME.weight))
        Text(
            text = okuText.timestampCreated.toLocalDate().toString(),
            modifier = Modifier.weight(FrequencyListHeaders.DATE_CREATED.weight)
        )
        // Percentage of known words
        Text(
            okuText.percentageKnown.toString(),
            modifier = Modifier.weight(FrequencyListHeaders.KNOWN_WORDS.weight)
        )
        // Count of unique words
        Text(
            okuText.uniqueWordsCount.toString(),
            modifier = Modifier.weight(FrequencyListHeaders.UNIQUE_WORDS.weight)
        )
        // Count of total words
        Text(
            okuText.totalWordsCount.toString(),
            modifier = Modifier.weight(FrequencyListHeaders.TOTAL_WORDS.weight)
        )
    }
}

@Composable
private fun tableToolbar(
    searchFilter: String,
    filteredLanguage: Int,
    languagesBeingLearned: Map<Int, DropdownOption>,
    selectedOkuText: OkuTextListItem?,
    textListService: TextListService,
    setSearchFilter: (String) -> Unit,
    setFilteredLanguage: (Int) -> Unit,
    settingsVm: SettingsService = getKoinInstance()
) {
    val textIsSelected = selectedOkuText != null

    Row(
        modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth().padding(bottom = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.Start,
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextInput(
                label = "Search",
                value = searchFilter,
                onValueChange = setSearchFilter,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small).width(300.dp)
            )
            Dropdown(
                placeholder = "",
                options = languagesBeingLearned,
                selectedOption = filteredLanguage,
                onOptionSelected = setFilteredLanguage,
                modifier = Modifier.width(200.dp).padding(end = MaterialTheme.spacing.small),
                label = "Language",
            )
        }

        Row(modifier = Modifier.fillMaxHeight().fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            IconButton(
                iconPainter = painterResource("icons/trash-2.svg"),
                isEnabled = textIsSelected,
                onClick = {
                    textListService.deleteText(selectedOkuText!!)
                    settingsVm.disableFrequencyTextCreation =
                        !settingsVm.licenseActivated && OkuTextEntity.getTrialFrequencyTextLimit().size >= 2
                },
                buttonType = ButtonType.PrimaryDanger,
            )
        }
    }
}

@Composable
private fun Pagination(modifier: Modifier, textListService: TextListService) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            iconPainter = painterResource("icons/minus.svg"),
            isEnabled = textListService.currentPage > 1,
            onClick = {
                textListService.currentPage--
                textListService.triggerTextReload++
            },
            buttonType = ButtonType.Primary,
        )
        Text(
            text = textListService.currentPage.toString() + "/" + textListService.numberOfPages.toString(),
            fontSize = 18.sp,
            modifier = Modifier.padding(end = MaterialTheme.spacing.small, start = MaterialTheme.spacing.small)
        )
        IconButton(
            iconPainter = painterResource("icons/plus.svg"),
            isEnabled = textListService.currentPage < textListService.numberOfPages,
            onClick = {
                textListService.currentPage++
                textListService.triggerTextReload++
            },
            buttonType = ButtonType.Primary,
        )
    }
}
