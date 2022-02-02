package com.fypmoney.view.bottomsheet

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class UpgradeYourKycBottomSheetVM(application: Application): BaseViewModel(application) {
    val event: LiveData<UpgradeYourKycEvent>
        get() = _event
    private val _event = LiveEvent<UpgradeYourKycEvent>()
    fun onUpgradeKYCClick(){
        _event.value = UpgradeYourKycEvent.UpgradeYourKYC
    }
    sealed class UpgradeYourKycEvent{
        object UpgradeYourKYC:UpgradeYourKycEvent()
    }
}