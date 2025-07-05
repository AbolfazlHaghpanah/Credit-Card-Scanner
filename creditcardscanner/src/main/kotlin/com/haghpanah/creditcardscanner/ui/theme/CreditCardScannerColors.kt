package com.haghpanah.creditcardscanner.ui.theme

import androidx.annotation.ColorInt
import java.io.Serializable

data class CreditCardScannerColors(
    @ColorInt val topBarContainerColor: Int,
    @ColorInt val topBarContentColor: Int,
    @ColorInt val snackbarContainerColor: Int,
    @ColorInt val snackbarContentColor: Int,
    @ColorInt val snackbarActionColor: Int,
    @ColorInt val hintContainerColor: Int,
    @ColorInt val hintContentColor: Int,
    @ColorInt val loadingDialogContainerColor: Int,
    @ColorInt val loadingDialogContentColor: Int,
) : Serializable
