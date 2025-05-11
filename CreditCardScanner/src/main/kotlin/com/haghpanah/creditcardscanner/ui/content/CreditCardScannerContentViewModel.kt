package com.haghpanah.creditcardscanner.ui.content

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.haghpanah.creditcardscanner.domain.NativeLibraryHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CreditCardScannerContentViewModel @Inject constructor(
    private val cameraPreview: Preview,
    private val imageAnalysis: ImageAnalysis,
    private val nativeLibraryHelper: NativeLibraryHelper,
) : ViewModel() {

    fun getCameraPreview() = cameraPreview

    fun setCameraPreviewSurfaceProvider(surfaceProvider: SurfaceProvider) {
        cameraPreview.setSurfaceProvider(surfaceProvider)
    }

    fun getImageAnalytics() = imageAnalysis

    @OptIn(ExperimentalGetImage::class)
    fun startAnalytics(onImageFound: (Bitmap?) -> Unit) {
        imageAnalysis.setAnalyzer(
            Executors.newSingleThreadExecutor()
        ) { image ->
            val yPlane = image.planes[0]

            val creditCardImage = nativeLibraryHelper.getPreprocessedImage(
                width = image.width,
                height = image.height,
                yBuffer = yPlane.buffer,
                yRowStride = yPlane.rowStride,
            )

            if (creditCardImage != null) {
                imageAnalysis.clearAnalyzer()
                onImageFound.invoke(creditCardImage)
                image.image?.let {
                    val recognizer =
                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val imageInput =
                        InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)

                    recognizer.process(imageInput).addOnSuccessListener { text ->
                        text.textBlocks.forEach {
                            Log.d("mmd", "analysed ::: ${it.text}")
                        }
                    }.addOnFailureListener {
                        throw it
                    }.addOnCompleteListener {
                        image.close()
                    }
                } ?: run {
                    Log.d("mmd", "startAnalytics: image was null")
                }
            } else {
                image.close()
            }
        }
    }

}