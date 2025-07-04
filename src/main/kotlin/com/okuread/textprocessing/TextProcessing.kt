package com.okuread.textprocessing

import com.huaban.analysis.jieba.JiebaSegmenter
import com.okuread.db.util.LanguageType
import com.okuread.db.util.OkuLanguage
import org.openkoreantext.processor.OpenKoreanTextProcessorJava


private val SPACE_REGEX = " +".toRegex()

private const val SPECIAL_LATIN_CHARACTERS =
    "'äáàâåãāăæçčćéèêëēėęěəíìîïīįıñóòôøõōöúùûüūųßýÿğşšžđxďĉĝĥĵŝŭœðþģķļņąċġħżłńśźțșľť"
private const val CYRILLIC_CHARACTERS =
    "абвгдеёжзийклмнопрстуфхцчшщъыьэюяґђѓєѕіїјљњћќўџѣҷҹһӏӑӓҗӕӗәӛӝӟӡӣӥӧқөӫӭӯӱу́ӳӵӷӹӻӽӿљћы́ќѝџѡѣҿ҃҅҇҉ҋҍҏґіјљћќўџѡѣҥҧѵҩҫҭүұҳҵҷҹһҽҿӂӄӆӈӊӌӎӏӑӓӕӗәӛӝӟӡѳӣӥӧөӫӭӯӱӳӵӷӹӻӽӿ"
private const val ARABIC_CHARACTERS =
    "ىێەﮪجﻭﻭﻭﯙﻥهطغﻡخَڵاﻝﮒزرشﮎﻕقأَحوڤذَﻑصﻍظلْﻉﺵذﺱضسبكبَجِترَفِﮊنـسـدﺯسِڕﺭﺩيحزََءدَمَّـسﺥﺡﭺعَﺝثخﺕﭖﺏﺍآأإؤئبپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیۀۂۄۆۈۊۋیئآإىۉۋېۑۓٰٕٖٜٟٗ٘ٙٚٛٝٞٮٯٱٳٵٷٹٺٻټٽپٿڀځڂڃڄڅچڇڈډڊڋڌڍڎڏڐڑڒړڔڕږڗژڙښڛڜڝڞڟڠڡڢڣڤڥڦڧڨکڪګڬڭڮگڰڱڲڳڴڵڶڷڸڹںڻڼڽھەېےۓۀۖۗۘۙۚۛۜ۝۞ۣ۟۠ۡۢۤۥۦۧۨ۩۪ۭ۫۬ۮۯ"

private val LATIN_CYRILLIC_COMBINED_REGEX =
    ("(?=[^a-zA-Z0-9${CYRILLIC_CHARACTERS + CYRILLIC_CHARACTERS.uppercase() + SPECIAL_LATIN_CHARACTERS + SPECIAL_LATIN_CHARACTERS.uppercase()}])|" +
            "(?<=[^a-zA-Z0-9${CYRILLIC_CHARACTERS + CYRILLIC_CHARACTERS.uppercase() + SPECIAL_LATIN_CHARACTERS + SPECIAL_LATIN_CHARACTERS.uppercase()}])").toRegex()

private val ARABIC_REGEX = ("(?=[^a-zA-Z0-9$ARABIC_CHARACTERS])|(?<=[^a-zA-Z0-9$ARABIC_CHARACTERS])").toRegex()

//private val japaneseTokenizer by lazy { JapaneseTokenizer.createDefaultTokenizer() }
private val chineseTokenizer by lazy { JiebaSegmenter() }

fun processOkuText(text: String, language: OkuLanguage): List<String> {
    //1. Tokenize the words according to languagцe
    return when (language.languageType) {
        LanguageType.ARABIC -> getWordList(text, regex = ARABIC_REGEX)
//            LanguageType.JAPANESE -> japaneseTokenizer.tokenize(okuText.body).map { it.text }
        LanguageType.KOREAN -> {
            val tokens = OpenKoreanTextProcessorJava.tokenize(text)
            OpenKoreanTextProcessorJava.tokensToJavaStringList(tokens)
        }

        LanguageType.CHINESE -> chineseTokenizer.process(text, JiebaSegmenter.SegMode.INDEX).map { it.word }
        else -> getWordList(text, regex = LATIN_CYRILLIC_COMBINED_REGEX)
    } ?: emptyList()
}

/**
 * Processes the words with the given regex (depending on language) and returns the list of words within in that text
 * @param text to analyze
 * @param regex to use
 */
private fun getWordList(text: String, regex: Regex): List<String> {
    return text.trim().replace(SPACE_REGEX, " ").split(regex).filterNot { it.isEmpty() }
}