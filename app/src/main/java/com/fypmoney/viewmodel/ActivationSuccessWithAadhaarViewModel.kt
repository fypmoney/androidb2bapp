package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This class is is used to display message in case aadhaar verification success
* */
class ActivationSuccessWithAadhaarViewModel(application: Application) : BaseViewModel(application) {
    var onContinueClicked = MutableLiveData<Boolean>()

    /*
    * This method is used to handle continue
    * */
    fun onContinueClicked() {
        onContinueClicked.value = true


    }


}