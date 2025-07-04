import com.okuread.services.ReadingService
import com.okuread.util.isSkippableWord
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.koin.core.component.inject
import org.testng.annotations.Test
import utils.BaseTest
import utils.generateOkuText

class FrequencyListTest : BaseTest() {

    private val readingService by inject<ReadingService>()
    private val okuText by lazy { generateOkuText() }
    private val okuText2 by lazy { generateOkuText() }

    @Test
    fun `Normal Texts are considered for frequency analysis`() {
        val okuTextId = readingService.insertText(okuText)
        readingService.calculateFrequencyRankingForLanguage(okuText.language)

        withClue("The frequency analysis takes into account all distinct and non-skippable words.") {
            okuText.wordList.distinct()
                .filterNot { it.isSkippableWord() }.size shouldBe readingService.frequencyRankingMap.keys.size

            readingService.numberOfFrequencyRanks shouldBe readingService.frequencyRankingMap.values.max()
        }
    }

    @Test(dependsOnMethods = ["Normal Texts are considered for frequency analysis"])
    fun `Frequency Analysis-Texts are considered for frequency analysis`() {
        val okuTextId = readingService.insertText(okuText2.copy(isFromFrequencyAnalysis = true))
        readingService.calculateFrequencyRankingForLanguage(okuText2.language)

        (okuText.wordList.distinct().filterNot { it.isSkippableWord() }.size +
                okuText2.wordList.distinct().filterNot { it.isSkippableWord() }.size) shouldBe
                readingService.frequencyRankingMap.keys.size
    }

}