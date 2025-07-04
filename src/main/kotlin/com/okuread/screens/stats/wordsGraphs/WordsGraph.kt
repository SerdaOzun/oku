package com.okuread.screens.stats.wordsGraphs

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.okuread.db.util.WordStatus
import com.okuread.services.StatsService
import com.okuread.ui.theme.spacing
import com.okuread.util.isSkippableWord
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.KoalaPlotTheme
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Composable
fun OkuGraphsView(statsService: StatsService) {
    LaunchedEffect(statsService.statsState) {
        delay(25)
        if (statsService.statsState.filteredLanguage != null) {
            statsService.refreshWordList()
        }
    }

    if (statsService.wordList.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(start = MaterialTheme.spacing.medium)) {
            val state = rememberLazyListState()

            LazyColumn(Modifier.fillMaxSize().padding(end = MaterialTheme.spacing.medium), state = state) {
                item {
                    WordStatusBarGraph(statsService, Modifier.padding(bottom = MaterialTheme.spacing.medium))
                }
                item {
                    LineGraphWordsKnownAndLearning(statsService)
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

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun WordStatusBarGraph(statsService: StatsService, modifier: Modifier) {
    val wordStatusLabels = listOf("Unknown", "Ignored", "Learning", "Known")
    val wordStatusData = listOf(WordStatus.UNKNOWN, WordStatus.IGNORED, WordStatus.LEARNING, WordStatus.KNOWN)
        .map { status -> statsService.wordList.filter { it.okuWord.status == status }.filterNot { it.okuWord.word.isSkippableWord() } }
        .map { it.size.toFloat() }

    val colors = listOf(Color.Yellow, Color.LightGray, Color.Green, Color.Black)

    if (wordStatusData.isNotEmpty()) {
        Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Word status", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            XYGraph(
                xAxisModel = remember { CategoryAxisModel(wordStatusLabels) },
                yAxisModel = rememberFloatLinearAxisModel(0f..statsService.wordList.size.toFloat(), minorTickCount = 0),
                yAxisTitle = "# Words",
                modifier = Modifier.height(400.dp)
            ) {
                VerticalBarPlot(
                    xData = wordStatusLabels,
                    yData = wordStatusData,
                    bar = {
                        DefaultVerticalBar(SolidColor(colors[it]))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun LineGraphWordsKnownAndLearning(statsService: StatsService) {

    @OptIn(ExperimentalKoalaPlotApi::class)
    @Composable
    fun XYGraphScope<Long, Int>.chart(data: List<DefaultPoint<Long, Int>>, color: Color) {
        LinePlot(
            data = data,
            lineStyle = LineStyle(
                brush = SolidColor(color),
                strokeWidth = 2.dp
            ),
        )
    }

    fun getData(zoneOffset: ZoneOffset): List<DefaultPoint<Long, Int>> {
        return buildList {
            statsService.wordList
                .filter { it.okuWord.status in listOf(WordStatus.LEARNING, WordStatus.KNOWN) }
                .groupingBy {
                    it.okuWord.learningFinished?.toLocalDate()?.atStartOfDay(zoneOffset)?.toEpochSecond()
                }
                .eachCount()
                .toMutableMap()
                .apply {
                    //add a day before all data with the value zero to set a starting position
                    this.keys.filterNotNull().minOfOrNull { it }.let { firstDateEntry ->
                        firstDateEntry?.let {
                            LocalDateTime.ofEpochSecond(firstDateEntry, 0, OffsetDateTime.now().offset).toLocalDate()
                                .minusDays(1).atStartOfDay(zoneOffset).toEpochSecond().let { theDayBefore ->
                                    this[theDayBefore] = 0
                                }
                        }
                    }
                }
                .toSortedMap(compareBy { it })
                .filterNot { it.key == null }
                .entries.fold(mutableMapOf<Long, Int>()) { acc, entry ->
                    val (key, count) = entry
                    val cumulativeSum = (acc.values.lastOrNull() ?: 0) + count
                    acc[key!!] = cumulativeSum
                    acc
                }
                .mapNotNull { add(DefaultPoint(it.key, it.value)) }
        }
    }

    val zoneOffset = OffsetDateTime.now().offset
    val knownData = getData(zoneOffset)

    if (knownData.isNotEmpty()) {
        Column(Modifier.fillMaxWidth().height(500.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Words known over time", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            ChartLayout(
                modifier = Modifier.padding(end = 16.dp),
                legendLocation = LegendLocation.BOTTOM
            ) {

                KoalaPlotTheme(axis = KoalaPlotTheme.axis.copy(minorGridlineStyle = null)) {
                    XYGraph(
                        xAxisModel = LongLinearAxisModel((knownData.first().x..(knownData.last().x + 86400))),
                        yAxisModel = IntLinearAxisModel(0..(knownData.maxOf { it.y } + 50)),
                        xAxisLabels = {
                            LocalDateTime.ofEpochSecond(it, 0, OffsetDateTime.now().offset).toLocalDate().toString()
                        },
                        xAxisStyle = rememberAxisStyle(labelRotation = 90),
                        xAxisTitle = "Time",
                        yAxisTitle = "# Words"
                    ) {
                        chart(knownData, Color.Black)
                    }
                }
            }
        }
    }
}
