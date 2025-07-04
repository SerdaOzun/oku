package com.okuread.screens.settings.views

import androidx.compose.foundation.*
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
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.ui.theme.spacing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


private val jsonString by lazy {
    object {}.javaClass.getResourceAsStream("/licensing/licenses.json")?.bufferedReader()?.readText()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LicensesView() {
    jsonString?.let { jsonString ->
        val json = Json.decodeFromString<LicenseList>(jsonString)

        var selectedLicense by remember { mutableStateOf(json.licenses.firstOrNull() ?: License()) }
        val scroll = rememberScrollState(0)

        Row(Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.3f)) {
                json.licenses.forEach {
                    Row(
                        Modifier.fillMaxWidth().clickable { selectedLicense = it }
                            .background(if (it.license == selectedLicense.license) WhiteTheme.layerAccent01 else Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                            if (selectedLicense.license == it.license) {
                                Divider(modifier = Modifier.fillMaxHeight().width(5.dp), color = WhiteTheme.focus)
                            }
                            Text(it.license, modifier = Modifier.padding(MaterialTheme.spacing.small), fontSize = 16.sp)
                        }
                    }
                }
            }
            Text(
                text = "${selectedLicense.link}\n\n${selectedLicense.text}",
                modifier = Modifier.weight(0.7f).padding(start = MaterialTheme.spacing.medium)
                    .verticalScroll(state = scroll)
            )
        }
    } ?: Text("Could not load licenses file")
}

@Serializable
private data class LicenseList(
    val licenses: List<License>
)

@Serializable
private data class License(
    val license: String = "",
    val link: String = "",
    val text: String = ""
)