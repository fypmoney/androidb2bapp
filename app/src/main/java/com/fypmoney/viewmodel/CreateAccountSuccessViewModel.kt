package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is used for handling account success creation
* */
class CreateAccountSuccessViewModel(application: Application) : BaseViewModel(application) {
    var schoolName = MutableLiveData<String>()
    var cityName = MutableLiveData<String>()
    var onActivateAccountClicked = MutableLiveData<Boolean>()

    /*
* This method is used to handle click of button
* */
    fun onActivateAccountClicked() {
        onActivateAccountClicked.value=true

    }

}