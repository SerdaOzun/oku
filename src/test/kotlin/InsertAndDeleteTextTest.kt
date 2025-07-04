import com.okuread.db.data.toOkuTextListItem
import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import com.okuread.services.ReadingService
import com.okuread.services.TextListService
import com.okuread.textprocessing.processOkuText
import io.kotest.assertions.withClue
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import org.koin.core.component.inject
import org.testng.annotations.Test
import utils.BaseTest
import utils.generateOkuText

@Test
class InsertAndDeleteTextTest : BaseTest() {

    private val readingService by inject<ReadingService>()
    private val textListService by inject<TextListService>()
    private lateinit var wordIds: Set<Long>
    private val okuText by lazy { generateOkuText() }
    private var okuTextId: Long = -1

    @Test
    fun insertText() {
        okuTextId = readingService.insertText(okuText)

        withClue("Saving an Okutext inserts it to the database") {
            OkuTextEntity.getAllIds().size shouldBe 1

            OkuTextEntity.getText(okuTextId)!!.run {
                okuTextId = id!!
                title shouldBe okuText.title
                body shouldBe okuText.body
                language shouldBe okuText.language
                timestampCreated shouldBeGreaterThan okuText.timestampCreated
                isFromFrequencyAnalysis shouldBe false
                wordIds = okuWordIdSet!!.also { it.size shouldBeGreaterThan 1 }
            }
        }

        withClue("All words from the text should have also been inserted into the words table") {
            OkuWordEntity.getWordsByFilter(okuText.language).run {
                size shouldBe wordIds.size
                this.map { it.id } shouldContainExactlyInAnyOrder wordIds
                this.map { it.word } shouldContainExactlyInAnyOrder processOkuText(
                    okuText.body,
                    OkuLanguage.ENGLISH
                ).distinct().map { it.lowercase() }
            }
        }
    }

    @Test(dependsOnMethods = ["insertText"])
    fun deleteText() {
        textListService.deleteText(okuText.copy(id = okuTextId).toOkuTextListItem())

        withClue("After deleting the text it's gone from the database, while the words remain with 0 occurrences each") {
            OkuTextEntity.getAllIds().size shouldBe 0
            OkuWordEntity.getWordsByFilter(OkuLanguage.ENGLISH).apply {
                size shouldBe wordIds.size
                forAll { it.occurrenceCount shouldBe 0 }
            }
        }
    }

}