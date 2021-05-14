package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is used for handling community
* */
class CommunityViewModel(application: Application) : BaseViewModel(application) {
    var schoolName = MutableLiveData<String>()
    var cityName = MutableLiveData<String>()
    var onContinueClicked = MutableLiveData<Boolean>()

    /*
* This method is used to handle click of continue
* */
    fun onContinueClicked() {
        onContinueClicked.value=true

    }

}