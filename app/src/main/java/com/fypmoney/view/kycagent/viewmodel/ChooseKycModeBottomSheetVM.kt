package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class ChooseKycModeBottomSheetVM(application: Application) : BaseViewModel(application) {

    val event : LiveData<ChooseKycModeEvent>
        get() = _event
    private val _event = LiveEvent<ChooseKycModeEvent>()

    fun onKycViaQrCode(){
        _event.value = ChooseKycModeEvent.OnKycQrCodeClick
    }

    fun onKycViaOtp(){
        _event.value = ChooseKycModeEvent.OnKycOtpClick
    }

    sealed class ChooseKycModeEvent{
        object OnKycQrCodeClick : ChooseKycModeEvent()
        object OnKycOtpClick : ChooseKycModeEvent()
    }

}