package com.haghpanah.creditcardscanner

import android.content.Context
import android.content.Intent
import com.haghpanah.creditcardscanner.ui.CreditCardScannerActivity

object CreditCardScanner {
    fun startActivity(context: Context) {
        Intent(context, CreditCardScannerActivity::class.java).apply {
            context.startActivity(this)
        }
    }
}