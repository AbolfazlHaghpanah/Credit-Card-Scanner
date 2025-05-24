package com.haghpanah.creditcardscanner.di

import com.haghpanah.creditcardscanner.core.CreditCardScanner
import com.haghpanah.creditcardscanner.core.CreditCardScannerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CoreModule {

    @Provides
    @Singleton
    fun providesCreditCardScanner(impl: CreditCardScannerImpl): CreditCardScanner {
        return CreditCardScannerProvider.getInstance()
    }
}