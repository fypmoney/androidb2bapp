package com.fypmoney.view.ordercard.cardorderoffer

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class CardOrderOfferVM(application: Application):BaseViewModel(application) {
    val event:LiveData<CardOfferEvent>
        get() = _event
    private val _event = LiveEvent<CardOfferEvent>()

    fun onContinueClicked(){
        _event.value = CardOfferEvent.Continue
    }
    sealed class CardOfferEvent{
        object Continue:CardOfferEvent()
    }
}