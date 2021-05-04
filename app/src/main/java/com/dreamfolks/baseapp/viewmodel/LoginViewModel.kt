package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.model.AuthLoginResponse
import com.dreamfolks.baseapp.model.BaseRequest
import com.dreamfolks.baseapp.model.LoginInitRequest
import com.dreamfolks.baseapp.model.LoginInitResponse
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.util.Utility

/*
* This is used to login
* */
class LoginViewModel(application: Application) : BaseViewModel(application) {
    var mobile = MutableLiveData<String>()
    var onMobileClicked = MutableLiveData<Boolean>()
    var onContinueClicked = MutableLiveData<Boolean>()
    var onOtpSentSuccess = MutableLiveData<Boolean>()
    var selectedCountryCodePosition = ObservableField(0)
    var minTextLength = 4
    var maxTextLength = 15
    var selectedCountryCode = ObservableField<String>()
    var isMobileEnabled = ObservableField(true)
    var isMobileFocusable = ObservableField(false)
    var isMobileEditableVisible = ObservableField(false)


    /*
    * This method is used to handle click of mobile
    * */
    fun onMobileClick() {
        onMobileClicked.value = true
    }

    /*
       * This method is used to handle click of continue
       * */
    fun onContinueClicked() {
        onContinueClicked.value = true
    }

    /*
      * This method is used to call auth login API
      * */
    fun callAuthLoginApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_AUTH_LOGIN,
                NetworkUtil.endURL(ApiConstant.API_AUTH_LOGIN),
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }
    /*
 * This method is used to call send otp API
 * */
    fun callSendOtpApi() {
        when {
            TextUtils.isEmpty(mobile.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.phone_email_empty_error))
            }
            else -> {
                WebApiCaller.getInstance().request(
                    ApiRequest(
                        ApiConstant.API_LOGIN_INIT,
                        NetworkUtil.endURL(ApiConstant.API_LOGIN_INIT),
                        ApiUrl.POST,
                        LoginInitRequest(
                            identifierType = AppConstants.MOBILE_TYPE,
                            identifier = mobile.value!!
                        ),
                        this, isProgressBar = true
                    )
                )
            }
        }

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_AUTH_LOGIN -> {
                if (responseData is AuthLoginResponse) {
                    // Save the access token in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_ACCESS_TOKEN,
                        value = responseData.authLoginResponseDetails?.access_token!!
                    )
                }
            }
            ApiConstant.API_LOGIN_INIT -> {
                if (responseData is LoginInitResponse) {
                    // set the button text to continue
                    when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                         Utility.showToast("OTP sent")
                            onOtpSentSuccess.value=true
                        }
                    }
                }
            }



        }

    }

    override fun onError(purpose: String,errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose,errorResponseInfo)
    }


}