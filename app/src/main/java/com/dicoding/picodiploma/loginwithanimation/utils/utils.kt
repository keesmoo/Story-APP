package com.dicoding.picodiploma.loginwithanimation.utils

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private const val MAXIMAL_SIZE = 1000000

fun File.reduceFileImage(): File {
    val bitmap = BitmapFactory.decodeFile(this.path)
    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
    return this
}
