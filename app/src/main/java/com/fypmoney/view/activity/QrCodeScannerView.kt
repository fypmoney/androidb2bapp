package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.databinding.ViewQrCodeScannerBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.QrCodeScannerViewModel
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used for scan qr code
* */
class QrCodeScannerView : BaseActivity<ViewQrCodeScannerBinding, QrCodeScannerViewModel>() {
    private lateinit var mViewModel: QrCodeScannerViewModel
    private var integrator: IntentIntegrator? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_qr_code_scanner
    }

    override fun getViewModel(): QrCodeScannerViewModel {
        mViewModel = ViewModelProvider(this).get(QrCodeScannerViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@QrCodeScannerView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        integrator = IntentIntegrator(this)
        integrator?.setPrompt("Scan a QR code")
        integrator?.setCameraId(0) // Use a specific camera of the device
        integrator?.setOrientationLocked(true)
        integrator?.setBeepEnabled(false)
        integrator?.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator?.captureActivity = CaptureActivityPortrait::class.java
        checkAndAskPermission()
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }

    /*
       * This method is used to check for permissions
       * */
    private fun checkAndAskPermission() {
        when (checkPermission(Manifest.permission.CAMERA)) {
            true -> {
                callScan()
            }
            else -> {
                requestPermission(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (parseQrCode(result.contents) != null) {
                intentToActivity(
                    contactEntity = ContactEntity(),
                    aClass = EnterAmountForPayRequestView::class.java,
                    AppConstants.PAY_USING_QR,
                    parseQrCode(result.contents).toString()

                )
            } else {
                when (result.formatName) {
                    AppConstants.QR_FORMAT_NAME -> {
                        Utility.showToast(getString(R.string.invalid_qr_code))
                        callScan()
                    }
                    else -> {
                        finish()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                //denied
                Utility.showToast(getString(R.string.permission_camera_required))
                requestPermission(Manifest.permission.CAMERA)
            } else {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //allow
                    callScan()
                } else {
                    //set to never ask again
                    SharedPrefUtils.putBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_STORAGE_PERMANENTLY_DENY,
                        true
                    )
                }
            }
        }


    }
    /*
    * This method is used to call the scan method
    * */

    private fun callScan() {
        integrator?.initiateScan()
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(
        contactEntity: ContactEntity?,
        aClass: Class<*>,
        action: String,
        qrCode: String
    ) {
        val intent = Intent(this@QrCodeScannerView, aClass)
        intent.putExtra(AppConstants.CONTACT_SELECTED_RESPONSE, contactEntity)
        intent.putExtra(AppConstants.WHICH_ACTION, action)
        intent.putExtra(AppConstants.FUND_TRANSFER_QR_CODE, qrCode)
        startActivity(intent)
        finish()
    }

    fun parseQrCode(qrValue: String?): String? {
        val hashMap = HashMap<String, String>()
        var lenConsumed = 0
        try {
            while (lenConsumed < qrValue!!.length) {
                val tag = qrValue.substring(lenConsumed, lenConsumed + 2)
                lenConsumed += 2
                val dataLength = qrValue.substring(lenConsumed, lenConsumed + 2)
                val dataLenValue: Int = dataLength.toInt()
                lenConsumed += 2
                val tagValue = qrValue.substring(lenConsumed, lenConsumed + dataLenValue)
                lenConsumed += dataLenValue
                hashMap[tag] = tagValue
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hashMap["59"]
    }


}

