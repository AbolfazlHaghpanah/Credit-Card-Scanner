package com.haghpanah.creditcardscanner.core

import android.content.Context
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import com.haghpanah.creditcardscanner.ui.colors.CreditCardScannerDefault
import com.haghpanah.creditcardscanner.ui.colors.CreditCardScannerDefault.Colors
import com.haghpanah.creditcardscanner.ui.colors.CreditCardScannerDefault.Language
import com.haghpanah.creditcardscanner.ui.colors.CreditCardScannerDefault.Typography
import kotlinx.coroutines.flow.Flow

abstract class CreditCardScanner {
    internal abstract suspend fun setCreditCardData(data: CreditCardData)

    abstract fun observeCreditCardData(): Flow<CreditCardData>

    abstract fun startActivity(
        context: Context,
        colors: Colors? = null,
        typography: Typography? = null,
        hintText: String? = null,
        topBarText: String? = null,
        shouldTopBarVisible: Boolean? = null,
        shouldShowHintText: Boolean? = null,
        language: Language? = null,
    )
}
