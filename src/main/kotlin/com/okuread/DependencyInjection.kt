package com.okuread

import com.okuread.services.*
import com.okuread.util.OkuConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

val appModule
    get() = module {
        single { OkuConfig() }
        single { StatsService() }
        single { SettingsService() }
        single { ReadingService() }
        single { TextListService() }
        single { DictionaryService() }
        single { DictionaryDownloadService() }
    }

inline fun <reified T : Any> getKoinInstance(): T {
    return object : KoinComponent {
        val value: T by inject()
    }.value
}