package textprocessing

import androidx.compose.ui.res.useResource
import database.OkuText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import opennlp.tools.langdetect.LanguageDetector
import opennlp.tools.langdetect.LanguageDetectorME
import opennlp.tools.langdetect.LanguageDetectorModel
import java.io.InputStream


fun processOkuText(okuText: OkuText): List<String> {
    //1. Detect which language
    CoroutineScope(Dispatchers.IO).launch {
        val nlp = TextProcessing()
        nlp.detectLanguage(okuText)
    }


    //2. Tokenize the words according to language
    return processLatinAlphabet(okuText.body)
}

fun processLatinAlphabet(text: String): List<String> {
    val wordsRegex = "\\W+".toRegex()

    val words = text.split(wordsRegex)
    return words
}