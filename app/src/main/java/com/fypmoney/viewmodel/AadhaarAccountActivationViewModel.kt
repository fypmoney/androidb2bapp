package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.Utility

/*
* This class is used for handling aadhaar verification using otp
* */
class AadhaarAccountActivationViewModel(application: Application) : BaseViewModel(application) {
    var onActivateAccountClicked = MutableLiveData<Boolean>()

    /*
* This method is used to handle click of activate account using Aadhaar card
* */
    fun onActivateAccountClicked() {
        onActivateAccountClicked.value = true


    }

}