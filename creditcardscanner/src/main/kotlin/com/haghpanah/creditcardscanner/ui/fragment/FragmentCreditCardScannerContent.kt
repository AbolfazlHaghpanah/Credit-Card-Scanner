package com.haghpanah.creditcardscanner.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.health.connect.datatypes.units.Length
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
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
import com.haghpanah.creditcardscanner.Constant.HINT_TEXT_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.HINT_VISIBLE_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.TOP_BAR_TEXT_BUNDLE_KEY
import com.haghpanah.creditcardscanner.Constant.TOP_BAR_VISIBLE_BUNDLE_KEY
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
        val loadingDialogView = layoutInflater.inflate(R.layout.loading_dialog, null)
        loadingDialogView.setBackgroundColor(getColorsOrDefault().loadingDialogContainerColor)

        val loadingDialog = AlertDialog.Builder(requireContext())
            .setView(loadingDialogView)


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
        activityResultLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    fun showError(
        message: String,
        length: Int = Snackbar.LENGTH_LONG,
        @StringRes actionText: Int? = null,
        action: (() -> Unit)? = null,
    ) {
        val color = getColorsOrDefault()
        val snackbar = Snackbar.make(
            requireContext(),
            _binding.root,
            message,
            length
        )
        snackbar.setText(message)
        snackbar.setBackgroundTint(color.snackbarContainerColor)
        snackbar.setTextColor(color.snackbarContentColor)
        if (action != null) {
            snackbar.setActionTextColor(color.snackbarActionColor)
            snackbar.setAction(
                actionText ?: R.string.label_try_again,
            ) {
                action()
            }
        }
        snackbar.show()
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                showError(getString(R.string.message_camera_permission_denied)) {
                    requestPermission()
                }
            } else {
                startCamera()
            }
        }

    private fun setupComponents() {
        val colors = getColorsOrDefault()
        val shouldHintVisible = arguments?.getBoolean(HINT_VISIBLE_BUNDLE_KEY)
        val hintText = arguments
            ?.getString(HINT_TEXT_BUNDLE_KEY)
            ?: getString(R.string.message_default_hint)
        val shouldToolbarVisible = arguments?.getBoolean(TOP_BAR_VISIBLE_BUNDLE_KEY)
        val toolBarText = arguments
            ?.getString(TOP_BAR_TEXT_BUNDLE_KEY)
            ?: getString(R.string.default_toolbar_text)

        _binding.hint.apply {
            visibility = if (shouldHintVisible != false) VISIBLE else GONE
            text = hintText
            setBackgroundColor(colors.hintContainerColor)
            setTextColor(colors.hintContentColor)
            alpha = 0.7f
        }
        _binding.toolbar.apply {
            visibility = if (shouldToolbarVisible == true) VISIBLE else GONE
            setBackgroundColor(colors.topBarContainerColor)
            textAlignment = TEXT_ALIGNMENT_CENTER
            title = toolBarText
            setTitleTextColor(colors.topBarContentColor)
        }
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
        snackbarContentColor = requireContext().getColor(R.color.black),
        snackbarActionColor = requireContext().getColor(R.color.error),
        hintContainerColor = requireContext().getColor(R.color.surface),
        hintContentColor = requireContext().getColor(R.color.on_surface),
        loadingDialogContainerColor = requireContext().getColor(R.color.surface),
        loadingDialogContentColor = requireContext().getColor(R.color.on_surface)
    )
}