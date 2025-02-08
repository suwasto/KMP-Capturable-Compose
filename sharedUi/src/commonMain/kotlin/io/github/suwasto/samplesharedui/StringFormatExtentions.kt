package io.github.suwasto.samplesharedui

import kotlin.math.round

fun formatPrice(value: Double): String {
    return (round(value * 100) / 100).toString()
}