package io.github.suwasto.capturablecompose.capturablebox

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import io.github.suwasto.capturablecompose.ShareSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    captureController: CapturableBoxController,
    shareSheet: ShareSheet? = null,
    content: @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (captureController.isCapturing) {
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        coroutineScope.launch(Dispatchers.Default) {
                            val bitmap = graphicsLayer.toImageBitmap()
                            withContext(Dispatchers.Main) {
                                captureController.onCaptured(bitmap)
                                shareSheet?.share(bitmap)
                                captureController.reset()
                            }
                        }
                    }
                }
            }
    ) {
        content()
    }
}