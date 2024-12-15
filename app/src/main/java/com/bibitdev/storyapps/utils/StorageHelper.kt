package com.bibitdev.storyapps.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object StorageHelper {
    fun getFileFromUri(selectedImg: Uri, context: Context): File {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val myFile = createTempFile(context)
            val inputStream = contentResolver.openInputStream(selectedImg)
            val outputStream = FileOutputStream(myFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } > 0) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
            myFile
        } catch (e: Exception) {
            throw IOException("Error converting URI to File: ${e.message}")
        }
    }

    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "temp_image",
            ".jpg",
            storageDir
        )
    }

    fun generateTempFile(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val byteArrayStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayStream)
            val byteArray = byteArrayStream.toByteArray()
            streamLength = byteArray.size
            compressQuality = adjustCompressionQuality(streamLength, compressQuality)
        } while (streamLength > 1000000 && compressQuality > 0)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun adjustCompressionQuality(currentLength: Int, currentQuality: Int): Int {
        return when {
            currentLength > 1000000 -> currentQuality - 5
            currentLength > 500000 -> currentQuality - 2
            else -> currentQuality
        }
    }

}
