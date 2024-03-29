package textprocessing

import database.OkuText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import opennlp.tools.langdetect.Language
import opennlp.tools.langdetect.LanguageDetector
import opennlp.tools.langdetect.LanguageDetectorME
import opennlp.tools.langdetect.LanguageDetectorModel

class TextProcessing {
    private val inputstream = javaClass.getResourceAsStream("/langdetect-183.bin")
    private val m = LanguageDetectorModel(inputstream)
    private val myCategorizer: LanguageDetector = LanguageDetectorME(m)

    fun detectLanguage(okuText: OkuText) {
        println(myCategorizer.predictLanguage(okuText.body).lang)
    }

}