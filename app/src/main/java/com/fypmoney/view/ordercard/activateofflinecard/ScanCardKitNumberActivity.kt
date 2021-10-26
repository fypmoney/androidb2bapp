package com.fypmoney.view.ordercard.activateofflinecard

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityScanCardKitNumberBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.CaptureManager
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.SetPinView
import com.fypmoney.view.fragment.ActivateCardBottomSheet
import com.fypmoney.view.fragment.SetOrChangePinBottomSheet
import com.fypmoney.view.setpindialog.SetPinDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import kotlinx.android.synthetic.main.dialog_burn_mynts.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import java.util.*

class ScanCardKitNumberActivity : BaseActivity<ActivityScanCardKitNumberBinding,ScanCardKitNumberActivityVM>(),
    TorchListener {
    lateinit var mViewModel:ScanCardKitNumberActivityVM
    lateinit var binding: ActivityScanCardKitNumberBinding
    private var isFlashOn:Boolean = false
    private lateinit var capture: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setupViews()
        setupFlashLight()
        setupScanner()
        setUpObserver()
        handleState()
    }

    private fun handleState() {
        mViewModel.state.observe(this,{
            when(it){
                ScanCardKitNumberActivityVM.ScanCardKitNumberState.KitNumberVerified -> {
                    callActivateCardSheet()
                }
            }
        })
    }

    private fun setUpObserver() {
        mViewModel.event.observe(this,{
            when(it){
                ScanCardKitNumberActivityVM.ScanCardKitNumberEvent.OnVerifyKitNumberClickedEvent ->{
                    mViewModel.checkKitNumberIsValid(binding.kitNumberEt.text.toString())
                }
                ScanCardKitNumberActivityVM.ScanCardKitNumberEvent.OnActivateCardSuccess -> {
//                    callSetPinBottomSheet()
                    val setPinFragment = SetPinDialogFragment(setPinClickListener = {
                        mViewModel.callSetOrChangeApi(binding.kitNumberEt.text.toString())
                    })
                    setPinFragment.show(supportFragmentManager, "set pin")

                }
                is ScanCardKitNumberActivityVM.ScanCardKitNumberEvent.SetPinSuccess -> {
                    it.setpinResponseDetails.url.let {
                        intentToActivity(
                            SetPinView::class.java,
                            type = AppConstants.SET_PIN_URL,
                            url = it
                        )
                    }
                }
                ScanCardKitNumberActivityVM.ScanCardKitNumberEvent.OnActivateCardFaliure ->{
                    finish()
                }
            }
        })

        mViewModel.errorRecived.observe(this, {
            if (it != null) {
                Utility.showDialog(this@ScanCardKitNumberActivity,
                    info = "Scanned code is invalid",
                    cancelable = false,
                    positiveButton = "Retry",
                    positiveListener = DialogInterface.OnClickListener
                    { p0, p1 ->
                        binding.zxingBarcodeScanner.resume()


                    },
                    negativeListener = null,
                    negativeButton = null
                )
            }
            mViewModel.errorRecived.postValue(null)


        }
        )
    }


    /*
    * This method is used to call set spending limit fragment
    * */
    private fun callActivateCardSheet() {
        val bottomSheet =
            ActivateCardBottomSheet(object : ActivateCardBottomSheet.OnActivateCardClickListener {
                override fun onActivateCardClick(kitFourDigit: String?) {
                    mViewModel.callActivateCardApi(kitFourDigit)
                }

                override fun onPrivacyPolicyTermsClicked(title: String, url: String) {
                    openWebPageFor(title, url)
                }
            }, object : ActivateCardBottomSheet.OnActivateSheetDismissListner {
                override fun OnDismiss() {
//                    finish()
                }

            })
        bottomSheet.isCancelable = false
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "ActivateCard")
    }

    /*
   * This method is used to call set or change pin
   * */
    private fun callSetPinBottomSheet() {
        val bottomSheet =
            SetOrChangePinBottomSheet(object :
                SetOrChangePinBottomSheet.OnSetOrChangePinClickListener {
                override fun setPinClick() {
                    mViewModel.callSetOrChangeApi(binding.kitNumberEt.text.toString())
                }

            }, object : SetOrChangePinBottomSheet.OnSetOrChangePinDismissListener {
                override fun OnSheetDismissed() {
//                    finish()
                }
            })
        bottomSheet.isCancelable = false
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "SetPin")
    }
    private fun intentToActivity(aClass: Class<*>, type: String? = null, url: String? = null) {
        val intent = Intent(this@ScanCardKitNumberActivity, aClass)
        if (type == AppConstants.SET_PIN_URL) {
            intent.putExtra(AppConstants.SET_PIN_URL, url)
        }
        startActivity(intent)
        finish()
    }
    private fun setupViews() {
        setToolbarAndTitle(
            context = this@ScanCardKitNumberActivity,
            toolbar = toolbar,
            toolbarTitle = getString(R.string.scan_kit_number),
            isBackArrowVisible = true,
            backArrowTint = Color.WHITE,
            titleColor = Color.WHITE
        )
        binding.kitNumberEt.doOnTextChanged { text, start, before, count ->
//            binding.verifyKitNumberBtn.isEnabled = text?.length == 10
            if (text?.length == 0) {
                binding.zxingBarcodeScanner.resume()
            } 
        }
    }

    private fun setupScanner() {
        capture = CaptureManager(this, binding.zxingBarcodeScanner)

        capture.setShowMissingCameraPermissionDialog(true)

        binding.zxingBarcodeScanner.setTorchListener(this)

        val callback: BarcodeCallback = object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (result.text.matches(Regex("[0-9]+"))
                    && result.text.length == 10
                ) {
                    binding.kitNumberEt.setText(result.text)
                    binding.zxingBarcodeScanner.pause()
                    mViewModel.verifyKitNumber()

                    Log.d("kitNumber", result.text)
                } else {
                    binding.zxingBarcodeScanner.pause()
                    Utility.showDialog(this@ScanCardKitNumberActivity,
                        info = "Scanned code is invalid",
                        cancelable = false,
                        positiveButton = "Retry",
                        positiveListener = DialogInterface.OnClickListener
                        { p0, p1 ->
                            binding.zxingBarcodeScanner.resume()


                        },
                        negativeListener = null,
                        negativeButton = null
                    )

//                    Toast.makeText(
//                        binding.kitNumberEt.context,
//                        getString(R.string.please_scan_valid_qr_code),
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            }

            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        }

        val formats: Collection<BarcodeFormat> =
            Arrays.asList(BarcodeFormat.QR_CODE)
        binding.zxingBarcodeScanner.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        binding.zxingBarcodeScanner.initializeFromIntent(intent)
        binding.zxingBarcodeScanner.decodeContinuous(callback)
    }

    private fun setupFlashLight() {
        if (!hasFlash()) {
            binding.toolbarLayout.toolbar_image.visibility = View.GONE
        } else {
            binding.toolbarLayout.toolbar_image.setImageResource(R.drawable.ic_outline_flash_on_24)
            binding.toolbarLayout.toolbar_image.visibility = View.VISIBLE
            binding.toolbarLayout.toolbar_image.setOnClickListener {
                if(isFlashOn){
                    binding.zxingBarcodeScanner.setTorchOff()
                }else{
                    binding.zxingBarcodeScanner.setTorchOn()

                }
            }
        }
    }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId() = R.layout.activity_scan_card_kit_number

    override fun getViewModel(): ScanCardKitNumberActivityVM {
        mViewModel = ViewModelProvider(this).get(ScanCardKitNumberActivityVM::class.java)
        return mViewModel
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onResume() {
        super.onResume()
        if (!mViewModel.isAlreadyCaptured) {
            capture.onResume()
            binding.zxingBarcodeScanner.resume()
        }

    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
        binding.zxingBarcodeScanner.pause()

    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onTorchOn() {
        isFlashOn = true
        binding.toolbarLayout.toolbar_image.invalidate()
        binding.toolbarLayout.toolbar_image.setImageResource(R.drawable.ic_outline_flash_off_24)
    }

    override fun onTorchOff() {
        isFlashOn = false
        binding.toolbarLayout.toolbar_image.invalidate()
        binding.toolbarLayout.toolbar_image.setImageResource(R.drawable.ic_outline_flash_on_24)
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