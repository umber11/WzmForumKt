package com.ls.librarybase.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

/**
 * 确认弹窗
 */
@Composable
fun YesOrNoDialog(
    title: String,
    content: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = "取消")
            }
        },
        title = {
            Text(text = title, fontSize = 18.sp)
        },
        text = {
            Text(
                text = content,
                fontSize = 14.sp,
                maxLines = 8,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}
