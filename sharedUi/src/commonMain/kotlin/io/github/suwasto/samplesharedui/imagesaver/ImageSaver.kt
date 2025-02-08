package io.github.suwasto.samplesharedui.imagesaver

interface ImageSaver {
    fun saveImage(byteArray: ByteArray, filename: String, extention: String): String?
}