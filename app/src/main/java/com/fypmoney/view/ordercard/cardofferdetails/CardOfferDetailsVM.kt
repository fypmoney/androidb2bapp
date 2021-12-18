package com.fypmoney.view.ordercard.cardofferdetails

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.ordercard.model.UserOfferCard

class CardOfferDetailsVM(application: Application):BaseViewModel(application) {
    var userOfferCard: UserOfferCard? = null

    val event: LiveData<CardOfferDetailsEvent>
        get() = _event
    private val _event = LiveEvent<CardOfferDetailsEvent>()

    fun onContinueClicked(){
        _event.value = CardOfferDetailsEvent.ContinueEvent
    }

    sealed class CardOfferDetailsEvent{
        object ContinueEvent:CardOfferDetailsEvent()
    }

}