package com.fypmoney.view.ordercard.placeordersuccess

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class PlaceOrderSuccessVM(application: Application):BaseViewModel(application) {

    val event:LiveData<PlaceOrderSuccessEvent>
        get() = _event
    private val _event = LiveEvent<PlaceOrderSuccessEvent>()

    fun onTrackOrderClicked(){
        _event.value = PlaceOrderSuccessEvent.TrackOrderEvent
    }

    sealed class PlaceOrderSuccessEvent{
        object TrackOrderEvent:PlaceOrderSuccessEvent()
    }
}