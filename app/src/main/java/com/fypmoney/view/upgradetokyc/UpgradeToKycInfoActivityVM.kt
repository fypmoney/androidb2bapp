package com.fypmoney.view.upgradetokyc

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class UpgradeToKycInfoActivityVM(application: Application) : BaseViewModel(application) {

    val event: LiveData<UpgradeToKYCEvent>
        get() = _event
    private val _event = LiveEvent<UpgradeToKYCEvent>()

    fun upgradeToKyc(){
        _event.value = UpgradeToKYCEvent.UpgradeKycClick
    }
    sealed class UpgradeToKYCEvent{
        object UpgradeKycClick:UpgradeToKYCEvent()
    }

}