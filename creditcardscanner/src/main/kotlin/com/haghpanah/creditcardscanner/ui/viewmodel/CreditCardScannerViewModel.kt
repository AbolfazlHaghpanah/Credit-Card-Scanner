package com.haghpanah.creditcardscanner.ui.viewmodel

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haghpanah.creditcardscanner.core.CreditCardScanner
import com.haghpanah.creditcardscanner.data.imagerecognizer.ImageRecognizer
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import com.haghpanah.creditcardscanner.data.textrecognizer.TextRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CreditCardScannerViewModel @Inject constructor(
    private val cameraPreview: Preview,
    private val imageAnalysis: ImageAnalysis,
    private val textRecognizer: TextRecognizer,
    private val imageRecognizer: ImageRecognizer,
    private val creditCardScanner: CreditCardScanner,
) : ViewModel() {
    private val _scanResult = MutableStateFlow<CreditCardData?>(null)
    val scanResult = _scanResult.asStateFlow()

    init {
        observeCreditCardData()
    }

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

            val isCreditCardFound = imageRecognizer.isImageContainsCreditCard(
                width = image.width,
                height = image.height,
                yBuffer = yPlane.buffer,
                yRowStride = yPlane.rowStride,
            )

            if (isCreditCardFound) {
                imageAnalysis.clearAnalyzer()

                val creditCardDataResult = textRecognizer.getCreditCardData(
                    imageProxy = image,
                    exportShaba = false,
                    exportCvv2 = false,
                    exportExpireDate = false,
                    exportBankName = false,
                )

                viewModelScope.launch {
                    creditCardDataResult?.let {
                        creditCardScanner.setCreditCardData(creditCardDataResult)
                    }
                }
            } else {
                image.close()
            }
        }
    }

    private fun observeCreditCardData() {
        viewModelScope.launch {
            creditCardScanner.observeCreditCardData().collect {
                _scanResult.emit(it)
            }
        }
    }
}