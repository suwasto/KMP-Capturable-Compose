package io.github.suwasto.samplesharedui.imagesaver

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore


actual class ImageSaverFactory : ImageSaver {

    override fun saveImage(byteArray: ByteArray, filename: String, extention: String): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.$extention")
            put(
                MediaStore.MediaColumns.MIME_TYPE,
                if (extention == "png") "image/png" else "image/jpeg"
            )
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context?.contentResolver
        val uri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(byteArray)
            }
            it.toString() // Return the URI instead of file path
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        fun init(context: Context) {
            this.context = context
        }
    }

    actual fun getImageSaver(): ImageSaver {
        return this
    }
}