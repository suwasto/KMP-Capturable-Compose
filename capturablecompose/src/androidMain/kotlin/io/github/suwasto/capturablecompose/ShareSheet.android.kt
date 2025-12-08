package io.github.suwasto.capturablecompose

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class AndroidShareSheet(private val context: Context) : ShareSheet {
    override fun share(image: ImageBitmap) {
        val byteArray = image.toByteArray(CompressionFormat.PNG, 100)
        val file = File(context.cacheDir, "shared_image.png")
        FileOutputStream(file).use {
            it.write(byteArray)
        }
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}

@Composable
actual fun rememberShareSheet(): ShareSheet {
    val context = LocalContext.current
    return remember(context) { AndroidShareSheet(context) }
}
