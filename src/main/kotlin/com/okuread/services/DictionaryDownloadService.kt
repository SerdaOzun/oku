package com.okuread.services

import com.google.gson.Gson
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import com.okuread.db.data.DictionaryInsert
import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import com.okuread.licenseKeys.client
import com.okuread.util.OkuConfig
import com.okuread.util.toLocalDate
import kotlinx.coroutines.coroutineScope
import okhttp3.Request
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.GZIPInputStream
import kotlin.io.path.absolutePathString
import kotlin.io.path.bufferedReader
import kotlin.io.path.exists
import kotlin.system.measureTimeMillis

class DictionaryDownloadService : KoinComponent {
    private val config by inject<OkuConfig>()
    private val dictionaryDir = Paths.get(getUserDirectory().toString(), "dictionaries")
    private val log = LoggerFactory.getLogger(javaClass)
    private val combinedDictionaryLanguages = listOf(
        OkuLanguage.MOLDOVAN, OkuLanguage.ROMANIAN,
        OkuLanguage.BOSNIAN, OkuLanguage.CROATIAN, OkuLanguage.MONTENEGRIN, OkuLanguage.SERBIAN
    )

    val batchInsertCount = 50

    suspend fun downloadAndSaveDictionary(okuLanguage: OkuLanguage) = coroutineScope {
        try {
            downloadDictionary(okuLanguage)
        } catch (e: Exception) {
            log.error("Downloading and installing dictionary failed", e)
        }
    }

    private suspend fun downloadDictionary(okuLanguage: OkuLanguage) = coroutineScope {
        if (!dictionaryDir.exists()) {
            Files.createDirectories(dictionaryDir)
        }

        //Delete existing dictionary words from database
        Dictionary.delete(okuLanguage)

        getDictionaryNames(okuLanguage).forEach { dictName ->
            val request = Request.Builder().url("${config.dictionariesUrl}/$dictName").build()
            val response = client.newCall(request).execute()
            val dictPath = Paths.get(dictionaryDir.toString(), dictName.substringBeforeLast(".gz"))

            if (!response.isSuccessful) {
                log.error("Failed to download file ${dictName}: ${response.code}")
                return@forEach
            }

            val compressedInputStream: InputStream? = response.body?.byteStream()
            compressedInputStream.use { input ->
                if (input != null) {
                    GZIPInputStream(input).use { gzipInputStream ->
                        FileOutputStream(File(dictPath.toString())).use { outputStream ->
                            gzipInputStream.copyTo(outputStream)
                        }
                    }
                    log.info("Downloaded dictionary to ${dictPath.absolutePathString()}")
                    saveDictionaryInDb(dictPath, okuLanguage)
                } else {
                    log.error("Failed to unzip inputstream for dictionary $dictName.")
                }
            }
        }
    }

    /**
     * If a dictionary is no longer in the file system or
     * the update_at of it is older than what's online, an update is available
     */
    suspend fun checkDictionaryUpdateAvailable(): Set<OkuLanguage> = coroutineScope {
        var dictionariesWithUpdateAvailable = mutableSetOf<OkuLanguage>()

        InstalledDictionaries.getInstalledDictionaries().also {
            log.info("Checking updates for ${it.size} dictionaries")
        }.forEach { (lang, localLastUpdated) ->
            getDictionaryNames(lang).forEach { dictName ->
                val request = Request.Builder().url("${config.dictionariesUrl}/$dictName").build()
                val response = client.newCall(request).execute()
                val modifiedDate = response.headers("Last-Modified").first().toLocalDate()

                if (modifiedDate > localLastUpdated) {
                    dictionariesWithUpdateAvailable.add(lang)
                }
            }
        }

        if (dictionariesWithUpdateAvailable.isNotEmpty()) {
            log.info("Dictionary updates available for ${dictionariesWithUpdateAvailable.size} dictionaries")
        } else {
            log.info("No dictionary updates found")
        }

        dictionariesWithUpdateAvailable
    }

