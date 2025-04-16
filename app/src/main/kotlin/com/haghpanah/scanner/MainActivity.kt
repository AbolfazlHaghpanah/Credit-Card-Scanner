package com.haghpanah.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_90
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.haghpanah.scanner.databinding.ActivityMainBinding
import com.haghpanah.scanner.domain.NativeLibraryHelper
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    @Inject
    private lateinit var cameraPreview: Preview

    @Inject
    private lateinit var imageAnalysis: ImageAnalysis

    @Inject
    private lateinit var nativeLibraryHelper: NativeLibraryHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermission()
        }

        binding.startAnalytics.setOnClickListener {
            startAnalytics()
        }
    }

    private fun startAnalytics() {
        Log.d("mmd", "startAnalytics:")
        imageAnalysis.setAnalyzer(
            //TODO change it to background executor when there was no need of showing in imageView
            ContextCompat.getMainExecutor(this)
        ) { image ->
            val a = yuv420888ToNv21(image)
            val isCreditCard =
                nativeLibraryHelper.isImageContainsCreditCard(a, image.width, image.height)

            binding.imagePreview.setImageBitmap(
                nativeLibraryHelper.getPreprocessedImage(
                    a,
                    image.width,
                    image.height
                )
            )

            if (isCreditCard) {
                Log.d("mmd", "oooooooo  i found credit card")
//                takePicture()
            }
            image.close()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    cameraPreview,
                    imageAnalysis
                )
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    fun yuv420888ToNv21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)

        val chromaRowStride = image.planes[1].rowStride
        val chromaPixelStride = image.planes[1].pixelStride

        // Interleave U and V like NV21: VU VU VU...
        var offset = ySize
        val width = image.width
        val height = image.height

        val u = ByteArray(uSize)
        val v = ByteArray(vSize)
        uBuffer.get(u)
        vBuffer.get(v)

        for (row in 0 until height / 2) {
            for (col in 0 until width / 2) {
                val uvIndex = row * chromaRowStride + col * chromaPixelStride
                nv21[offset++] = v[uvIndex]  // V first
                nv21[offset++] = u[uvIndex]  // Then U
            }
        }

        return nv21
    }

    private fun ImageProxy.toBitmap(): Bitmap {
        val buffer = planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun requestPermission() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(
                    baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                startCamera()
            }
        }

    companion object {
        private const val LOG_TAG = "cameraError"
        private val REQUIRED_PERMISSIONS =
            mutableListOf<String>(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

    }
}