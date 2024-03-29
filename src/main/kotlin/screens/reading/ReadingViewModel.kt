package screens.reading

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import database.OkuText
import database.OkuTextEntity
import database.getAllTexts
import database.insertText
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.ViewModel

class ReadingViewModel : ViewModel() {
    var currentOkuText: OkuText? by mutableStateOf(null)

    fun getAllTexts(): List<OkuText> = OkuTextEntity.getAllTexts()

    fun updateCurrentText(okuText: OkuText) {
        currentOkuText = okuText
    }

    fun insertText(okuText: OkuText) {
        OkuTextEntity.insertText(okuText)
    }

}