package io.github.suwasto.capturablecompose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toByteArray(
    compressionFormat: CompressionFormat,
    quality: Int
): ByteArray {
    val skiaBitmap = this.asSkiaBitmap()
    val image: Image = Image.makeFromBitmap(skiaBitmap)
    val format = when (compressionFormat) {
        CompressionFormat.JPEG -> EncodedImageFormat.JPEG
        CompressionFormat.PNG -> EncodedImageFormat.PNG
    }
    val imageData = image.encodeToData(format = format, quality = quality)
    return imageData?.bytes ?: ByteArray(0)
}