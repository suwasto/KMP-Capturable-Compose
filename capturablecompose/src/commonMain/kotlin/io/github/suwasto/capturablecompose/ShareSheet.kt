package io.github.suwasto.capturablecompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

interface ShareSheet {
    fun share(image: ImageBitmap)
}

@Composable
expect fun rememberShareSheet(): ShareSheet
