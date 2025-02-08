package io.github.suwasto.capturablecompose

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toByteArray(
    compressionFormat: CompressionFormat,
    quality: Int
): ByteArray {
    val androidBitmap = this.asAndroidBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    val format = when(compressionFormat) {
        CompressionFormat.JPEG -> Bitmap.CompressFormat.JPEG
        CompressionFormat.PNG -> Bitmap.CompressFormat.PNG
    }
    androidBitmap.compress(format, quality, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}