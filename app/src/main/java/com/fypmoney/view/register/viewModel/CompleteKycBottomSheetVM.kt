package com.fypmoney.view.register.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class CompleteKycBottomSheetVM(application: Application):BaseViewModel(application) {
    val event: LiveData<CompleteKycEvent>
        get() = _event
    private val _event = LiveEvent<CompleteKycEvent>()
    fun onCompleteKycClicked(){
        _event.value = CompleteKycEvent.GotItEvent
    }
    sealed class CompleteKycEvent{
        object GotItEvent:CompleteKycEvent()
    }
}