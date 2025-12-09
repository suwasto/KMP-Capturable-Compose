package io.github.suwasto.capturablecompose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A composable that captures its content as an [ImageBitmap].
 *
 * This function is **deprecated**. Please migrate to [io.github.suwasto.capturablecompose.capturablebox.CapturableBox] for an improved API that handles
 * capture logic more internally within the controller and supports optional sharing features.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param onCaptured A callback invoked with the captured [ImageBitmap] when a capture request completes.
 * @param captureController The controller used to trigger the capture process.
 * @param content The composable content to be captured.
 *
 * @see io.github.suwasto.capturablecompose.capturablebox.CapturableBox
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
                                onCaptured(bitmap)
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

class CaptureController {
    private val _isCapturing = mutableStateOf(false)
    internal val isCapturing: Boolean
        get() = _isCapturing.value

    fun capture() {
        _isCapturing.value = true
    }

    internal fun reset() {
        _isCapturing.value = false
    }
}
/**
 * Creates and remembers an instance of [CaptureController].
 *
 * This function returns a [CaptureController] which can be used to control the capturing process
 * of a [Capturable] composable. The controller is remembered across recompositions, ensuring
 * that the state of the capture request is maintained.
 *
 * @return A remembered instance of [CaptureController].
 * @see Capturable
 * @see CaptureController
 */
@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}