    fun deleteDictionary(okuLanguage: OkuLanguage) {
        try {
            Dictionary.delete(okuLanguage)
            InstalledDictionaries.delete(okuLanguage)
            log.info("Deleted dictionary for ${okuLanguage.label}")
        } catch (e: Exception) {
            log.error("An error occurred when trying to delete Dictionary for ${okuLanguage.label}", e)
        }
    }

    private fun saveDictionaryInDb(dictPath: Path, okuLanguage: OkuLanguage) {
        val dictDialect = if (dictPath.fileName.toString().contains("-")) {
            dictPath.fileName.toString().substringAfter("-").substringBefore("_")
        } else ""

        val gson = Gson()

        val batchData = mutableListOf<DictionaryInsert>()

        try {
            val timeTaken = measureTimeMillis {
                dictPath.bufferedReader().use { reader ->
                    reader.forEachLine { line ->

                        val reader = JsonReader(StringReader(line)).apply { strictness = Strictness.LENIENT }
                        val word = gson.fromJson<DictionaryWord>(reader, DictionaryWord::class.java).word
                        val entry = DictionaryInsert(word, line, dictDialect)

                        batchData.add(entry)

                        if (batchData.size % batchInsertCount == 0) {
                            Dictionary.insertData(okuLanguage, batchData)
                            batchData.clear()
                        }
                    }

                    if (batchData.isNotEmpty()) {
                        Dictionary.insertData(okuLanguage, batchData)
                    }
                }

                InstalledDictionaries.insertDictionary(okuLanguage)
            }

            log.info(
                "Successfully saved dictionary '${dictPath.fileName.toString().substringBefore(".")}' " +
                        "into database in $timeTaken ms"
            )
        } catch (e: Exception) {
            log.error("Could not save dictionary '$dictPath' in database", e)
        }
    }

    /**
     * @return Return list of Dictionary names as available in the s3 bucket. Might be multiple in case of languages like Norwegian
     * @param okuLanguage
     */
    fun getDictionaryNames(okuLanguage: OkuLanguage): List<String> = when (okuLanguage) {
        OkuLanguage.BOSNIAN, OkuLanguage.CROATIAN, OkuLanguage.MONTENEGRIN, OkuLanguage.SERBIAN -> listOf("Serbian_dict.jsonl.gz")
        OkuLanguage.MOLDOVAN, OkuLanguage.ROMANIAN -> listOf("Romanian_dict.jsonl.gz")
        OkuLanguage.NORWEGIAN -> listOf(
            "Norwegian-Bokmal_dict.jsonl.gz",
            "Norwegian-Nynorsk_dict.jsonl.gz"
        )

        else -> listOf(okuLanguage.label.replace(" ", "-").plus("_dict.jsonl.gz"))
    }

    /**
     * return list of all available Dictionaries
     * !IMPORTANT. Special cases where some languages all share the same dictionary
     */
    fun getDictionaries(): List<AvailableDictionary> {
        val installed = InstalledDictionaries.getInstalledDictionaries().map { it.first }
        val langsWithCombinedDictionaries = listOf(
            AvailableDictionary(
                dictName = "Bosnian/Croatian/Montenegrin/Serbian",
                OkuLanguage.SERBIAN,
                installed.contains(OkuLanguage.SERBIAN)
            ),
            AvailableDictionary(
                dictName = "Moldovan/Romanian", OkuLanguage.ROMANIAN, installed.contains(OkuLanguage.ROMANIAN)
            )
        )
        return OkuLanguage.entries
            .filterNot { it == OkuLanguage.ALL }
            .filterNot { it in combinedDictionaryLanguages }
            .map { lang -> AvailableDictionary(dictName = lang.label, lang, installed.contains(lang)) }
            .plus(langsWithCombinedDictionaries)
            .sortedBy { it.dictName }
    }


    data class DictionaryWord(
        val word: String
    )
}


data class AvailableDictionary(
    val dictName: String,
    val okuLanguage: OkuLanguage, // Only one language may represent multiple. Like serbian for bosnian, croation etc.
    val installed: Boolean
)