package com.fypmoney.view.home.main.homescreen.viewmodel

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class HomeActivityVM(application: Application): BaseViewModel(application) {

    val isUnreadNotificationAvailable = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_NEW_MESSAGE
    )
    val userProfileUrl = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_PROFILE_IMAGE
    )


    var toolbarTitle = MutableLiveData(
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

    override fun onCleared() {
        super.onCleared()
        storeFirstTimeUserLandedOnHomeScreen()
    }

    private fun storeFirstTimeUserLandedOnHomeScreen() {
        SharedPrefUtils.getLong(
            PockketApplication.instance,
            SharedPrefUtils.SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME
        ).let {
            if (it == 0L) {
                SharedPrefUtils.putLong(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME, Date().time
                )
            }
        }
    }

    private fun checkUserIsLandedFirstTime(): Boolean {
        SharedPrefUtils.getLong(
            PockketApplication.instance,
            SharedPrefUtils.SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME
        ).let {
            return it == 0L || DateUtils.isToday(it)
        }
    }


    sealed class HomeActivityEvent {
        object ProfileClicked : HomeActivityEvent()
        object NotificationClicked : HomeActivityEvent()
    }
}