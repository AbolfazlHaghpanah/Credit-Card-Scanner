package com.haghpanah.scanner

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.opencv.android.OpenCVLoader

@HiltAndroidApp
class ScannerApplication : Application() {
    init {
        System.loadLibrary("opencv_java4")
        System.loadLibrary("scanner")

        if (OpenCVLoader.initLocal()) {
            Log.i("OpenCV", "OpenCV successfully loaded.");
        }
    }
}