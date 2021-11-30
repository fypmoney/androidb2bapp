package com.fypmoney.view.home.main.homescreen.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import kotlinx.coroutines.flow.MutableStateFlow

class HomeActivityVM(application: Application): BaseViewModel(application) {

    val isUnreadNotificationAvailable = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_NEW_MESSAGE
    )
    val userProfileUrl = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_PROFILE_IMAGE
    )
    val userGreetingsTitle:LiveData<String> = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},")

    val event:LiveData<HomeActivityEvent>
        get() = _event
    private var _event = LiveEvent<HomeActivityEvent>()

    fun onProfileClicked(){
        _event.value = HomeActivityEvent.ProfileClicked
    }
    fun onNotificationClicked(){
        _event.value = HomeActivityEvent.NotificationClicked
    }


    sealed class HomeActivityEvent{
        object ProfileClicked:HomeActivityEvent()
        object NotificationClicked:HomeActivityEvent()
    }
}