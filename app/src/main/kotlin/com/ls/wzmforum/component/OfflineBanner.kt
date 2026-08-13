package com.ls.wzmforum.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ls.wzmforum.bus.NetworkStateBus

/**
 * 全局断网横幅：覆盖在所有页面顶层，断网时显示、恢复后自动隐藏。
 */
@Composable
fun OfflineBanner() {
    val networkOnline by NetworkStateBus.networkState.collectAsStateWithLifecycle()
    if (!networkOnline) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(Color(0xFFE53935))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "网络连接已断开",
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}
