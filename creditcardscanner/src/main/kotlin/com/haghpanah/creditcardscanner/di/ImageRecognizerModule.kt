package com.haghpanah.creditcardscanner.di

import com.haghpanah.creditcardscanner.data.imagerecognizer.ImageRecognizerImpl
import com.haghpanah.creditcardscanner.data.imagerecognizer.ImageRecognizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ImageRecognizerModule {

    @Binds
    fun bindsImageRecognizer(
        imageRecognizerImpl: ImageRecognizerImpl,
    ): ImageRecognizer
}