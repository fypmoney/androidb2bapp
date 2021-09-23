package com.fypmoney.view.setpindialog

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class SetPinFragmentVM(application: Application):BaseViewModel(application) {

    val event:LiveData<SetPinEvent>
         get() =_event
    private val _event = LiveEvent<SetPinEvent>()

    fun onSetPinClicked(){
        _event.value = SetPinEvent.SetPinClick
    }

    sealed class SetPinEvent{
        object SetPinClick:SetPinEvent()
    }
}