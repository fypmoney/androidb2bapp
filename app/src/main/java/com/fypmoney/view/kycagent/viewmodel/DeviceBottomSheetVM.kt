package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class DeviceBottomSheetVM(application: Application) : BaseViewModel(application) {

    val event : LiveData<DeviceResultEvent>
        get() = _event
    private val _event = LiveEvent<DeviceResultEvent>()

    fun onContinueClick(){
        _event.value = DeviceResultEvent.OnContinueClick
    }

    sealed class DeviceResultEvent{
        object OnContinueClick : DeviceResultEvent()
    }

}