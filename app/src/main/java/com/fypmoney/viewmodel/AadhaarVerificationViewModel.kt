package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.SharedPrefUtils
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
            aadhaarNumber.value?.length!! <16-> {
                Utility.showToast(PockketApplication.instance.getString(R.string.aadhaar_number_valid_error))
            }

            else -> {
                SharedPrefUtils.putString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_AADHAAR_NUMBER,
                    aadhaarNumber.value?.trim()
                )
                onGetOtpClicked.value = true
            }
        }

    }

}