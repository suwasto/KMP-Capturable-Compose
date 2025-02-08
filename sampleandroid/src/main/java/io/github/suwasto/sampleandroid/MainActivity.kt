package io.github.suwasto.sampleandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.suwasto.samplesharedui.RestaurantReceiptScreen
import io.github.suwasto.samplesharedui.imagesaver.ImageSaverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImageSaverFactory.init(this.applicationContext)
        enableEdgeToEdge()
        setContent {
            RestaurantReceiptScreen()
        }
    }
}
