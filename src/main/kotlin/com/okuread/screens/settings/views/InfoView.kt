package com.okuread.screens.settings.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.okuread.db.repositories.OkuSetting
import com.okuread.db.repositories.SettingsEntity
import com.okuread.db.repositories.getSetting
import com.okuread.licenseKeys.licensingScreen
import com.okuread.services.SettingsService
import com.okuread.ui.theme.spacing
import com.okuread.version


@Composable
fun infoView(settingsService: SettingsService) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Version: $version")

        val annotatedLinkString: AnnotatedString = buildAnnotatedString {

            val str = "okuread.com"
            append(str)
            addStyle(
                style = SpanStyle(
                    color = Color(0xff64B5F6),
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.Underline
                ), start = 0, end = str.length
            )

            // attach a string annotation that stores a URL to the text "link"
            addStringAnnotation(
                tag = "URL",
                annotation = "https://okuread.com",
                start = 0,
                end = str.length
            )

        }

        Row(
            modifier = Modifier.padding(top = MaterialTheme.spacing.medium, bottom = MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Website: ")
            ClickableText(
                modifier = Modifier,
                text = annotatedLinkString,
                onClick = {
                    annotatedLinkString
                        .getStringAnnotations("URL", it, it)
                        .firstOrNull()?.let { stringAnnotation ->
                            uriHandler.openUri(stringAnnotation.item)
                        }
                }
            )
        }

        Row(modifier = Modifier.padding(bottom = MaterialTheme.spacing.small), verticalAlignment = Alignment.CenterVertically) {
            Text("Support: ")
            SelectionContainer { Text("support@okuread.com") }
        }

        if (settingsService.licenseActivated) {
            Text("Licensed to: ${SettingsEntity.getSetting<OkuSetting.LicensedEmail>()}")
        } else {
            licensingScreen { settingsService.licenseActivated = it }
        }
    }
}