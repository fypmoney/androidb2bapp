package com.fypmoney.viewmodel


import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is used to handle qr code
* */
class QrCodeScannerViewModel(application: Application) : BaseViewModel(application) {
    var onContinueClicked = MutableLiveData(false)



}


