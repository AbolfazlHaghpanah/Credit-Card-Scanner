package com.haghpanah.scanner.data

import android.graphics.Bitmap
import com.haghpanah.scanner.domain.NativeLibraryHelper
import javax.inject.Inject

class NativeLibraryHelperImpl @Inject constructor() : NativeLibraryHelper {

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