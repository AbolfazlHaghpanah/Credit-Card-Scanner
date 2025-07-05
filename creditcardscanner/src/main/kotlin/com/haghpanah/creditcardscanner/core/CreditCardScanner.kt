package com.haghpanah.creditcardscanner.core

import android.content.Context
import androidx.annotation.FontRes
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import com.haghpanah.creditcardscanner.ui.theme.CreditCardScannerColors
import com.haghpanah.creditcardscanner.ui.theme.CreditCardScannerLanguage
import kotlinx.coroutines.flow.Flow


abstract class CreditCardScanner{
    internal abstract suspend fun setCreditCardData(data: CreditCardData)

    abstract fun observeCreditCardData(): Flow<CreditCardData>

    abstract fun startActivity(
        context: Context,
        colors: CreditCardScannerColors? = null,
        @FontRes font: Int? = null,
        hintText: String? = null,
        topBarText: String? = null,
        shouldTopBarVisible: Boolean? = null,
        shouldShowHintText: Boolean? = null,
        language: CreditCardScannerLanguage? = null,
    )
}
