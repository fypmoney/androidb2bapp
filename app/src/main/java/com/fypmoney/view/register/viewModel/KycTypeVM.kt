package com.fypmoney.view.register.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is used for handling aadhaar verification
* */
class KycTypeVM(application: Application) : BaseViewModel(application) {


    var isAadhaarClicked = MutableLiveData<Boolean>()
    var isPanClick = MutableLiveData<Boolean>()
    var isContinueClick = MutableLiveData<Boolean>()
    var isSkipToHomeClick = MutableLiveData<Boolean>()


    fun onTeenagerClick() {
        isAadhaarClicked.value = true

    }

    fun onGuardianClick() {
        isPanClick.value = true
    }

    fun onContinue() {
        isContinueClick.value = true
    }

    fun onSkipToHome() {
        isSkipToHomeClick.value = true
    }
}