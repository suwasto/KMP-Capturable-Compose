package io.github.suwasto.capturablecompose.capturablebox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CapturableBoxController {

    private var continuation: (ImageBitmap) -> Unit = {}

    private val _isCapturing = mutableStateOf(false)
    internal val isCapturing: Boolean
        get() = _isCapturing.value

    fun reset() {
        _isCapturing.value = false
    }

    suspend fun capture(): ImageBitmap = suspendCancellableCoroutine { cont ->
        _isCapturing.value = true
        continuation = { bitmap ->
            if (cont.isActive) {
                cont.resume(bitmap)
            }
        }
    }

    internal fun onCaptured(bitmap: ImageBitmap) {
        continuation(bitmap)
    }
}

/**
 * Creates and remembers a [CapturableBoxController] instance.
 *
 * This helper function ensures that the controller survives recompositions. It should be used
 * to obtain a controller instance that can be passed to a [CapturableBox] to trigger image captures.
 *
 * @return A [CapturableBoxController] instance that persists across recompositions.
 */
@Composable
fun rememberCaptureBoxController(): CapturableBoxController {
    return remember { CapturableBoxController() }
}