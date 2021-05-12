package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.LeaveFamilyResponse
import com.fypmoney.util.Utility

/*
* This class is used to handle referral code
* */
class ReferralCodeViewModel(application: Application) : BaseViewModel(application) {
    var onContinueClicked = MutableLiveData(false)
    var onSkipClicked = MutableLiveData(false)
    var referralCode = MutableLiveData<String>()

    /*
      * This method is used to handle on click of skip
      * */
    fun onSkipClicked() {
        onSkipClicked.value = true
    }

    /*
    * This method is used to call verify referral code
    * */
    fun callVerifyReferralCode() {
        when {
            TextUtils.isEmpty(referralCode.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.referral_code_empty_error))
            }
            else -> {
                WebApiCaller.getInstance().request(
                    ApiRequest(
                        ApiConstant.API_VERIFY_REFERRAL_CODE,
                        NetworkUtil.endURL(ApiConstant.API_VERIFY_REFERRAL_CODE + referralCode.value?.trim()),
                        ApiUrl.POST,
                        BaseRequest(),
                        this, isProgressBar = false
                    )
                )
            }
        }

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_VERIFY_REFERRAL_CODE -> {
                if (responseData is LeaveFamilyResponse) {
                    // set the button text to continue
                    when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                        }
                    }
                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}


