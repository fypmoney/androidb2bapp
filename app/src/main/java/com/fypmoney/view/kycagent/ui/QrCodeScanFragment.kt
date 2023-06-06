package com.fypmoney.view.kycagent.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentQrCodeScanBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.CaptureManager
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.util.AES
import com.fypmoney.view.kycagent.viewmodel.QrCodeScanFragmentVM
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.delay
import java.util.Arrays

class QrCodeScanFragment : BaseFragment<FragmentQrCodeScanBinding, QrCodeScanFragmentVM>() {

    private lateinit var binding: FragmentQrCodeScanBinding
    private val qrCodeScanFragmentVM by viewModels<QrCodeScanFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var capture: CaptureManager

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int = R.layout.fragment_qr_code_scan

    override fun getViewModel(): QrCodeScanFragmentVM = qrCodeScanFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()
        setUpObserver()
        setupFlashLight()
        setupScanner()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = binding.toolbar,
            isBackArrowVisible = true,
            toolbarTitle = "Complete Full KYC"
        )

//        val decryptedString = AES.decrypt("nbs6dhnafbjK4EGA9kR26MMOeGrRdFTnnUY1VLHUGGtinbMTwKx+9IVR97Cqoegi")
//
//        qrCodeScanFragmentVM.finalQrDecryptCode = decryptedString!!.split("|")
//        performActions()
//
//        Log.d("FinalQR: ", qrCodeScanFragmentVM.finalQrDecryptCode.toString())

    }

    private fun setUpObserver() {
        qrCodeScanFragmentVM.event.observe(viewLifecycleOwner) {
            handelEvent(it)
        }
    }

    private fun handelEvent(it: QrCodeScanFragmentVM.ScanAndPayEvent?) {
        when(it){
            QrCodeScanFragmentVM.ScanAndPayEvent.FlashOff -> {
                binding.ivFlash.invalidate()
                binding.ivFlash.setImageResource(R.drawable.ic_outline_flash_on_24)
            }
            QrCodeScanFragmentVM.ScanAndPayEvent.FlashOn -> {
                binding.ivFlash.invalidate()
                binding.ivFlash.setImageResource(R.drawable.ic_outline_flash_off_24)
            }
            QrCodeScanFragmentVM.ScanAndPayEvent.QRCodeScannedSuccessfully -> {
                binding.zxingBarcodeScanner.pause()

            }
            null -> {}
        }
    }

    private fun setupFlashLight() {
        if (!hasFlash()) {
            binding.ivFlash.toGone()
        } else {
            binding.ivFlash.setImageResource(R.drawable.ic_outline_flash_on_24)
            binding.ivFlash.toVisible()
            binding.ivFlash.setOnClickListener {
                if (qrCodeScanFragmentVM.isFlashOn) {
                    binding.zxingBarcodeScanner.setTorchOff()
                } else {
                    binding.zxingBarcodeScanner.setTorchOn()

                }
            }
        }
    }

    private fun hasFlash(): Boolean {
        return requireContext().applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun setupScanner() {
        capture = CaptureManager(requireActivity(), binding.zxingBarcodeScanner)

        capture.setShowMissingCameraPermissionDialog(true)

        binding.zxingBarcodeScanner.setTorchListener(qrCodeScanFragmentVM)

        val callback: BarcodeCallback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
//                qrCodeScanFragmentVM.parseScanQrCodeString(result.text)

                val decryptedString = AES.decrypt(result.text)

                if (decryptedString != null){
                    qrCodeScanFragmentVM.finalQrDecryptCode = decryptedString.split("|")

                    val lastFourDigit: String = qrCodeScanFragmentVM.finalQrDecryptCode[0].substring(6)

                    qrCodeScanFragmentVM.showSuccessDialog(lastFourDigit)
                    performActions()

                    Log.d("FinalQR: ", qrCodeScanFragmentVM.finalQrDecryptCode.toString())
                }else{
//                    qrCodeScanFragmentVM.showErrorDialog()
                    Utility.showToast("Invalid QR Code")
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        }

        val formats: Collection<BarcodeFormat> =
            Arrays.asList(BarcodeFormat.QR_CODE)
        binding.zxingBarcodeScanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        binding.zxingBarcodeScanner.initializeFromIntent(requireActivity().intent)
        binding.zxingBarcodeScanner.decodeContinuous(callback)
    }

    private fun performActions() {
        if (qrCodeScanFragmentVM.finalQrDecryptCode[2] == AppConstants.YES){
            val bundle = Bundle()
            bundle.putString("mobileNumber", qrCodeScanFragmentVM.finalQrDecryptCode[0])
            bundle.putString("via", "UserKyc")
            findNavController().navigate(
                R.id.navigation_enter_aadhaar_number_kyc,
                bundle,
                navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
        }else{
            val bundle = Bundle()
            bundle.putString("mobileNumber", qrCodeScanFragmentVM.finalQrDecryptCode[0])
            bundle.putString("via", "UserKyc")
            findNavController().navigate(
                R.id.navigation_agent_authentication,
                bundle,
                navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
        }
    }

    private fun pauseScannerAndResumeAfterDelay() = lifecycleScope.launchWhenResumed {
        binding.zxingBarcodeScanner.pause()
        delay(1000)
        binding.zxingBarcodeScanner.resume()
    }

    override fun onTryAgainClicked() {

    }


    override fun onResume() {
        super.onResume()

        Dexter.withContext(requireContext())
            .withPermission(
                Manifest.permission.CAMERA
            )
            .withListener( object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    capture.onResume()
                    binding.zxingBarcodeScanner.resume()
                }
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    if(p0!!.isPermanentlyDenied){
                        showSettingsDialog()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
    }

    override fun onPause() {
        super.onPause()
        if (this::capture.isInitialized){
            capture.onPause()
        }
        binding.zxingBarcodeScanner.pause()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::capture.isInitialized){
            capture.onDestroy()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }


}