package com.ls.librarybase.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class ViewState {
    object Loading : ViewState()
    object Empty : ViewState()
    data class Error(val message: String? = "加载失败，请重试") : ViewState()
}

/**
 * 三态视图(加载中、空数据、错误)
 */
@Composable
fun StatusView(
    state: ViewState,
    modifier: Modifier = Modifier,
    emptyText: String = "暂无数据",
    onRetry: (() -> Unit)? = null
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            ViewState.Loading -> CircularProgressIndicator()

            ViewState.Empty -> Text(
                text = emptyText,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            is ViewState.Error -> Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = state.message ?: "加载失败",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                if (onRetry != null) {
                    Button(onClick = onRetry) { Text("重试") }
                }
            }
        }
    }
}