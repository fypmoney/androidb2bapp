package com.fypmoney.view.register.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is used for handling aadhaar verification
* */
class UserTypeOnLoginViewModel(application: Application) : BaseViewModel(application) {
    var isTeenagerClicked = MutableLiveData<Boolean>()
    var isGuardianClick = MutableLiveData<Boolean>()
    var isContinueClick = MutableLiveData<Boolean>()


    fun onTeenagerClick() {
        isTeenagerClicked.value = true

    }

    fun onGuardianClick() {
        isGuardianClick.value = true
    }

    fun onContinue() {
        isContinueClick.value = true
    }
}