package com.fypmoney.view.kycagent.viewmodel

import android.R.attr.text
import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
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

    sealed class ScanAndPayEvent{
        object FlashOn: ScanAndPayEvent()
        object FlashOff: ScanAndPayEvent()
        object QRCodeScannedSuccessfully:ScanAndPayEvent()
    }
}