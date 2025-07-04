package com.okuread.util

import com.typesafe.config.ConfigFactory
import io.github.config4k.getValue
import org.koin.core.component.KoinComponent

class OkuConfig : KoinComponent {
    private val config = ConfigFactory.load()
    val dictionariesUrl: String by config
}