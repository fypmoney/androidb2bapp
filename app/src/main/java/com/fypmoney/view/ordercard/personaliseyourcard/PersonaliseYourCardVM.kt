package com.fypmoney.view.ordercard.personaliseyourcard

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.ordercard.model.UserOfferCard

class PersonaliseYourCardVM(application: Application):BaseViewModel(application) {
    var userOfferCard: UserOfferCard? = null

    val event:LiveData<PersonaliseCardEvents>
        get() = _event
    private val _event = LiveEvent<PersonaliseCardEvents>()


    fun onPerfectClicked(){
        _event.value = PersonaliseCardEvents.PersonaliseComplete
    }
    sealed class PersonaliseCardEvents{
        object PersonaliseComplete:PersonaliseCardEvents()
    }
}