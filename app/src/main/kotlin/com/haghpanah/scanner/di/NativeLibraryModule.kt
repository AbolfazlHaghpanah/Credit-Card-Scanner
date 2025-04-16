package com.haghpanah.scanner.di

import com.haghpanah.scanner.data.NativeLibraryHelperImpl
import com.haghpanah.scanner.domain.NativeLibraryHelper
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