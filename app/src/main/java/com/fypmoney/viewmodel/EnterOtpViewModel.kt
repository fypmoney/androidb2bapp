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
    var onVerificationSuccess = MutableLiveData<Boolean>()
    var onVerificationFail = MutableLiveData<Boolean>()
    var otp = ObservableField<String>()
    var resendOtpSuccess = MutableLiveData(true)
    var resendOtpTimerVisibility = ObservableField(true)
    var isChangeVisible = ObservableField(true)
    var isResendEnabled = ObservableField(false)
    var fromWhichScreen = ObservableField<String>()
    private var kycToken = ObservableField<String>()
    var isYesBankLogoVisible = ObservableField(false)
    var otpTitle =
        ObservableField(PockketApplication.instance.getString(R.string.otp_title))

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
                        callKycVerificationApi()
                    }
                    AppConstants.KYC_MOBILE_VERIFICATION -> {
                        callKycMobileVerificationApi()
                    }

                }
            }
        }
    }

    /*
      * This method is used to call kyc init api
      * */
    private fun callKycInitApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_INIT,
                NetworkUtil.endURL(ApiConstant.API_KYC_INIT),
                ApiUrl.PUT,
                KycInitRequest(
                    kycMode = AppConstants.KYC_MODE,
                    kycType = AppConstants.KYC_TYPE,
                    documentIdentifier = SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_AADHAAR_NUMBER
                    )!!,
                    documentType = AppConstants.KYC_DOCUMENT_TYPE,
                    action = AppConstants.KYC_ACTION_ADHAR_AUTH
                ), this, isProgressBar = true
            )
        )
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

            AppConstants.AADHAAR_VERIFICATION -> {
                if (!resendOtpTimerVisibility.get()!!) {
                    callKycInitApi()
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
                KycMobileVerifyRequest(
                    action = AppConstants.KYC_ACTION_MOBILE_AUTH,
                    otp = otp.get()!!,
                    token = kycToken.get()!!
                ),
                this, isProgressBar = true
            )
        )
    }

    /*
      * This method is used to call aadhaar verification
      * */
    private fun callKycVerificationApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_VERIFICATION,
                NetworkUtil.endURL(ApiConstant.API_KYC_VERIFICATION),
                ApiUrl.PUT,
                KycVerificationRequest(
                    action = AppConstants.KYC_ACTION_ADHAR_AUTH,
                    otp = otp.get()!!,
                    token = kycToken.get()!!
                ),
                this, isProgressBar = true
            )
        )
    }

    /*
  * This method is used to call auth login API
  * */
    private fun callKycAccountActivationApi() {
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

            ApiConstant.API_KYC_INIT -> {
                if (responseData is KycInitResponse) {
                    resendOtpSuccess.value = true
                    resendOtpTimerVisibility.set(true)
                    isResendEnabled.set(false)
                    kycToken.set(responseData.kycInitResponseDetails.token)

                }
            }

            ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                if (responseData is KycActivateAccountResponse) {
                    resendOtpSuccess.value = true
                    resendOtpTimerVisibility.set(true)
                    isResendEnabled.set(false)
                    kycToken.set(responseData.kycActivateAccountResponseDetails.token)

                }
            }

            ApiConstant.API_KYC_MOBILE_VERIFICATION -> {
                if (responseData is KycMobileVerifyResponse) {
                    if (responseData.kycMobileVerifyResponseDetails.showAdharInitScreen == AppConstants.YES)
                        onVerificationSuccess.value = true


                }
            }

            ApiConstant.API_KYC_VERIFICATION -> {
                if (responseData is KycVerificationResponse) {
                    onVerificationSuccess.value = true


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

        when(purpose)
        {
            ApiConstant.API_KYC_MOBILE_VERIFICATION,ApiConstant.API_KYC_VERIFICATION->{
                onVerificationFail.value=true
            }
        }


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
        kycToken.set(token)

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
                isYesBankLogoVisible.set(true)

            }
        }


    }
}