package com.fypmoney.view.home.main.home.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class UpiComingSoonVM(application: Application):BaseViewModel(application) {
    val event:LiveData<UpiComingSoonEvent>
        get() = _event
    private val _event = LiveEvent<UpiComingSoonEvent>()
    fun onGotItClicked(){
        _event.value = UpiComingSoonEvent.GotItEvent
    }
    sealed class UpiComingSoonEvent{
        object GotItEvent:UpiComingSoonEvent()
    }
}