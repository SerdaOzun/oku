package com.okuread.services

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.okuread.db.repositories.*
import com.okuread.db.util.OkuLanguage
import moe.tlaster.precompose.viewmodel.ViewModel
import org.koin.core.component.KoinComponent

class SettingsService: ViewModel(), KoinComponent {
    var licenseActivated by mutableStateOf(getLicenseStatus())
    var disableFrequencyTextCreation by mutableStateOf(!licenseActivated && OkuTextEntity.getTrialFrequencyTextLimit().size >= 2)

    //The main language that the user set in settings
    var defaultLanguage by mutableStateOf(
        OkuLanguage.valueByLabel(
            SettingsEntity.getSetting<OkuSetting.DefaultLanguage>() ?: OkuLanguage.ALL.label
        )
    )

    //Text fontsize for reading
    var defaultFontSize by mutableStateOf(
        SettingsEntity.getSetting<OkuSetting.DefaultFontSize>()?.toFloat()?.sp ?: 18.sp
    )

    private fun getLicenseStatus(): Boolean = SettingsEntity.getSetting<OkuSetting.LicenseStatus>()?.toBoolean() == true
}