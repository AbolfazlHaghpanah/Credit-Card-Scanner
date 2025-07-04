package com.haghpanah.creditcardscanner.ui.colors

import android.graphics.Color
import android.graphics.fonts.FontStyle

object CreditCardScannerDefault {
    internal lateinit var colors: Colors
        private set
    internal lateinit var typography: Typography
        private set
    internal lateinit var language: Language
        private set
    internal var hintText: String? = null
        private set
    internal var topBarText: String? = null
        private set

    internal fun initiate(
        colors: Colors?,
        typography: Typography?,
        hintText: String?,
        topBarText: String?,
        shouldTopBarVisible: Boolean?,
        shouldShowHintText: Boolean?,
        language: Language?,
    ) {
        this.colors = colors ?: TODO()
        this.language = language ?: TODO()
        this.typography = typography ?: TODO()
        this.hintText = if (shouldShowHintText != false) {
            hintText ?: TODO("defualt Hint Text")
        } else {
            null
        }
        this.topBarText = if (shouldTopBarVisible != false) {
            topBarText ?: TODO("defualt topbar Text")
        } else {
            null
        }
    }

    data class Typography(
        val topBarTypography: FontStyle,
        val snackbarContentTypography: FontStyle,
        val snackbarActionTypography: FontStyle,
        val hintTypography: FontStyle,
    )

    data class Colors(
        val topBarContainerColor: Color,
        val topBarContentColor: Color,
        val snackbarContainerColor: Color,
        val snackbarContentColor: Color,
        val snackbarActionColor: Color,
        val hintContainerColor: Color,
        val hintContentColor: Color,
        val loadingDialogContainerColor: Color,
        val loadingDialogContentColor: Color,
    )

    enum class Language {
        En,
        Fa
    }
}