package com.haghpanah.creditcardscanner.ui.viewmodel

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.lifecycle.ViewModel
import com.haghpanah.creditcardscanner.data.imagerecognizer.ImageRecognizer
import com.haghpanah.creditcardscanner.data.textrecognizer.TextRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CreditCardScannerViewModel @Inject constructor(
    private val cameraPreview: Preview,
    private val imageAnalysis: ImageAnalysis,
    private val textRecognizer: TextRecognizer,
    private val imageRecognizer: ImageRecognizer,
) : ViewModel() {

    fun getCameraPreview() = cameraPreview

    fun setCameraPreviewSurfaceProvider(surfaceProvider: Preview.SurfaceProvider) {
        cameraPreview.setSurfaceProvider(surfaceProvider)
    }

    fun getImageAnalytics() = imageAnalysis

    @OptIn(ExperimentalGetImage::class)
    fun startAnalytics(onImageFound: (Bitmap?) -> Unit) {
        imageAnalysis.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { image ->
            val yPlane = image.planes[0]

            val creditCardImage = imageRecognizer.getPreprocessedImage(
                width = image.width,
                height = image.height,
                yBuffer = yPlane.buffer,
                yRowStride = yPlane.rowStride,
            )

            if (creditCardImage != null) {
                imageAnalysis.clearAnalyzer()
                onImageFound.invoke(creditCardImage)

                textRecognizer.getCreditCardData(
                    imageProxy = image,
                    exportShaba = false,
                    exportCvv2 = false,
                    exportExpireDate = false,
                    exportBankName = false,
                )
            } else {
                image.close()
            }
        }
    }
}