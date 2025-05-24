package com.haghpanah.creditcardscanner.core

import android.content.Context
import com.haghpanah.creditcardscanner.data.model.CreditCardData
import kotlinx.coroutines.flow.Flow

abstract class CreditCardScanner {
    internal abstract suspend fun setCreditCardData(data: CreditCardData)

    abstract fun observeCreditCardData(): Flow<CreditCardData>

    abstract fun startActivity(context: Context)
}