package com.haghpanah.creditcardscanner.data.model

data class CreditCardData(
    val number: String,
    val cvv2: String? = null,
    val expireMonth: String? = null,
    val expireYear: String? = null,
    val shaba: String? = null,
    val bank: String? = null,
) {
    class Builder() {
        private lateinit var number: String
        private var cvv2: String? = null
        private var expireMonth: String? = null
        private var expireYear: String? = null
        private var shaba: String? = null
        private var bank: String? = null

        fun setNumber(value: String) {
            number = value
        }

        fun setCvv2(value: String) {
            cvv2 = value
        }

        fun setExpireMonth(value: String) {
            expireMonth = value
        }

        fun setExpireYear(value: String) {
            expireYear = value
        }

        fun setShaba(value: String) {
            shaba = value
        }

        fun setBank(value: String) {
            bank = value
        }

        fun build(): CreditCardData {
            return CreditCardData(
                number = number,
                cvv2 = cvv2,
                expireMonth = expireMonth,
                expireYear = expireYear,
                shaba = shaba,
                bank = bank
            )
        }
    }
}
