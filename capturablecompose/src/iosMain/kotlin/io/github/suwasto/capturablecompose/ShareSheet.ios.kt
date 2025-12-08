package io.github.suwasto.capturablecompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

class IosShareSheet : ShareSheet {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun share(image: ImageBitmap) {
        val byteArray = image.toByteArray(CompressionFormat.PNG, 100)
        val data = byteArray.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = byteArray.size.toULong())
        }
        val uiImage = UIImage(data = data)
        
        val activityViewController = UIActivityViewController(
            activityItems = listOf(uiImage),
            applicationActivities = null
        )
        
        val window = UIApplication.sharedApplication.keyWindow
        val rootViewController = window?.rootViewController
        
        var topController = rootViewController
        while (topController?.presentedViewController != null) {
            topController = topController.presentedViewController
        }
        
        topController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}

@Composable
actual fun rememberShareSheet(): ShareSheet {
    return remember { IosShareSheet() }
}
