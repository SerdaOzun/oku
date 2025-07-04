package com.okuread.util

import ch.qos.logback.core.PropertyDefinerBase
import com.okuread.db.repositories.getUserDirectory

class LogDirectoryPropertyDefiner: PropertyDefinerBase() {
    override fun getPropertyValue(): String {
        return getUserDirectory().toString()
    }
}

