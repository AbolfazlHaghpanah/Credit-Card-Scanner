package com.haghpanah.creditcardscanner.di

import com.haghpanah.creditcardscanner.data.NativeLibraryHelperImpl
import com.haghpanah.creditcardscanner.domain.NativeLibraryHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NativeLibraryModule {

    @Binds
    fun bindsNativeLibraryHelper(
        nativeLibraryHelperImpl: NativeLibraryHelperImpl,
    ): NativeLibraryHelper
}