package io.github.suwasto.capturablecompose

import androidx.compose.ui.graphics.ImageBitmap

expect fun ImageBitmap.toByteArray(
    compressionFormat: CompressionFormat,
    quality: Int
): ByteArray

enum class CompressionFormat {
    JPEG, PNG
}