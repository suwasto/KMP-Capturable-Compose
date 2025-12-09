package io.github.suwasto.capturablecompose

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AndroidShareSheet(private val context: Context) : ShareSheet {
    override fun share(image: ImageBitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(context.cacheDir, "shared_image.jpg")
            FileOutputStream(file).use { out ->
                image.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            withContext(Dispatchers.Main) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
            }
        }
    }
}

@Composable
actual fun rememberShareSheet(): ShareSheet {
    val context = LocalContext.current
    return remember(context) { AndroidShareSheet(context) }
}
