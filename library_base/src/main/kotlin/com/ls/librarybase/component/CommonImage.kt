package com.ls.librarybase.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * 图片加载
 */
@Composable
fun CommonImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: Painter? = null,
    error: Painter? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (url.isNullOrEmpty()) {
        val painter = placeholder ?: error
        if (painter != null) {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = modifier.fillMaxSize(),
                contentScale = contentScale
            )
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            placeholder = placeholder,
            error = error,
            contentScale = contentScale
        )
    }
}