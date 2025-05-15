package com.haghpanah.creditcardscanner.data.imagerecognizer

import android.graphics.Bitmap
import java.nio.ByteBuffer

interface ImageRecognizer {
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
}