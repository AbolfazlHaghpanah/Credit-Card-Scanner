package com.haghpanah.creditcardscanner.data.textrecognizer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import javax.inject.Inject

class TextRecognizerImpl @Inject constructor() : TextRecognizer {

    @OptIn(ExperimentalGetImage::class)
    override fun getCreditCardData(
        imageProxy: ImageProxy,
        exportShaba: Boolean,
        exportCvv2: Boolean,
        exportExpireDate: Boolean,
        exportBankName: Boolean,
    ): CreditCardData? {
        val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val imageInput =
            InputImage.fromMediaImage(
                requireNotNull(imageProxy.image),
                imageProxy.imageInfo.rotationDegrees
            )

        var creditCardData: CreditCardData? = null
        var error: Throwable? = null

        recognizer.process(imageInput)
            .addOnSuccessListener { text ->
                runCatching {
                    creditCardData = text.textBlocks.map { it.text }.exportCreditCardData(
                        exportShaba = exportShaba,
                        exportCvv2 = exportCvv2,
                        exportExpireDate = exportExpireDate,
                        exportBankName = exportBankName
                    )
                }.onFailure {
                    error = it
                }
            }.addOnFailureListener {
                error = it
            }.addOnCompleteListener {
                imageProxy.close()
            }

        while (true) {
            if (creditCardData != null || error != null) {
                break
            }
        }

        error?.let { throw it }
        return creditCardData
    }

    private fun List<String>.exportCreditCardData(
        exportShaba: Boolean,
        exportCvv2: Boolean,
        exportExpireDate: Boolean,
        exportBankName: Boolean,
    ): CreditCardData {
        return CreditCardData(
            number = exportNumbers(),
            cvv2 = if (exportCvv2) exportCvv2() else null,
            expireMonth = null,
            expireYear = null,
            shaba = null,
            bank = null
        )
    }

    private fun List<String>.exportNumbers(): String {
        return requireNotNull(
            find { text ->
                text.replace("\\s".toRegex(), "").matches(Regex("\\d{16}"))
            }?.replace("\\s".toRegex(), "")
        ) {
            "Could Not Find Credit CardNumbers From TextBlock : ${
                this.joinToString { it.trim() }
            }"
        }
    }

    private fun List<String>.exportCvv2(): String? {
        return find { text ->
            text.startsWith("CVV2") || text.startsWith("cvv2")
        }
    }

    private fun List<String>.exportShaba(): String? {
        return find { text ->
            text.startsWith("IR") && text.length == 26
        }
    }

    private fun List<String>.exportDate(): String? {
        return null
    }

    private fun List<String>.exportBankName(): String? {
        return null
    }
}