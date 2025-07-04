package com.okuread.util

import com.okuread.licenseKeys.client
import com.okuread.loggerRoot
import kotlinx.coroutines.coroutineScope
import okhttp3.Request

suspend fun checkInternetConnection(): Boolean = coroutineScope {
    try {
        val request = Request.Builder().url("https://google.com").head().build()
        val response = client.newCall(request).execute()
        response.code == 200
    } catch (e: Exception) {
        loggerRoot.warn("No internet connection. ${ e.message}")
        false
    }
}
