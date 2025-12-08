package io.github.suwasto.samplesharedui.imagesaver

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.*
import platform.Foundation.*
import platform.Photos.*

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), this.size.toULong())
    }
}

actual class ImageSaverFactory : ImageSaver {

    override fun saveImage(byteArray: ByteArray, filename: String, extention: String): String? {
        val data = byteArray.toNSData()

        PHPhotoLibrary.requestAuthorization { status ->
            if (status == PHAuthorizationStatusAuthorized) {
                saveImageToLibrary(data, filename, extention)
            } else {
                println("Permission denied to access photo library")
            }
        }

        return "photos"
    }

    fun saveImageToLibrary(data: NSData, filename: String, extension: String) {
        // Create a temporary file to store the image data
        val tempDirectory = NSTemporaryDirectory()
        val tempFilePath = "$tempDirectory$filename.$extension"

        // Write NSData to the temporary file
        val fileManager = NSFileManager.defaultManager
        if (fileManager.createFileAtPath(tempFilePath, data, null)) {
            val fileURL = NSURL.fileURLWithPath(tempFilePath)

            // Add the image to the Photos library
            PHPhotoLibrary.sharedPhotoLibrary().performChanges({
                val creationRequest = PHAssetCreationRequest.creationRequestForAsset()
                creationRequest.addResourceWithType(
                    PHAssetResourceTypePhoto,
                    fileURL,
                    PHAssetResourceCreationOptions()
                )
            }, { success, error ->
                if (success) {
                    println("Successfully saved image to Photos.")
                } else {
                    println("Failed to save image: ${error?.localizedDescription}")
                }
            })
        } else {
            println("Failed to write image data to temporary file.")
        }
    }

    actual fun getImageSaver(): ImageSaver {
        return this
    }
}