package com.haghpanah.creditcardscanner.di

import com.haghpanah.creditcardscanner.data.textrecognizer.TextRecognizer
import com.haghpanah.creditcardscanner.data.textrecognizer.TextRecognizerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TextRecognizerModule {

    @Binds
    abstract fun bindsTextRecognizer(
        textRecognizerImpl: TextRecognizerImpl,
    ): TextRecognizer
}