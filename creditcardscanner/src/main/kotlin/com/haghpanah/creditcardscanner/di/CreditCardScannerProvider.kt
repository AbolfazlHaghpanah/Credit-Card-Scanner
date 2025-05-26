package com.haghpanah.creditcardscanner.di

import com.haghpanah.creditcardscanner.core.CreditCardScanner
import com.haghpanah.creditcardscanner.core.CreditCardScannerImpl

object CreditCardScannerProvider {
    private val creditCardScannerImpl = CreditCardScannerImpl()

    fun getInstance(): CreditCardScanner = creditCardScannerImpl
}
