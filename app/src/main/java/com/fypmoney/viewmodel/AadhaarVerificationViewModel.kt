package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.Utility

/*
* This class is used for handling aadhaar verification
* */
class AadhaarVerificationViewModel(application: Application) : BaseViewModel(application) {
    var aadhaarNumber = MutableLiveData<String>()
    var onGetOtpClicked = MutableLiveData<Boolean>()

    /*
* This method is used to handle click of get otp
* */
    fun onGetOtpClicked() {
        when {
            TextUtils.isEmpty(aadhaarNumber.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.aadhaar_number_empty_error))
            }

            else -> {
                onGetOtpClicked.value = true
            }
        }

    }

}