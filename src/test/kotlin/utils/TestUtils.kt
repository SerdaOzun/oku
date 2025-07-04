package utils

import com.okuread.db.data.OkuText
import com.okuread.db.util.OkuLanguage
import com.okuread.textprocessing.processOkuText
import java.time.LocalDateTime
import kotlin.random.Random

fun generateOkuText(): OkuText {
    val title = generateRandomString()
    val body = generateRandomString(true)

    return OkuText(
        id = null,
        title = title,
        body = body,
        wordList = processOkuText(body, OkuLanguage.ENGLISH),
        timestampCreated = LocalDateTime.now(),
        timestampFinished = null,
        language = OkuLanguage.ENGLISH
    )
}


private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

private fun generateRandomString(multiple: Boolean = false): String {

    var result: String = ""

    result += (1..10)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")

    if (multiple) {
        repeat(Random.nextInt(10, 200)) {
            result += " "
            result += (1..10)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }
    }

    return result
}