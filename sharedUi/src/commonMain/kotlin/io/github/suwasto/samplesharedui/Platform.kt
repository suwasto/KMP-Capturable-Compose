package io.github.suwasto.samplesharedui

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform