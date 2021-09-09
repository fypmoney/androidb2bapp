package com.fypmoney.view.ordercard

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent


class OrderCardViewModel(application: Application) : BaseViewModel(application) {

    val event: LiveData<OrderCardEvent>
        get() = _event
    private val _event = LiveEvent<OrderCardEvent>()


    fun onOrderCardClicked() {
        _event.value = OrderCardEvent.GetOrderCardEvent
    }


    sealed class OrderCardEvent {
        object GetOrderCardEvent : OrderCardEvent()
    }
}