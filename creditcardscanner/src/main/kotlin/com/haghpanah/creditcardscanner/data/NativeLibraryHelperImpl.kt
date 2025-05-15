package com.haghpanah.creditcardscanner.data

import android.graphics.Bitmap
import com.haghpanah.creditcardscanner.domain.NativeLibraryHelper
import java.nio.ByteBuffer
import javax.inject.Inject

class NativeLibraryHelperImpl @Inject constructor() : NativeLibraryHelper {

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