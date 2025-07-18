package io.github.suwasto.capturablecompose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.launch

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
                if (captureController.isCapturing) {
                    onDrawWithContent {
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                        coroutineScope.launch {
                            val bitmapResult = graphicsLayer.toImageBitmap()
                            onCaptured(bitmapResult)
                        }
                        captureController.reset()
                    }
                } else {
                    onDrawWithContent {
                        drawContent()
                    }
                }
            }
    ) {
        content()
    }
}

@Composable
fun CapturableWithScroll(
    modifier: Modifier = Modifier,
    onCaptured: (ImageBitmap) -> Unit,
    captureController: CustomCaptureController,
    content: @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .drawWithCache {
                if (captureController.isCapturing) {
                    onDrawWithContent {
                        graphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                        coroutineScope.launch {
                            val bitmapResult = graphicsLayer.toImageBitmap()
                            onCaptured(bitmapResult)
                        }
                        captureController.reset()
                    }
                } else {
                    onDrawWithContent {
                        drawContent()
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
@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}
