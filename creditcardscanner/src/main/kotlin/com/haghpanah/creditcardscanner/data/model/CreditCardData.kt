package com.haghpanah.creditcardscanner.data.model

data class CreditCardData(
    val number: String,
    val cvv2: String? = null,
    val expireMonth: String? = null,
    val expireYear: String? = null,
    val shaba: String? = null,
    val bank: String? = null,
)
