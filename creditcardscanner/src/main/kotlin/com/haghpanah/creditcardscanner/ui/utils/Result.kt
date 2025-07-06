package com.haghpanah.creditcardscanner.ui.utils

sealed class Result<out D>(val data: D?) {
    data class Success<out T>(val finalValue: T) : Result<T>(finalValue)
    data class Fail(val error: Throwable) : Result<Nothing>(null)
    data object Loading : Result<Nothing>(null)
    data object Idle : Result<Nothing>(null)
}