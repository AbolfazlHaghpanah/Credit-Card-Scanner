package com.haghpanah.scanner.di

import android.util.Size
import android.view.Surface.ROTATION_0
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CameraXModule {

    @Provides
    fun provideCameraPreview(): Preview = Preview
        .Builder()
        .setTargetRotation(ROTATION_0)
        .build()

    @Provides
    fun provideImageAnalytics(): ImageAnalysis = ImageAnalysis
        .Builder()
        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
        .setTargetResolution(Size(1280, 720))
        .setTargetRotation(ROTATION_0)
        .setImageQueueDepth(3)
        .build()
}