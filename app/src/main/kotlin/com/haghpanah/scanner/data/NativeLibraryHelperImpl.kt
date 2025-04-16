package com.haghpanah.scanner.data

import android.graphics.Bitmap
import com.haghpanah.scanner.domain.NativeLibraryHelper

class NativeLibraryHelperImpl : NativeLibraryHelper {

    external override fun isImageContainsCreditCard(
        imageData: ByteArray,
        width: Int,
        height: Int,
    ): Boolean

    external override fun getPreprocessedImage(
        imageData: ByteArray,
        width: Int,
        height: Int,
    ): Bitmap

}