package com.okuread.screens.reading.reader.right.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import com.gabrieldrn.carbon.button.ButtonType
import com.gabrieldrn.carbon.button.IconButton
import com.gabrieldrn.carbon.foundation.color.WhiteTheme
import com.okuread.db.data.OkuWord
import com.okuread.db.util.WordStatus
import com.okuread.services.ReadingService
import com.okuread.ui.theme.spacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedWordContainer(
    modifier: Modifier,
    clipboardManager: ClipboardManager,
    readingService: ReadingService,
    selectedWord: OkuWord?,
    copiedWord: Boolean,
    setCopiedWord: (Boolean) -> Unit,
    onSelectedWord: (OkuWord) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Selected Word",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = MaterialTheme.spacing.small)
            )

            if (selectedWord != null) {
                Icon(
                    painter = painterResource("icons/clipboard.svg"),
                    "",
                    modifier = Modifier.onClick {
                        clipboardManager.setText(AnnotatedString(selectedWord.word))
                        setCopiedWord(true)
                    }
                )
            }
        }

        if (copiedWord) {
            Text(
                "Copied to clipboard",
                modifier = Modifier.padding(top = MaterialTheme.spacing.small),
                color = WhiteTheme.focus
            )
        }

        //Ausgewähltes Wort
        SelectionContainer {
            Text(
                text = selectedWord?.word ?: "",
                modifier = Modifier.padding(
                    top = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.small
                ),
                fontSize = 20.sp
            )
        }

        //Frequency Rank
        if (selectedWord != null) {
            if (!selectedWord.ignoreForFrequencyRanking) {
                Text(
                    text = "Frequency Rank: ${readingService.frequencyRankingMap[selectedWord.word]} / ${readingService.numberOfFrequencyRanks}",
                    modifier = Modifier.padding(top = MaterialTheme.spacing.smaller),
                    fontSize = 14.sp
                )
            }
        }

        //Wordstatus
        if (selectedWord != null) {
            Text(
                text = "Status: ${selectedWord.status.label}",
                modifier = Modifier.padding(top = MaterialTheme.spacing.smaller),
                fontSize = 14.sp
            )
        }

        //Buttons zum Ändern des Wortstatus
        WordStatusButtonPanel(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(top = MaterialTheme.spacing.small),
            selectedWord = selectedWord,
            readingService = readingService,
            onSelect = { onSelectedWord(it) }
        )
    }
}

/**
 * Buttons to change the status of the selected word
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordStatusButtonPanel(
    modifier: Modifier,
    selectedWord: OkuWord?,
    readingService: ReadingService,
    onSelect: (OkuWord) -> Unit
) {
    if (selectedWord != null) {
        FlowRow(modifier, horizontalArrangement = Arrangement.Start) {
            IconButton(
                iconPainter = painterResource("icons/book-open-text.svg"),
                onClick = {
                    val updatedOkuWord =
                        selectedWord.copy(status = WordStatus.byStatuscode(WordStatus.LEARNING.statuscode))
                    readingService.updateWord(updatedOkuWord)
                    onSelect(updatedOkuWord)
                },
                buttonType = ButtonType.Primary
            )

            IconButton(
                iconPainter = painterResource("icons/book-check.svg"),
                onClick = {
                    val updatedOkuWord =
                        selectedWord.copy(status = WordStatus.byStatuscode(WordStatus.KNOWN.statuscode))
                    readingService.updateWord(updatedOkuWord)
                    onSelect(updatedOkuWord)
                },
                buttonType = ButtonType.Secondary,
            )

            IconButton(
                iconPainter = painterResource("icons/book-minus.svg"),
                onClick = {
                    val updatedOkuWord =
                        selectedWord.copy(status = WordStatus.byStatuscode(WordStatus.IGNORED.statuscode))
                    readingService.updateWord(updatedOkuWord)
                    onSelect(updatedOkuWord)
                },
                buttonType = ButtonType.Tertiary,
            )
        }
    }
}
