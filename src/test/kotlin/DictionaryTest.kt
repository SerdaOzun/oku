import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import com.okuread.services.DictionaryDownloadService
import com.okuread.util.OkuConfig
import com.okuread.util.toLocalDate
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import org.koin.core.component.inject
import org.testng.annotations.Test
import utils.BaseTest
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

class DictionaryTest : BaseTest() {
    private val dictDownloadService by inject<DictionaryDownloadService>()
    private val config by inject<OkuConfig>()

    @Test
    fun `Check if all required Dictionaries exist`() {
        OkuLanguage.entries.filterNot { it == OkuLanguage.ALL }.forEach {
            val urls = dictDownloadService.getDictionaryNames(it).map { "${config.dictionariesUrl}/$it" }
            urls.forEach { url ->
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                withClue("Dictionary $url should be available") {
                    response.code shouldBe 200
                }
                withClue("Header includes last modified date for each dictionary") {
                    //No error should be thrown
                    response.headers("Last-Modified").first().toLocalDate()
                }
            }
        }
    }

    @Test
    fun `Download Dictionary and Insert into DB`() = runBlocking {
        val dictionaryDir = Paths.get(getUserDirectory().toString(), "dictionaries")
        Dictionary.delete(OkuLanguage.ROMANI)

        Dictionary.selectWord(OkuLanguage.ROMANI, "rat").shouldBeEmpty()

        //Test won't work for languages that have more than one dictionary, as the dictionaries will be deleted in the forEach
        dictDownloadService.getDictionaryNames(OkuLanguage.ROMANI).forEach { dictName ->
            val dictPath = Paths.get(dictionaryDir.toString(), dictName.substringBeforeLast(".gz"))
            dictPath.deleteIfExists()
            dictPath.exists() shouldBe false

            dictDownloadService.downloadAndSaveDictionary(OkuLanguage.ROMANI)
            dictPath.exists() shouldBe true
            dictPath.fileName.toString() shouldBe "Romani_dict.jsonl"
        }

        InstalledDictionaries.getInstalledDictionaries().map { it.first } shouldContain OkuLanguage.ROMANI
        Dictionary.selectWord(OkuLanguage.ROMANI, "rat").size shouldBe 2
    }
}