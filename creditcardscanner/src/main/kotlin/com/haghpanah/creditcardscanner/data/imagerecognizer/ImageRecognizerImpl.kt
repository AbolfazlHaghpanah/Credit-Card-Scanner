package com.haghpanah.creditcardscanner.data.imagerecognizer

import android.graphics.Bitmap
import java.nio.ByteBuffer
import javax.inject.Inject

class ImageRecognizerImpl @Inject constructor() : ImageRecognizer {

    external override fun isImageContainsCreditCard(
        width: Int,
        height: Int,
        yBuffer: ByteBuffer,
        yRowStride: Int,
    ): Boolean

    external override fun getPreprocessedImage(
        width: Int,
        height: Int,
        yBuffer: ByteBuffer,
        yRowStride: Int,
    ): Bitmap?
}
