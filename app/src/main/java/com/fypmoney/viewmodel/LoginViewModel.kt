package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
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
import com.fypmoney.model.AuthLoginResponse
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.LoginInitRequest
import com.fypmoney.model.LoginInitResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

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