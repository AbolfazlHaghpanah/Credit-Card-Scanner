package com.haghpanah.creditcardscanner.core

import android.content.Context
import android.content.Intent
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import com.haghpanah.creditcardscanner.ui.CreditCardScannerActivity
import com.haghpanah.creditcardscanner.ui.colors.CreditCardScannerDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CreditCardScannerImpl @Inject constructor() : CreditCardScanner() {
    private val _creditCardData = MutableStateFlow<CreditCardData?>(null)

    override suspend fun setCreditCardData(data: CreditCardData) {
        _creditCardData.emit(data)
    }

    override fun observeCreditCardData(): Flow<CreditCardData> {
        return _creditCardData.filterNotNull()
    }

    override fun startActivity(
        context: Context,
        colors: CreditCardScannerDefault.Colors?,
        typography: CreditCardScannerDefault.Typography?,
        hintText: String?,
        topBarText: String?,
        shouldTopBarVisible: Boolean?,
        shouldShowHintText: Boolean?,
        language: CreditCardScannerDefault.Language?
    ) {
        CreditCardScannerDefault.initiate(
            colors = colors,
            typography = typography,
            hintText = hintText,
            topBarText = topBarText,
            shouldTopBarVisible = shouldTopBarVisible,
            shouldShowHintText = shouldShowHintText,
            language = language
        )
        Intent(context, CreditCardScannerActivity::class.java).apply {
            context.startActivity(this)
        }
    }
}
