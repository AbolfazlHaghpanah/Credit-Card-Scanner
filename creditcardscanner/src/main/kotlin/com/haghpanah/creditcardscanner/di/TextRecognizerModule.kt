package com.haghpanah.creditcardscanner.di

import com.haghpanah.creditcardscanner.data.textrecognizer.TextRecognizer
import com.haghpanah.creditcardscanner.data.textrecognizer.TextRecognizerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TextRecognizerModule {

    @Binds
    @Singleton
    abstract fun bindsTextRecognizer(
        textRecognizerImpl: TextRecognizerImpl,
    ): TextRecognizer

//    companion object {
//        @Provides
//        @Singleton
//        fun provede
//    }
}