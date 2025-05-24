package com.haghpanah.creditcardscanner.core

import android.content.Context
import android.content.Intent
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import com.haghpanah.creditcardscanner.ui.CreditCardScannerActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

internal class CreditCardScannerImpl @Inject constructor() : CreditCardScanner() {
    private val _creditCardData = MutableStateFlow<CreditCardData?>(null)

    override suspend fun setCreditCardData(data: CreditCardData) {
        _creditCardData.emit(data)
    }

    override fun observeCreditCardData(): Flow<CreditCardData> {
        return _creditCardData.filterNotNull()
    }

    override fun startActivity(context: Context) {
        Intent(context, CreditCardScannerActivity::class.java).apply {
            context.startActivity(this)
        }
    }
}
