package com.okuread.screens.reading.reader.left

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.okuread.db.data.OkuWord
import com.okuread.db.util.WordStatus
import com.okuread.services.ReadingService
import com.okuread.ui.components.bottomBorder
import com.okuread.ui.theme.spacing
import com.okuread.ui.utils.measureTextWidth
import kotlinx.coroutines.delay
import kotlin.math.ceil

@Composable
fun ContentLazyColumn(
    rService: ReadingService,
    scrollState: LazyListState,
    fontSize: TextUnit,
    rtlLanguage: Boolean,
    content: @Composable (Int, OkuWord, String) -> Unit
) {
    val density = LocalDensity.current

    var maxWidth by remember { mutableStateOf(0.dp) }

    LaunchedEffect(fontSize) {
        delay(150)
        rService.okuWordsSublist = rService.createChunkedWordlist(maxWidth)
        rService.numberOfPages =
            ceil(rService.okuWordsSublist.sumOf { it.size }.toDouble() / rService.wordsPerPage).toInt()
        if (rService.currentPage > rService.numberOfPages) {
            rService.currentPage = rService.numberOfPages
        }
    }

    LaunchedEffect(rService.okuWordsSublist, rService.currentPage) {
        delay(10)
        if (rService.okuWordsSublist.isNotEmpty()) {
            rService.lineOffset = rService.okuWordsSublist.computeLineOffset(rService)
        }
    }

    //Calculate word widths in dp
    rService.currentOkuText!!.okuText.wordList.distinct().forEach {
        //Add 2.dp due to a spacer after each word
        val width = measureTextWidth(it, TextStyle(fontSize = fontSize)) + 2.dp
        rService.wordDpMap[it] = width
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(
            end = if (!rtlLanguage) MaterialTheme.spacing.medium else 0.dp,
            start = if (rtlLanguage) MaterialTheme.spacing.medium else 0.dp
        ).onGloballyPositioned { layoutCoordinates ->
            // Convert pixels to dp
            maxWidth = with(density) { layoutCoordinates.size.width.toDp() }
        },
        state = scrollState
    ) {

        if (rService.okuWordsSublist.isNotEmpty()) {
            //Each sublist has the exact number of words that fit into a single row, considering the maximum row width
            itemsIndexed(
                items = rService.okuWordsSublist.subList(
                    rService.lineOffset.first.coerceAtMost(rService.okuWordsSublist.size),
                    rService.lineOffset.last.coerceAtMost(rService.okuWordsSublist.size)
                ),
                key = { index, sublist -> index }) { sublistIndex, sublist ->

                Row {
                    sublist.forEach { wordString ->
                        val okuWord: OkuWord? = rService.currentOkuText!!.okuWords[wordString.first.lowercase()]
                        content(wordString.second, okuWord!!, wordString.first)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordItem(
    originalWord: String,
    word: OkuWord,
    readingService: ReadingService,
    fontSize: TextUnit,
    isSelected: Boolean,
    wordIndex: Int,
    rightClickIndex: Int,
    onClick: (OkuWord, Int) -> Unit,
    onRightClick: (Int) -> Unit
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        if (isSelected) {
            Divider(Modifier.width(1.dp).fillMaxSize(), color = Color.Red)
        }
        ClickableText(
            AnnotatedString(originalWord),
            modifier = Modifier
                .bottomBorder(
                    strokeWidth = 1.dp,
                    color = if (isSelected || rightClickIndex == wordIndex) Color.Red else Color.Transparent
                )
                .padding(
                    top = MaterialTheme.spacing.smallAf,
                    bottom = MaterialTheme.spacing.smallAf,
                )
                //Rechtsklick
                .onClick(
                    matcher = PointerMatcher.mouse(PointerButton.Secondary),
                    onClick = { onRightClick(wordIndex) })
                .background(color = word.status.color),
            style = TextStyle(fontSize = fontSize),
            //Linksklick
            onClick = {
                if (!isSelected) {
                    onRightClick(-1) //Reset rechtsklick index
                    onClick(word, wordIndex)
                } else {
                    val updatedWord = word.copy(status = WordStatus.byStatuscode(word.status.nextStageOnClick))
                    onRightClick(-1) //Reset rechtsklick index
                    onClick(updatedWord, wordIndex)
                    readingService.updateWord(updatedWord)
                }
            })
        if (rightClickIndex == wordIndex) {
            Divider(Modifier.width(1.dp).fillMaxSize(), color = Color.Red)
        }
        Spacer(Modifier.width(MaterialTheme.spacing.smallest).height(IntrinsicSize.Min))
    }

}

/**
 * computes the line offset (starting at 0) that contains the required number of words
 */
private fun List<List<Pair<String, Int>>>.computeLineOffset(readingService: ReadingService): IntRange {
    val wordsTotal = when {
        readingService.currentPage == 1 -> 0

        else -> {
            val tmpOffset = (readingService.currentPage - 1) * readingService.wordsPerPage
            if (tmpOffset == 0) readingService.wordsPerPage else tmpOffset
        }
    }

    //drop the first 0
    val runningTotals = runningFold(0) { acc, list -> acc + list.size }.drop(1)
    val offsetStart = runningTotals.indexOfFirst { wordsTotal <= it }
    val offsetEnd = runningTotals.indexOfFirst { (wordsTotal + readingService.wordsPerPage) <= it }
    return offsetStart..(if (offsetEnd < 0) this.size else offsetEnd)
}