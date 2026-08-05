package com.ls.librarybase.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 页面脚手架
 */
@Composable
fun PageWrapper(
    title: String? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    loading: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 顶部标题栏（状态栏下方）
            if (title != null || showBack) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(start = 14.dp)
                                .size(20.dp)
                                .clickable(enabled = onBack != null) { onBack?.invoke() }
                        )
                    }
                    if (title != null) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = if (showBack) 48.dp else 16.dp)
                        )
                    }
                }
            }

            // 页面内容
            Box(modifier = Modifier.fillMaxSize()) {
                content()
                // 加载遮罩
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}