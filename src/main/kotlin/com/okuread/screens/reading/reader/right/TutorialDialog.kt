package com.okuread.screens.reading.reader.right

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TutorialDialogContent(modifier: Modifier) {
    val tutorial = "Change Status: The second click on a word changes its status\n\n" +
            "Select sentence: Left-click the first and right-click the last word to select a sentence\n\n"
    Text(tutorial, modifier = modifier)
}
