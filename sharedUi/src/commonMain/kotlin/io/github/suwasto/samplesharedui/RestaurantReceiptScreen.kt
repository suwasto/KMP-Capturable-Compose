package io.github.suwasto.samplesharedui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.suwasto.capturablecompose.Capturable
import io.github.suwasto.capturablecompose.CompressionFormat
import io.github.suwasto.capturablecompose.rememberCaptureController
import io.github.suwasto.capturablecompose.toByteArray
import io.github.suwasto.samplesharedui.imagesaver.ImageSaverFactory
import io.github.suwasto.samplesharedui.theme.SharedTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

data class ReceiptData(
    val restaurantName: String,
    val address: String,
    val phone: String,
    val orderNumber: Int,
    val items: List<OrderItem>,
    val taxRate: Double,
    val discount: Double = 0.0
)

@Composable
fun RestaurantReceiptScreen(
    imageSaverFactory: ImageSaverFactory = ImageSaverFactory()
) {
    val captureController = rememberCaptureController()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    SharedTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize().background(
                    Color.LightGray
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Capturable(
                    captureController = captureController,
                    onCaptured = { imageBitmap ->
                        coroutineScope.launch {
                            val byteArray = imageBitmap.toByteArray(
                                CompressionFormat.PNG, 100
                            )
                            val path = imageSaverFactory.getImageSaver().saveImage(
                                byteArray = byteArray,
                                filename = "screenshoot${Clock.System.now().toEpochMilliseconds()}",
                                extention = "png"
                            )
                            snackbarHostState.showSnackbar("File Saved in $path")
                        }
                    }
                ) {
                    RestaurantReceipt(
                        modifier = Modifier.padding(innerPadding),
                        receiptData = ReceiptData(
                            restaurantName = "Gourmet Bistro",
                            address = "123 Main Street, Food City",
                            phone = "(555) 123-4567",
                            orderNumber = 142,
                            items = listOf(
                                OrderItem("Steak Frites", 2, 24.99),
                                OrderItem("Caesar Salad", 1, 12.50),
                                OrderItem("Red Wine", 3, 8.00),
                                OrderItem("Chocolate Cake", 1, 9.99)
                            ),
                            taxRate = 0.08,
                            discount = 10.0
                        )
                    )
                }

                Button(
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        captureController.capture()
                    }
                ) {
                    Text("Capture")
                }

            }
        }
    }
}

@Composable
fun RestaurantReceipt(
    modifier: Modifier,
    receiptData: ReceiptData
) {
    val subtotal = receiptData.items.sumOf { it.price * it.quantity }
    val tax = subtotal * receiptData.taxRate
    val total = subtotal + tax - receiptData.discount

    Column(
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = receiptData.restaurantName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            )
            Text(
                text = receiptData.address,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Tel: ${receiptData.phone}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))

        // Order Info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Order #${receiptData.orderNumber}")
            Text("${Clock.System.now()}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Items List
        receiptData.items.forEach { item ->
            ReceiptItemRow(item)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        DashedDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Totals
        ReceiptTotalRow("Subtotal:", subtotal)
        ReceiptTotalRow("Tax (${receiptData.taxRate.times(100).toInt()}%):", tax)
        if (receiptData.discount > 0) {
            ReceiptTotalRow("Discount:", -receiptData.discount)
        }
        Spacer(modifier = Modifier.height(8.dp))
        ReceiptTotalRow("TOTAL", total, isTotal = true)

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Thank you for dining with us!",
            style = MaterialTheme.typography.bodySmall.copy(
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReceiptItemRow(item: OrderItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.quantity}x ${item.name}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$${formatPrice(item.price * item.quantity)}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ReceiptTotalRow(label: String, amount: Double, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
            )
        )
        Text(
            text = "$${formatPrice(amount)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun DashedDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(1.dp)
                )
                .padding(horizontal = 2.dp)
        )
    }
}
