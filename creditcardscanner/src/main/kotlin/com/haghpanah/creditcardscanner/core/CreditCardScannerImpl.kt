package com.haghpanah.creditcardscanner.core

import android.content.Context
import android.content.Intent
import com.haghpanah.creditcardscanner.Constant.COLORS_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.HINT_TEXT_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.HINT_VISIBLE_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.TOP_BAR_TEXT_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.TOP_BAR_VISIBLE_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.TYPOGRAPHY_BUNDLE_KEY
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import com.haghpanah.creditcardscanner.ui.CreditCardScannerActivity
import com.haghpanah.creditcardscanner.ui.theme.CreditCardScannerColors
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
        colors: CreditCardScannerColors?,
        typography: Int?,
        hintText: String?,
        topBarText: String?,
        shouldTopBarVisible: Boolean?,
        shouldShowHintText: Boolean?,
    ) {
        Intent(context, CreditCardScannerActivity::class.java).apply {
            colors?.let { putExtra(COLORS_BUNDLE_KEY, it) }
            typography?.let { putExtra(TYPOGRAPHY_BUNDLE_KEY, it) }
            hintText?.let { putExtra(HINT_TEXT_BUNDLE_KEY, it) }
            topBarText?.let { putExtra(TOP_BAR_TEXT_BUNDLE_KEY, it) }
            shouldTopBarVisible?.let { putExtra(TOP_BAR_VISIBLE_BUNDLE_KEY, it) }
            shouldShowHintText?.let { putExtra(HINT_VISIBLE_BUNDLE_KEY, it) }

            context.startActivity(this)
        }
    }
}
