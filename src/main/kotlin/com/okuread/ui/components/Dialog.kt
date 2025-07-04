package com.okuread.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gabrieldrn.carbon.button.Button
import com.gabrieldrn.carbon.button.ButtonType
import com.okuread.ui.theme.ContentBoxBorderColor
import com.okuread.ui.theme.spacing


enum class DialogType {
    CREATE, DELETE, RENAME
}

@Composable
fun DialogWithConfirmAndCancel(
    message: String,
    content: @Composable () -> Unit = {},
    confirmButtonLabel: String = "Confirm",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            Modifier.clip(RectangleShape)
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max)
                .background(MaterialTheme.colors.background)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.small)) {
                Text(
                    message,
                    modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.medium)
                        .bottomBorder(color = MaterialTheme.colors.ContentBoxBorderColor, strokeWidth = 1.dp)
                )

                content()

                Row(
                    Modifier.padding(top = MaterialTheme.spacing.medium).fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small).fillMaxHeight(),
                        label = confirmButtonLabel,
                        onClick = onConfirm,
                        buttonType = ButtonType.Primary
                    )
                    Button(
                        modifier = Modifier.padding(end = MaterialTheme.spacing.small).fillMaxHeight(),
                        label = "Cancel",
                        onClick = onDismiss,
                        buttonType = ButtonType.Secondary
                    )
                }
            }
        }
    }
}

@Composable
fun DialogWithCancel(
    modifier: Modifier = Modifier,
    message: String,
    content: @Composable () -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier.clip(RectangleShape)
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Max)
                .background(MaterialTheme.colors.background)
        ) {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.small)) {
                Text(message,
                    modifier = Modifier.fillMaxWidth().padding(bottom = MaterialTheme.spacing.medium)
                        .bottomBorder(color = MaterialTheme.colors.ContentBoxBorderColor, strokeWidth = 1.dp)
                )

                content()

                Row(
                    Modifier.padding(top = MaterialTheme.spacing.medium).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        label = "Close",
                        onClick = onDismiss,
                        buttonType = ButtonType.Secondary
                    )
                }
            }
        }
    }
}
