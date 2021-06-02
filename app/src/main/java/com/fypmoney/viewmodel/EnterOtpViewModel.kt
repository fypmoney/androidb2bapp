package com.fypmoney.viewmodel

import android.app.Application
import android.os.Build
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.multidex.BuildConfig
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import java.util.*

/*
* This is used to handle OTP
* */
class EnterOtpViewModel(application: Application) : BaseViewModel(application) {
    var mobile = MutableLiveData<String>()
    var heading =
        MutableLiveData(application.resources.getString(R.string.enter_code_title))
    var changeOrEditText =
        MutableLiveData(application.resources.getString(R.string.change_text))
    var mobileWithoutCountryCode = MutableLiveData<String>()
    var onChangeClicked = MutableLiveData<Boolean>()
    var onLoginSuccess = MutableLiveData<Boolean>()
    var cancelTimer = MutableLiveData<Boolean>()
    var otp = ObservableField<String>()
    var resendOtpSuccess = MutableLiveData(true)
    var resendOtpTimerVisibility = ObservableField(true)
    var isChangeVisible = ObservableField(true)
    var isResendEnabled = ObservableField(false)
    var fromWhichScreen = ObservableField<String>()
    var kycMobileToken = ObservableField<String>()
    var isYesBankLogoVisibile = ObservableField(false)
    var otpTitle =
        ObservableField<String>(PockketApplication.instance.getString(R.string.otp_title))

    /*
    * This method is used to handle click of mobile
    * */
    fun onChangeClicked() {
        onChangeClicked.value = true
    }

    /*
       * This method is used to handle click of submit
       * */
    fun onVerifyClicked() {
        when {
            otp.get().isNullOrEmpty() -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.empty_otp_error))

            }
            else -> {
                when (fromWhichScreen.get()) {
                    AppConstants.LOGIN_SCREEN -> {
                        cancelTimer.value = true
                        callLoginApi()
                    }
                    AppConstants.AADHAAR_VERIFICATION -> {
                    }
                    AppConstants.KYC_MOBILE_VERIFICATION -> {
                        callKycMobileVerificationApi()
                    }

                    else -> {
                        onLoginSuccess.value = true
                    }
                }
            }
        }
    }


    /*
      * This method is used to call login API
      * */
    private fun callLoginApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_LOGIN,
                NetworkUtil.endURL(ApiConstant.API_LOGIN),
                ApiUrl.POST,
                AppLoginApiRequest(
                    AppConstants.MOBILE_TYPE,
                    mobileWithoutCountryCode.value,
                    AppConstants.DEFAULT_COUNTRY_CODE,
                    AppConstants.REGISTRATION_MODE_APP,
                    otp.get(),
                    null,
                    null,
                    null,
                    UserDeviceInfo(
                        Build.BRAND,
                        Build.MODEL,
                        Build.ID,
                        TimeZone.getDefault().getDisplayName(
                            Locale.ROOT
                        ),
                        PockketApplication.instance.resources.configuration.locale.country,
                        BuildConfig.VERSION_NAME,
                        AppConstants.PLATFORM,
                        Build.VERSION.SDK_INT.toString(),
                        SharedPrefUtils.getString(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_FIREBASE_TOKEN
                        ) ?: ""
                    )

                ), this, isProgressBar = true
            )
        )


    }

    /*
* This method is used to call send otp API
* */
    fun callSendOtpApi() {
        when (fromWhichScreen.get()) {
            AppConstants.LOGIN_SCREEN -> {
                if (!resendOtpTimerVisibility.get()!!) {
                    WebApiCaller.getInstance().request(
                        ApiRequest(
                            ApiConstant.API_LOGIN_INIT,
                            NetworkUtil.endURL(ApiConstant.API_LOGIN_INIT),
                            ApiUrl.POST,
                            LoginInitRequest(
                                identifierType = AppConstants.MOBILE_TYPE,
                                identifier = mobileWithoutCountryCode.value!!
                            ),
                            this, isProgressBar = true
                        )
                    )
                } else {
                    Utility.showToast("")
                }

            }
            AppConstants.KYC_MOBILE_VERIFICATION -> {
                if (!resendOtpTimerVisibility.get()!!) {
                    callKycAccountActivationApi()
                }
            }
        }
    }

    /*
       * This method is used to call auth login API
       * */
    private fun callKycMobileVerificationApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_MOBILE_VERIFICATION,
                NetworkUtil.endURL(ApiConstant.API_KYC_MOBILE_VERIFICATION),
                ApiUrl.PUT,
                KycModel.KycMobileVerifyRequest(
                    action = AppConstants.KYC_ACTION_MOBILE_AUTH,
                    otp = otp.get()!!,
                    token = kycMobileToken.get()!!
                ),
                this, isProgressBar = true
            )
        )
    }

    /*
  * This method is used to call auth login API
  * */
    fun callKycAccountActivationApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_ACTIVATE_ACCOUNT,
                NetworkUtil.endURL(ApiConstant.API_KYC_ACTIVATE_ACCOUNT),
                ApiUrl.PUT,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_LOGIN_INIT -> {
                if (responseData is LoginInitResponse) {
                    resendOtpSuccess.value = true
                    resendOtpTimerVisibility.set(true)
                    isResendEnabled.set(false)
                    // set the button text to continue
                    when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                            otp.set("")
                            Utility.showToast("OTP sent successfully")
                        }
                    }
                }
            }

            ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                if (responseData is KycModel.KycActivateAccountResponse) {
                    resendOtpSuccess.value = true
                    resendOtpTimerVisibility.set(true)
                    isResendEnabled.set(false)
                    kycMobileToken.set(responseData.kycActivateAccountResponseDetails.token)


                }
            }
            ApiConstant.API_LOGIN -> {
                if (responseData is LoginResponse) {
                    SharedPrefUtils.putBoolean(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_IS_LOGIN,
                        true
                    )
                    // Save the access token in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_ACCESS_TOKEN,
                        value = responseData.loginResponseDetails?.access_token!!
                    )
                    onLoginSuccess.value = true

                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    /*
    * Used to set initial data
    * */
    fun setInitialData(
        type: String? = null,
        fromWhichScreenValue: String? = null,
        token: String? = null
    ) {
        mobile.value = type
        fromWhichScreen.set(fromWhichScreenValue)
        kycMobileToken.set(token)

        when (fromWhichScreen.get()) {
            AppConstants.AADHAAR_VERIFICATION -> {
                heading.value =
                    PockketApplication.instance.resources.getString(R.string.aadhaar_otp_screen_title)
                mobile.value =
                    PockketApplication.instance.resources.getString(R.string.aadhaar_otp_sub_title) + SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_AADHAAR_NUMBER
                    ) + PockketApplication.instance.resources.getString(R.string.aadhaar_otp_edit)
                isChangeVisible.set(false)

                otpTitle.set("")

            }

            AppConstants.KYC_MOBILE_VERIFICATION -> {
                heading.value = ""
                mobile.value =
                    PockketApplication.instance.resources.getString(R.string.aadhaar_mobile_otp_sub_title)
                isChangeVisible.set(false)

                otpTitle.set("")
                isYesBankLogoVisibile.set(true)

            }
        }


    }
}