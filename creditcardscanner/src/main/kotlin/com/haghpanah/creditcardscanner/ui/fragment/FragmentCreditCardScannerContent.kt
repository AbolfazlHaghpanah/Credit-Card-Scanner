package com.haghpanah.creditcardscanner.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.haghpanah.creditcardscanner.Constant
import com.haghpanah.creditcardscanner.ui.theme.CreditCardScannerColors
import com.haghpanah.creditcardscanner.ui.viewmodel.CreditCardScannerViewModel
import com.haghpanah.scanner.R
import com.haghpanah.scanner.databinding.FragmentCreditCardScannerContentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentCreditCardScannerContent : Fragment() {

    private lateinit var _binding: FragmentCreditCardScannerContentBinding

    private lateinit var cameraProvider: ProcessCameraProvider

    private val viewModel by viewModels<CreditCardScannerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scanResult.collectLatest {
                    if (it != null) {
                        activity?.finish()
                    }
                }
            }
        }

        _binding = FragmentCreditCardScannerContentBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupComponents()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermission()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                viewModel.setCameraPreviewSurfaceProvider(_binding.previewView.surfaceProvider)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    viewModel.getCameraPreview(),
                    viewModel.getImageAnalytics()
                )
            },
            ContextCompat.getMainExecutor(requireContext())
        )

        viewModel.startAnalytics()
    }

    private fun requestPermission() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun showError(message: String) {
        Snackbar.make(
            requireContext(),
            _binding.root,
            message,
            1000
        ).show()
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
                showError("لطفا")
            } else {
                startCamera()
            }
        }

    private fun setupComponents() {
        val colors = getColorsOrDefault()

        _binding.hint.apply {
            text = context.getString(R.string.message_default_hint)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setBackgroundColor(colors.hintContainerColor)
                setTextColor(colors.hintContentColor)
                alpha = 0.7f
            }
        }
    }

    companion object {
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

private fun FragmentCreditCardScannerContent.getColorsOrDefault(): CreditCardScannerColors {
    val givenColors = arguments?.getSerializable(
        Constant.COLORS_BUNDLE_KEY,
    ) as CreditCardScannerColors?

    return givenColors ?: CreditCardScannerColors(
        topBarContainerColor = requireContext().getColor(R.color.primary),
        topBarContentColor = requireContext().getColor(R.color.on_primary),
        snackbarContainerColor = requireContext().getColor(R.color.snackbar_container),
        snackbarContentColor = requireContext().getColor(R.color.on_surface),
        snackbarActionColor = requireContext().getColor(R.color.error),
        hintContainerColor = requireContext().getColor(R.color.surface),
        hintContentColor = requireContext().getColor(R.color.on_surface),
        loadingDialogContainerColor = requireContext().getColor(R.color.surface),
        loadingDialogContentColor = requireContext().getColor(R.color.on_surface)
    )
}