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


    /*   //Getting the scan results
       override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
           super.onActivityResult(requestCode, resultCode, data)
           *//*   val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
           if (result != null) {
               //if qrcode has nothing in it
               if (result.contents == null) {
                   Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show()
               } else {
                   //if qr contains data
                   try {
                       //converting the data to json
                       val obj = JSONObject(result.contents)
                       //setting values to textviews
                    } catch (e: JSONException) {
                       e.printStackTrace()
                       //if control comes here
                       //that means the encoded format not matches
                       //in this case you can display whatever data is available on the qrcode
                       //to a toast
                       Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                   }
               }
           } else {
               super.onActivityResult(requestCode, resultCode, data)
           *//*
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Utility.showToast(getString(R.string.qr_scan_issue))
                finish()
            } else {
                Log.d("sjfneji", result.contents)
                intentToActivity(
                    contactEntity = ContactEntity(),
                    aClass = EnterAmountForPayRequestView::class.java,
                    AppConstants.PAY_USING_QR,
                    result.contents
                )
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
        integrator = IntentIntegrator(this)
        integrator?.setPrompt("Scan a QR code")
        integrator?.setCameraId(0) // Use a specific camera of the device
        integrator?.setOrientationLocked(true)
        integrator?.setBeepEnabled(false)
        integrator?.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator?.captureActivity = CaptureActivityPortrait::class.java
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


}

