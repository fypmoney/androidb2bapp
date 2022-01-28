package com.fypmoney.view.fragment

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class MaxLoadAmountReachedWarningDialogFragmentVM(application: Application):BaseViewModel(application) {

    val event: LiveData<MaxLoadAmountReachedWarningDialogEvent>
        get() =_event
    private val _event = LiveEvent<MaxLoadAmountReachedWarningDialogEvent>()

    fun onOkClicked(){
        _event.value = MaxLoadAmountReachedWarningDialogEvent.OkEvent
    }

    sealed class MaxLoadAmountReachedWarningDialogEvent{
        object OkEvent:MaxLoadAmountReachedWarningDialogEvent()
    }
}