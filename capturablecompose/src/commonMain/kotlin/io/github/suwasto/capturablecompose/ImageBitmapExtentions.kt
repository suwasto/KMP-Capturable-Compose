package io.github.suwasto.capturablecompose

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Converts this [ImageBitmap] into a [ByteArray] using the specified [compressionFormat] and [quality].
 *
 * This function allows for platform-specific implementations to handle image compression and byte array conversion.
 *
 * @param compressionFormat The format to use for compression (e.g., [CompressionFormat.JPEG] or [CompressionFormat.PNG]).
 * @param quality The quality of the compressed image. This value usually ranges from 0 to 100, where 0 is the lowest quality (maximum compression) and 100 is the highest quality (minimum compression). Note that for formats like PNG, which is lossless, this value might be ignored.
 * @return A [ByteArray] containing the compressed image data.
 */
expect fun ImageBitmap.toByteArray(
    compressionFormat: CompressionFormat,
    quality: Int
): ByteArray

enum class CompressionFormat {
    JPEG, PNG
}