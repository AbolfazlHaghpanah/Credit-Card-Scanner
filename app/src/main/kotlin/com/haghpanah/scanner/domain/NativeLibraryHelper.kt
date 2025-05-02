package com.haghpanah.scanner.domain

import android.graphics.Bitmap
import java.nio.ByteBuffer

interface NativeLibraryHelper {
    fun isImageContainsCreditCard(
        width: Int,
        height: Int,
        yBuffer: ByteBuffer,
        yRowStride: Int,
    ): Boolean

    fun getPreprocessedImage(
        width: Int,
        height: Int,
        yBuffer: ByteBuffer,
        yRowStride: Int,
    ): Bitmap?

    fun startNumberDetection(
        imageData: ByteArray,
        width: Int,
        height: Int,
    ): String
}