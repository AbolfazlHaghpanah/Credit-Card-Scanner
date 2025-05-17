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

        recognizer.process(imageInput)
            .addOnSuccessListener { text ->
                creditCardData = text.textBlocks.map { it.text }.exportCreditCardData(
                    exportShaba = exportShaba,
                    exportCvv2 = exportCvv2,
                    exportExpireDate = exportExpireDate,
                    exportBankName = exportBankName
                )
            }.addOnFailureListener {
                throw it
            }.addOnCompleteListener {
                imageProxy.close()
            }

        return creditCardData
    }

    private fun List<String>.then(
        condition: Boolean,
        execute: List<String>.() -> Unit,
    ): List<String> {
        if (condition) {
            execute.invoke(this)
        }
        return this
    }

    private fun List<String>.exportCreditCardData(
        exportShaba: Boolean,
        exportCvv2: Boolean,
        exportExpireDate: Boolean,
        exportBankName: Boolean,
    ): CreditCardData? {
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
                text.matches(Regex("\\d{4}( \\d{4}){3}"))
            }
        ) {
            "Could Not Find Credit CardNumbers From TextBlocks"
        }
    }

    private fun List<String>.exportCvv2(): String? {
        //TODO
        return null
    }

    private fun List<String>.exportShaba(): String? {
        //TODO
        return null
    }

    private fun List<String>.exportDate(): String? {
        return null
    }

    private fun List<String>.exportBankName(): String? {
        return null
    }
}