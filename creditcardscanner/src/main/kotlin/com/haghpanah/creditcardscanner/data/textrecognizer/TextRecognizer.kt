package com.haghpanah.creditcardscanner.data.textrecognizer

import androidx.camera.core.ImageProxy
import com.haghpanah.creditcardscanner.data.model.CreditCardData

interface TextRecognizer {

    fun getCreditCardData(
        image: ImageProxy,
        exportShaba: Boolean,
        exportCvv2: Boolean,
        exportExpireDate: Boolean,
        exportBankName: Boolean,
    ): CreditCardData
}