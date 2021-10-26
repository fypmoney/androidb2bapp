package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.SharedPrefUtils

/*
* This class is used for handling account success creation
* */
class CreateAccountSuccessViewModel(application: Application) : BaseViewModel(application) {
    var onActivateAccountClicked = MutableLiveData<Boolean>()

    /*
* This method is used to handle click of button
* */
    fun onActivateAccountClicked() {
        onActivateAccountClicked.value=true

    }
    init {
        trackr { it.services = arrayListOf(TrackrServices.FIREBASE, TrackrServices.MOENGAGE)
            it.name = TrackrEvent.Account_created
            it.add(
                TrackrField.user_id, SharedPrefUtils.getLong(
                    application,
                    SharedPrefUtils.SF_KEY_USER_ID
                ).toString())
        }
    }

}