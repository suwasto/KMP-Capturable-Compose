package io.github.suwasto.capturablecompose

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

interface ShareSheet {
    /**
     * Initiates the platform-specific sharing mechanism to share the provided [image].
     *
     * This function triggers a share sheet (or equivalent system UI) allowing the user to send
     * or save the captured [ImageBitmap]. The actual implementation varies by platform
     * (e.g., an Intent chooser on Android, a UIActivityViewController on iOS).
     *
     * @param image The captured [ImageBitmap] content to be shared.
     */
    fun share(image: ImageBitmap)
}

/**
 * Creates and remembers a platform-specific [ShareSheet] implementation.
 *
 * This composable function provides access to the native sharing capabilities of the
 * underlying platform (e.g., Android Intent chooser, iOS UIActivityViewController, or
 * a desktop file saver/share dialog).
 *
 * @return A [ShareSheet] instance capable of sharing an [ImageBitmap].
 */
@Composable
expect fun rememberShareSheet(): ShareSheet
