package com.haghpanah.scanner.domain

import android.graphics.Bitmap
import android.text.BoringLayout

interface NativeLibraryHelper {
    fun isImageContainsCreditCard(
        imageData: ByteArray,
        width: Int,
        height: Int,
    ): Boolean

    fun getPreprocessedImage(
        imageData: ByteArray,
        width: Int,
        height: Int,
    ): Bitmap
}