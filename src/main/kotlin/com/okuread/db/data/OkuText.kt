package com.okuread.db.data

import androidx.compose.runtime.Immutable
import com.okuread.db.repositories.LanguageEntity
import com.okuread.db.repositories.OkuTextEntity
import com.okuread.db.util.OkuLanguage
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

@Immutable
data class OkuText(
    val id: Long?,
    val title: String,
    val body: String,
    val wordList: List<String>, // the whole text split into Strings
    val okuWordIdSet: Set<Long>? = null, // id list of all okuWords that this text is using
    val timestampCreated: LocalDateTime,
    val timestampFinished: LocalDateTime?,
    val language: OkuLanguage,
    val isFromFrequencyAnalysis: Boolean = false,
    val currentPage: Int = 1,
    val updateMetrics: Boolean = true,
    val percentageKnown: Int = -1,
    val uniqueWordsCount: Int = -1,
    val totalWordsCount: Int = -1
) {
    constructor(resultRow: ResultRow) : this(
        id = resultRow[OkuTextEntity.id].value,
        title = resultRow[OkuTextEntity.title],
        wordList = Json.decodeFromString(resultRow[OkuTextEntity.wordList]),
        okuWordIdSet = resultRow[OkuTextEntity.okuWordSet]?.let { Json.decodeFromString(it) },
        body = resultRow[OkuTextEntity.body],
        timestampCreated = resultRow[OkuTextEntity.timestamp_created],
        timestampFinished = resultRow[OkuTextEntity.timestamp_finished],
        language = OkuLanguage.valueByLabel(resultRow[LanguageEntity.language]),
        isFromFrequencyAnalysis = resultRow[OkuTextEntity.frequency_analysis],
        currentPage = resultRow[OkuTextEntity.currentPage],
        updateMetrics = resultRow[OkuTextEntity.updateMetrics],
        percentageKnown = resultRow[OkuTextEntity.percentageKnown],
        uniqueWordsCount = resultRow[OkuTextEntity.uniqueWordsCount],
        totalWordsCount = resultRow[OkuTextEntity.totalWordsCount]
    )

    override fun toString(): String {
        return title
    }
}

@Immutable
data class OkuTextListItem(
    val id: Long?,
    val title: String,
    val timestampCreated: LocalDateTime,
    val timestampFinished: LocalDateTime?,
    val language: OkuLanguage,
    val isFromFrequencyAnalysis: Boolean = false,
    val currentPage: Int = 1,
    val updateMetrics: Boolean = true,
    val percentageKnown: Int,
    val uniqueWordsCount: Int,
    val totalWordsCount: Int
) {
    constructor(resultRow: ResultRow) : this(
        id = resultRow[OkuTextEntity.id].value,
        title = resultRow[OkuTextEntity.title],
        timestampCreated = resultRow[OkuTextEntity.timestamp_created],
        timestampFinished = resultRow[OkuTextEntity.timestamp_finished],
        language = OkuLanguage.valueByLabel(resultRow[LanguageEntity.language]),
        isFromFrequencyAnalysis = resultRow[OkuTextEntity.frequency_analysis],
        currentPage = resultRow[OkuTextEntity.currentPage],
        updateMetrics = resultRow[OkuTextEntity.updateMetrics],
        percentageKnown = resultRow[OkuTextEntity.percentageKnown],
        uniqueWordsCount = resultRow[OkuTextEntity.uniqueWordsCount],
        totalWordsCount = resultRow[OkuTextEntity.totalWordsCount]
    )

    override fun toString(): String {
        return title
    }
}

fun OkuText.toOkuTextListItem() = OkuTextListItem(
    id = id,
    title = title,
    timestampCreated = timestampCreated,
    timestampFinished = timestampFinished,
    language = language,
    isFromFrequencyAnalysis = isFromFrequencyAnalysis,
    currentPage = currentPage,
    updateMetrics = updateMetrics,
    percentageKnown = percentageKnown,
    uniqueWordsCount = uniqueWordsCount,
    totalWordsCount = totalWordsCount
)