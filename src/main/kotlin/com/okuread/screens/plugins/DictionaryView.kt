package com.okuread.screens.plugins

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonSize
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.dropdown.Dropdown
import com.gabrieldrn.carbon.dropdown.base.DropdownOption
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.gabrieldrn.carbon.loading.SmallLoading
import com.gabrieldrn.carbon.textinput.TextInput
import com.okuread.db.util.OkuLanguage
import com.okuread.getKoinInstance
import com.okuread.services.AvailableDictionary
import com.okuread.services.DictionaryDownloadService
import com.okuread.ui.components.DialogWithCancel
import com.okuread.ui.components.DialogWithConfirmAndCancel
import com.okuread.ui.components.bottomBorder
import com.okuread.ui.theme.spacing
import com.okuread.util.checkInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DictionariesView(
    dictionaryDownloadService: DictionaryDownloadService = getKoinInstance<DictionaryDownloadService>(),
) {
    val coroutineScope = rememberCoroutineScope()
    var showNoConnectionDialog by remember { mutableStateOf(false) }

    var searchFilter by remember { mutableStateOf("") }
    var dictFilter by remember { mutableStateOf<Int>(0) }
    var dictFilterOptions by remember {
        mutableStateOf(DictionaryViewFilter.entries.mapIndexed { index, entry -> index to DropdownOption(entry.label) }
            .toMap())
    }

    var dictsWithUpdateAvailable by remember { mutableStateOf(emptySet<OkuLanguage>()) }
    var allDictionaries by remember { mutableStateOf(dictionaryDownloadService.getDictionaries()) }
    var filteredDictionaries by remember { mutableStateOf(dictionaryDownloadService.getDictionaries()) }

    if (showNoConnectionDialog) {
        DialogWithCancel(
            modifier = Modifier.width(400.dp),
            message = "No Internet Connection",
            content = { Text("Please make sure that you have an active internet connection if you would like to install a dictionary.") },
            onDismiss = {
                showNoConnectionDialog = false
            }
        )
    }

    LaunchedEffect(searchFilter, dictFilter, allDictionaries) {
        delay(100)
        filteredDictionaries = allDictionaries
            .filter {
                if (searchFilter.isNotEmpty()) {
                    it.dictName.lowercase().contains(searchFilter.lowercase())
                } else true
            }
            .filter {
                when (DictionaryViewFilter.byLabel(dictFilterOptions[dictFilter]?.value) ?: DictionaryViewFilter.ALL) {
                    DictionaryViewFilter.ALL -> true
                    DictionaryViewFilter.INSTALLED -> it.installed == true
                } == true
            }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val state = rememberLazyListState()

        DictionaryHeaderRow(
            coroutineScope = coroutineScope,
            searchFilter = searchFilter,
            setSearchFilter = { searchFilter = it },
            listFilter = dictFilter,
            listFilterOptions = dictFilterOptions,
            setListFilterOptions = { dictFilter = it },
            dictionaryDownloadService = dictionaryDownloadService,
            setDictsWithUpdate = { dictsWithUpdateAvailable = it },
            setNoInternet = { showNoConnectionDialog = true },
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
                itemsIndexed(items = filteredDictionaries, key = { _, dict -> dict.dictName }) { index, dict ->
                    DictionaryItem(
                        coroutineScope = coroutineScope,
                        index = index,
                        availableDictionary = dict,
                        dictionaryDownloadService = dictionaryDownloadService,
                        updateAvailable = dictsWithUpdateAvailable.contains(dict.okuLanguage),
                        updateDictionaries = { allDictionaries = dictionaryDownloadService.getDictionaries() },
                        setNoInternet = { showNoConnectionDialog = true }
                    )
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

@Composable
private fun DictionaryItem(
    coroutineScope: CoroutineScope,
    index: Int,
    availableDictionary: AvailableDictionary,
    dictionaryDownloadService: DictionaryDownloadService,
    updateAvailable: Boolean,
    updateDictionaries: () -> Unit,
    setNoInternet: () -> Unit
) {
    var showLoadingSpinner by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DialogWithConfirmAndCancel(
            "Delete",
            content = { Text("Delete ${availableDictionary.okuLanguage.label} dictionary?") },
            confirmButtonLabel = "Delete",
            onConfirm = {
                showLoadingSpinner = true
                coroutineScope.launch(Dispatchers.IO) {
                    dictionaryDownloadService.deleteDictionary(availableDictionary.okuLanguage)
                    updateDictionaries()
                    showLoadingSpinner = false
                }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false })
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(end = MaterialTheme.spacing.small)
            .background(if (index % 2 == 0) WhiteTheme.layer01 else Color.Transparent)
            .bottomBorder(1.dp, WhiteTheme.borderSubtle00).padding(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Text(availableDictionary.dictName)
        if (updateAvailable) {
            Text(" - Update available", modifier = Modifier.padding(start = MaterialTheme.spacing.smaller))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showLoadingSpinner) {
                Text("This can take a few minutes...")
                SmallLoading(
                    modifier = Modifier.padding(
                        end = MaterialTheme.spacing.small,
                        start = MaterialTheme.spacing.small
                    )
                )
            }

            if (updateAvailable) {
                Button(
                    label = "Update",
                    onClick = {
                        showLoadingSpinner = true
                        coroutineScope.launch(Dispatchers.IO) {
                            if (!checkInternetConnection()) {
                                setNoInternet()
                                showLoadingSpinner = false
                                return@launch
                            }
                            dictionaryDownloadService.downloadAndSaveDictionary(availableDictionary.okuLanguage).let {
                                showLoadingSpinner = false
                            }
                            updateDictionaries()
                        }
                    },
                    buttonSize = ButtonSize.Small,
                    modifier = Modifier.padding(end = MaterialTheme.spacing.small)
                )
            }
            if (availableDictionary.installed) {
                Button(
                    label = "Delete",
                    onClick = { showDeleteDialog = true },
                    buttonSize = ButtonSize.Small,
                    buttonType = ButtonType.PrimaryDanger
                )
            } else {
                Button(
                    label = "Install",
                    onClick = {
                        showLoadingSpinner = true
                        coroutineScope.launch(Dispatchers.IO) {
                            if (!checkInternetConnection()) {
                                setNoInternet()
                                showLoadingSpinner = false
                                return@launch
                            }
                            dictionaryDownloadService.downloadAndSaveDictionary(availableDictionary.okuLanguage).let {
                                showLoadingSpinner = false
                            }
                            updateDictionaries()
                        }
                    },
                    buttonSize = ButtonSize.Small
                )
            }
        }
    }
}

@Composable
private fun DictionaryHeaderRow(
    coroutineScope: CoroutineScope,
    searchFilter: String,
    setSearchFilter: (String) -> Unit,
    listFilter: Int,
    listFilterOptions: Map<Int, DropdownOption>,
    setListFilterOptions: (Int) -> Unit,
    dictionaryDownloadService: DictionaryDownloadService,
    setDictsWithUpdate: (Set<OkuLanguage>) -> Unit,
    setNoInternet: () -> Unit
) {
    var showLoadingSpinner by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth().padding(bottom = MaterialTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
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
                options = listFilterOptions,
                selectedOption = listFilter,
                onOptionSelected = setListFilterOptions,
                modifier = Modifier.width(200.dp).padding(end = MaterialTheme.spacing.small),
                label = "Show Dictionaries",
            )
        }

        Row(modifier = Modifier.fillMaxHeight().fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Button(
                label = "Check for updates",
                onClick = {
                    showLoadingSpinner = true
                    coroutineScope.launch(Dispatchers.IO) {
                        if (!checkInternetConnection()) {
                            setNoInternet()
                            showLoadingSpinner = false
                            return@launch
                        }
                        dictionaryDownloadService.checkDictionaryUpdateAvailable().let {
                            setDictsWithUpdate(it)
                            showLoadingSpinner = false
                        }
                    }
                }
            )

            if (showLoadingSpinner) {
                SmallLoading(modifier = Modifier.padding(start = MaterialTheme.spacing.small))
            }
        }
    }
}

private enum class DictionaryViewFilter(val label: String) {
    ALL("All"),
    INSTALLED("Installed");

    override fun toString(): String {
        return this.label
    }

    companion object {
        fun byLabel(label: String?): DictionaryViewFilter? =
            if (label != null) entries.firstOrNull { it.label == label } else null
    }
}