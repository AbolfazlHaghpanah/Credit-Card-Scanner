package com.haghpanah.creditcardscanner.data.textrecognizer

import androidx.camera.core.ImageProxy
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import javax.inject.Inject

class TextRecognizerImpl @Inject constructor() : TextRecognizer {

    override fun getCreditCardData(
        image: ImageProxy,
        exportShaba: Boolean,
        exportCvv2: Boolean,
        exportExpireDate: Boolean,
        exportBankName: Boolean,
    ): CreditCardData {
        TODO("Not yet implemented")
    }
}