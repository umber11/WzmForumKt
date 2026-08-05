package com.ls.librarybase.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 *Material 3 主题封装
 */
//浅色主题配色常量
private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary.copy(alpha = 0.12f),
    onPrimaryContainer = PrimaryDark,
    background = Background,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    outline = Divider,
    error = Danger,
    onError = Color.White
)
//深色主题配色常量
private val DarkColors = darkColorScheme(
    primary = Color(0xFF64B5F6),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    error = Danger
)
//主题组合函数
@Composable
fun WzmForumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = WzmForumTypography,
        content = content
    )
}