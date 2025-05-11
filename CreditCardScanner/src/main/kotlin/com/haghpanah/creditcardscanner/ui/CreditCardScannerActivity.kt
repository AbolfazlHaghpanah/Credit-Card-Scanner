package com.haghpanah.creditcardscanner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.fragment.app.commit
import androidx.navigation.ui.AppBarConfiguration
import com.haghpanah.creditcardscanner.domain.NativeLibraryHelper
import com.haghpanah.creditcardscanner.ui.content.CreditCardScannerContentFragment
import com.haghpanah.scanner.databinding.ActivityCreditCardScannerBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreditCardScannerActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityCreditCardScannerBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    @Inject
    lateinit var cameraPreview: Preview

    @Inject
    lateinit var imageAnalysis: ImageAnalysis

    @Inject
    lateinit var nativeLibraryHelper: NativeLibraryHelper

    external fun setThreshhold(first: Int, second: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreditCardScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(binding.contentFragmentContainer.id, CreditCardScannerContentFragment())
        }

//        var t1 = 0
//        var t2 = 0
//
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            requestPermission()
//        }
//
//        binding.startAnalytics.setOnClickListener {
//            startAnalytics()
//        }
//
//        binding.tholdSlider1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(
//                p0: SeekBar?,
//                p1: Int,
//                p2: Boolean,
//            ) {
//
//                binding.thold1Text.text = t1.toString()
//                t1 = p1
//            }
//
//            override fun onStartTrackingTouch(p0: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(p0: SeekBar?) {
//                setThreshhold(t1, t2)
//            }
//        })
//
//        binding.tholdSlider2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(
//                p0: SeekBar?,
//                p1: Int,
//                p2: Boolean,
//            ) {
//                binding.thold2Text.text = t2.toString()
//                t2 = p1
//            }
//
//            override fun onStartTrackingTouch(p0: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(p0: SeekBar?) {
//                setThreshhold(t1, t2)
//            }
//        })
    }
//
//    @OptIn(ExperimentalGetImage::class)
//    private fun startAnalytics() {
//        imageAnalysis.setAnalyzer(
//            //TODO change it to background executor when there was no need of showing in imageView
//            ContextCompat.getMainExecutor(this)
//        ) { image ->
//            val yPlane = image.planes[0]
//
//            val creditCardImage = nativeLibraryHelper.getPreprocessedImage(
//                width = image.width,
//                height = image.height,
//                yBuffer = yPlane.buffer,
//                yRowStride = yPlane.rowStride,
//            )
//
//            if (creditCardImage != null) {
//                binding.imagePreview.setImageBitmap(creditCardImage)
//                imageAnalysis.clearAnalyzer()
//
//                image.image?.let {
//                    val recognizer =
//                        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
//                    val imageInput =
//                        InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
//
//                    recognizer.process(imageInput).addOnSuccessListener { text ->
//                        text.textBlocks.forEach {
//                            Log.d("mmd", "analysed ::: ${it.text}")
//                        }
//                    }.addOnFailureListener {
//                        throw it
//                    }.addOnCompleteListener {
//                        image.close()
//                    }
//                } ?: run {
//                    Log.d("mmd", "startAnalytics: image was null")
//                }
//            } else {
//                image.close()
//            }
//        }
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener(
//            {
//                cameraProvider = cameraProviderFuture.get()
//                cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    this,
//                    CameraSelector.DEFAULT_BACK_CAMERA,
//                    cameraPreview,
//                    imageAnalysis
//                )
//            },
//            ContextCompat.getMainExecutor(this)
//        )
//    }
//
//    private fun requestPermission() {
//        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it
//        ) == PackageManager.PERMISSION_GRANTED
//    }

//    private val activityResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        )
//        { permissions ->
//            // Handle Permission granted/rejected
//            var permissionGranted = true
//            permissions.entries.forEach {
//                if (it.key in REQUIRED_PERMISSIONS && !it.value)
//                    permissionGranted = false
//            }
//            if (!permissionGranted) {
//                Toast.makeText(
//                    baseContext,
//                    "Permission request denied",
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                startCamera()
//            }
//        }
//
//    companion object {
//        init {
//            System.loadLibrary("opencv_java4")
//            System.loadLibrary("scanner")
//
//            if (OpenCVLoader.initLocal()) {
//                Log.i("OpenCV", "OpenCV successfully loaded.");
//            }
//        }
//
////        private const val LOG_TAG = "cameraError"
////        private val REQUIRED_PERMISSIONS =
////            mutableListOf<String>(
////                Manifest.permission.CAMERA,
////                Manifest.permission.RECORD_AUDIO
////            ).apply {
////                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
////                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
////                }
////            }.toTypedArray()
//    }
//
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_credit_card_scanner)
//        return navController.navigateUp(appBarConfiguration)
//            || super.onSupportNavigateUp()
//    }
}