package com.haghpanah.scanner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.haghpanah.scanner.databinding.ActivityMainBinding
import com.haghpanah.scanner.domain.NativeLibraryHelper
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.osgi.OpenCVInterface
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    @Inject
    lateinit var cameraPreview: Preview

    @Inject
    lateinit var imageAnalysis: ImageAnalysis

    @Inject
    lateinit var nativeLibraryHelper: NativeLibraryHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var t1 = 0
        var t2 = 0

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermission()
        }

        binding.startAnalytics.setOnClickListener {
            startAnalytics()
        }

        binding.tholdSlider1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean,
            ) {

                binding.thold1Text.text = t1.toString()
                t1 = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                setThreshhold(t1, t2)
            }
        })

        binding.tholdSlider2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                p0: SeekBar?,
                p1: Int,
                p2: Boolean,
            ) {
                binding.thold2Text.text = t2.toString()
                t2 = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                setThreshhold(t1, t2)
            }
        })
    }

    external fun setThreshhold(first: Int, second: Int)

    @OptIn(ExperimentalGetImage::class)
    private fun startAnalytics() {
        imageAnalysis.setAnalyzer(
            //TODO change it to background executor when there was no need of showing in imageView
            ContextCompat.getMainExecutor(this)
        ) { image ->
            val yPlane = image.planes[0]

            val creditCardImage = nativeLibraryHelper.getPreprocessedImage(
                width = image.width,
                height = image.height,
                yBuffer = yPlane.buffer,
                yRowStride = yPlane.rowStride,
            )

            if (creditCardImage != null) {
                binding.imagePreview.setImageBitmap(creditCardImage)
                imageAnalysis.clearAnalyzer()

                image.image?.let {
                    val recognizer =
                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val imageInput =
                        InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)

                    recognizer.process(imageInput).addOnSuccessListener { text ->
                        text.textBlocks.forEach {
                            Log.d("mmd", "analysed ::: ${it.text}")
                        }
                    }.addOnFailureListener {
                        throw it
                    }.addOnCompleteListener {
                        image.close()
                    }
                } ?: run {
                    Log.d("mmd", "startAnalytics: image was null")
                }
            } else {
                image.close()
            }
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