package io.github.suwasto.capturablecompose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

/**
 * A composable that captures its content as an [ImageBitmap].
 *
 * This function is **deprecated**. Please migrate to [CapturableBox] for an improved API that handles
 * capture logic more internally within the controller and supports optional sharing features.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param onCaptured A callback invoked with the captured [ImageBitmap] when a capture request completes.
 * @param captureController The controller used to trigger the capture process.
 * @param content The composable content to be captured.
 *
 * @see CapturableBox
 * @see CaptureController
 */
@Deprecated(
    message = "Use CapturableBox instead",
    replaceWith = ReplaceWith("CapturableBox(modifier, captureController, content = content)")
)
@Composable
fun Capturable(
    modifier: Modifier = Modifier,
    onCaptured: (ImageBitmap) -> Unit,
    captureController: CaptureController,
    content: @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    var pendingCapture by remember { mutableStateOf(false) }

    LaunchedEffect(pendingCapture) {
        if (pendingCapture) {
            val bitmap = withContext(Dispatchers.Default) {
                graphicsLayer.toImageBitmap()
            }
            onCaptured(bitmap)
            pendingCapture = false
            captureController.reset()
        }
    }

    Box(
        modifier = modifier
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (captureController.isCapturing && !pendingCapture) {
                        // Only record, no bitmap conversion here
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        pendingCapture = true
                    }
                }
            }
    ) {
        content()
    }
}

/**
 * A wrapper Composable that captures the content drawn inside it as an [ImageBitmap].
 *
 * This function works in conjunction with a [CaptureController] to trigger the capture process.
 * When a capture is requested via the controller, the content within this box is rendered into
 * a graphics layer, converted to a bitmap, and then returned via the controller or shared via
 * the optional [ShareSheet].
 *
 * Note: The capture happens asynchronously. The actual bitmap conversion occurs on the Default
 * dispatcher to avoid blocking the Main thread, while the result is delivered on the Main thread.
 *
 * @param modifier The modifier to be applied to the [Box] container.
 * @param captureController The controller used to trigger the capture and receive the resulting [ImageBitmap].
 * @param shareSheet An optional helper to immediately trigger a system share sheet with the captured bitmap.
 * @param content The Composable content to be displayed and captured.
 */
@Composable
fun CapturableBox(
    modifier: Modifier = Modifier,
    captureController: CaptureController,
    shareSheet: ShareSheet? = null,
    content: @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    var pendingCapture by remember { mutableStateOf(false) }

    LaunchedEffect(pendingCapture) {
        if (pendingCapture) {
            val bitmap = withContext(Dispatchers.Default) {
                graphicsLayer.toImageBitmap()
            }
            withContext(Dispatchers.Main) {
                captureController.onCaptured(bitmap)
                shareSheet?.share(bitmap)
                pendingCapture = false
                captureController.reset()
            }
        }
    }

    Box(
        modifier = modifier
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (captureController.isCapturing && !pendingCapture) {
                        // Only record, no bitmap conversion here
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        pendingCapture = true
                    }
                }
            }
    ) {
        content()
    }
}

class CaptureController {

    private var continuation: (ImageBitmap) -> Unit = {}

    var isCapturing: Boolean = false
        private set

    fun reset() {
        isCapturing = false
    }

    suspend fun capture(): ImageBitmap = suspendCancellableCoroutine { cont ->
        isCapturing = true

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

@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}
