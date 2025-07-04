package com.okuread.screens.reading.reader.right.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.ui.theme.spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedSentenceContainer(
    modifier: Modifier,
    clipboardManager: ClipboardManager,
    selectedSentence: String,
    copiedSentence: Boolean,
    setCopiedSentence: (Boolean) -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            //Ausgewählter Satz
            Text(
                "Selected Sentence",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.small,
                    bottom = MaterialTheme.spacing.small,
                    end = MaterialTheme.spacing.small
                )
            )

            if (selectedSentence.isNotBlank()) {
                Icon(
                    painter = painterResource("icons/clipboard.svg"),
                    "",
                    modifier = Modifier.onClick {
                        clipboardManager.setText(AnnotatedString(selectedSentence))
                        setCopiedSentence(true)
                    }
                )
            }
        }

        if (copiedSentence) {
            Text(
                "Copied to clipboard",
                modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                color = WhiteTheme.focus
            )
        }

        SelectionContainer {
            Text(
                text = selectedSentence,
                modifier = Modifier,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}