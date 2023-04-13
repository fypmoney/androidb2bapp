package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class DeviceDriverBottomSheetVM(application: Application) : BaseViewModel(application) {

    val event : LiveData<DeviceDriverEvent>
        get() = _event
    private val _event = LiveEvent<DeviceDriverEvent>()

    fun onInstallClick(){
        _event.value = DeviceDriverEvent.OnInstallClick
    }

    sealed class DeviceDriverEvent{
        object OnInstallClick : DeviceDriverEvent()
    }
}