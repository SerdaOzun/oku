import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import com.okuread.services.ReadingService
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.koin.core.component.inject
import org.testng.annotations.Test
import utils.BaseTest
import utils.generateOkuText

@Test
class UpdateTextTest : BaseTest() {

    private val readingService by inject<ReadingService>()
    private val okuText by lazy { generateOkuText() }

    @Test
    fun updateTextLanguage() {
        val textId = readingService.insertText(okuText)
        val modifiedOkuTet = okuText.copy(id = textId, language = OkuLanguage.GERMAN)
        readingService.insertText(okuText = modifiedOkuTet, okutextOld = okuText)

        withClue(
            "After modifying an english text to german, there should be no english texts in the database." +
                    "English words remain, but with 0 occurrences for each word" +
                    "or words in the database"
        ) {
            OkuTextEntity.getAllIds().size shouldBe 1
            OkuTextEntity.getText(textId)!!.language shouldBe modifiedOkuTet.language
            OkuWordEntity.getWordsByFilter(OkuLanguage.ENGLISH).let {
                it.size shouldBeGreaterThan 1
                it.forAll { it.occurrenceCount shouldBe 0 }
            }
            OkuWordEntity.getWordsByFilter(OkuLanguage.GERMAN).shouldNotBeEmpty()
        }
    }
}