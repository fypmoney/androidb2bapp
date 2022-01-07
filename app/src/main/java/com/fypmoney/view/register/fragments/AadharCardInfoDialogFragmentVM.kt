package com.fypmoney.view.register.fragments

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class AadharCardInfoDialogFragmentVM (application: Application): BaseViewModel(application) {

    val event: LiveData<AadharCardInfoDialogEvent>
        get() =_event
    private val _event = LiveEvent<AadharCardInfoDialogEvent>()

    fun onCloseClicked(){
        _event.value = AadharCardInfoDialogEvent.OnCloseClick
    }

    sealed class AadharCardInfoDialogEvent{
        object OnCloseClick:AadharCardInfoDialogEvent()
    }
}