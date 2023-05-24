package com.fypmoney.view.kycagent.viewmodel

import android.R.attr.text
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.journeyapps.barcodescanner.DecoratedBarcodeView


class QrCodeScanFragmentVM(application: Application) : BaseViewModel(application),
    DecoratedBarcodeView.TorchListener {

    var isFlashOn:Boolean = false
    val event: LiveData<ScanAndPayEvent>
        get() = _event
    private val _event = LiveEvent<ScanAndPayEvent>()
    lateinit var finalQrDecryptCode: List<String>


    override fun onTorchOn() {
        isFlashOn = true
        _event.value = ScanAndPayEvent.FlashOn
    }

    override fun onTorchOff() {
        isFlashOn = false
        _event.value = ScanAndPayEvent.FlashOff
    }

    fun showErrorDialog(){
        alertDialog.value = DialogUtils.AlertStateUiModel(
            icon = ContextCompat.getDrawable(
                PockketApplication.instance,
                R.drawable.ic_error_alert
            )!!,
            message = "Invalid QR Code",
            backgroundColor = ContextCompat.getColor(
                PockketApplication.instance,
                R.color.errorAlertBgColor
            )
        )
    }

    fun showSuccessDialog(mobile: String){
        alertDialog.value = DialogUtils.AlertStateUiModel(
            icon = ContextCompat.getDrawable(
                PockketApplication.instance,
                R.drawable.ic_success_alert
            )!!,
            message = "Customer verified with mobile number xxxxxx$mobile",
            backgroundColor = ContextCompat.getColor(
                PockketApplication.instance,
                R.color.successAlertBgColor
            )
        )
    }

    sealed class ScanAndPayEvent{
        object FlashOn: ScanAndPayEvent()
        object FlashOff: ScanAndPayEvent()
        object QRCodeScannedSuccessfully:ScanAndPayEvent()
    }
}