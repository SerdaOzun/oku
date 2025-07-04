package com.okuread.licenseKeys

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.gson.Gson
import com.okuread.db.data.ActivateResponse
import com.okuread.db.repositories.OkuSetting
import com.okuread.db.repositories.SettingsEntity
import com.okuread.db.repositories.upsert
import com.okuread.loggerRoot
import com.okuread.ui.theme.spacing
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.EMPTY_REQUEST

private const val lemonSqueezyBaseUrl = "https://api.lemonsqueezy.com"
private const val activateRoute = "/v1/licenses/activate"

val client by lazy { OkHttpClient() }

@Composable
fun licensingScreen(activate: (Boolean) -> Unit) {
    var licenseKey by remember { mutableStateOf("") }
    var licenseResult: Boolean? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        TextField(
            licenseKey,
            onValueChange = { licenseKey = it },
            placeholder = { Text("License key") },
            singleLine = true
        )
        Button(
            onClick = {
                licenseResult = activateKey(licenseKey.trim())
                activate(licenseResult == true)
            },
            enabled = licenseKey.isNotBlank()
        ) {
            Text("Activate key")
        }
        if (licenseResult != null && licenseResult == false) {
            Text(
                "Could not verify your license. Please try again.",
                modifier = Modifier.padding(top = MaterialTheme.spacing.medium),
                color = MaterialTheme.colors.error
            )
        }
    }
}

private fun activateKey(licenseKey: String): Boolean = runBlocking {
    try {
        loggerRoot.info("Activating license")
        val request =
            Request.Builder().url("$lemonSqueezyBaseUrl${activateRoute}?license_key=$licenseKey&instance_name=oku")
                .post(EMPTY_REQUEST).build()
        val response = client.newCall(request).execute()

        if (response.code != 200) {
            loggerRoot.error("Failed to activate key. Code: ${response.code}. Response: ${response.body}")
        }

        val result = Gson().fromJson(response.body?.string(), ActivateResponse::class.java)

        if (response.code == 400) {
            loggerRoot.error("Failed to activate key. Error response: ${result.error}")
        }

        if (result.activated) {
            SettingsEntity.upsert(OkuSetting.LicenseStatus(activated = true))
            SettingsEntity.upsert(OkuSetting.LicenseKey(licenseKey = licenseKey))
            SettingsEntity.upsert(OkuSetting.LicensedEmail(email = result.meta?.customer_email ?: ""))
            loggerRoot.info("Successfully activated license")
        }

        result.activated
    } catch (e: Exception) {
        loggerRoot.error("Failed to activate key.", e)
        false
    }
}