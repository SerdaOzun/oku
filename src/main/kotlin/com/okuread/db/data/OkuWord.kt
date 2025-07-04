package com.okuread.db.data

import androidx.compose.runtime.Immutable
import com.okuread.db.repositories.LanguageEntity
import com.okuread.db.repositories.OkuWordEntity
import com.okuread.db.util.OkuLanguage
import com.okuread.db.util.WordStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

@Immutable
@Serializable
data class OkuWord(
    val id: Long?,
    val word: String,
    val status: WordStatus,
    @Contextual val learningStart: LocalDateTime?,
    @Contextual val learningFinished: LocalDateTime?,
    val language: OkuLanguage,
    val occurrenceCount: Int,
    val ignoreForFrequencyRanking: Boolean = false,
) {
    constructor(resultRow: ResultRow) : this(
        id = resultRow[OkuWordEntity.id].value,
        word = resultRow[OkuWordEntity.word],
        status = WordStatus.byStatuscode(resultRow[OkuWordEntity.wordstatus]),
        learningStart = resultRow[OkuWordEntity.learning_start],
        learningFinished = resultRow[OkuWordEntity.learning_finished],
        language = OkuLanguage.valueByLabel(resultRow[LanguageEntity.language]),
        occurrenceCount = resultRow[OkuWordEntity.occurrence_count],
        ignoreForFrequencyRanking = resultRow[OkuWordEntity.ignoreForFrequencyRanking]
    )
}
