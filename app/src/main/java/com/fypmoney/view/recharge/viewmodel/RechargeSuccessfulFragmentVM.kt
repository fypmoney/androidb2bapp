package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class RechargeSuccessfulFragmentVM(application: Application):BaseViewModel(application) {
    var mobile: String? = null
    var amount: String? = null

    val event:LiveData<RechargeSuccessfulEvent>
        get() = _event
    private val _event = LiveEvent<RechargeSuccessfulEvent>()

    fun onContinueClick(){
        _event.value = RechargeSuccessfulEvent.OnDoneAndRetryEvent
    }
    sealed class RechargeSuccessfulEvent{
        object OnDoneAndRetryEvent:RechargeSuccessfulEvent()
    }
}