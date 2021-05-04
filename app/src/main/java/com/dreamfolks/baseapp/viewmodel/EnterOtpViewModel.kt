package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.BuildConfig
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.InterestRepository
import com.dreamfolks.baseapp.model.*
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.util.Utility
import java.util.*
import kotlin.collections.ArrayList

/*
* This is used to handle OTP
* */
class EnterOtpViewModel(application: Application) : BaseViewModel(application) {
    var mobile = MutableLiveData<String>()
    var mobileWithoutCountryCode = MutableLiveData<String>()
    var onChangeClicked = MutableLiveData<Boolean>()
    var cancelTimer = MutableLiveData<Boolean>()
    var getCustomerInfoSuccess = MutableLiveData<CustomerInfoResponse>()
    var interestRepository = InterestRepository(mDB = appDatabase)
    var otp = ObservableField<String>()
    var resendOtpSuccess = MutableLiveData(true)
    var resendOtpVisibility = ObservableField(false)
    var isResendEnabled = ObservableField(true)

    /*
    * This method is used to handle click of mobile
    * */
    fun onChangeClicked() {
        onChangeClicked.value = true
    }

    init {
        callGetInterestApi()
    }

    /*
      *This method is used to call get interest API
      * */
    private fun callGetInterestApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_INTEREST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_INTEREST),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }

    /*
       * This method is used to handle click of submit
       * */
    fun onSubmitClicked() {
        when {
            otp.get().isNullOrEmpty() -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.empty_otp_error))

            }
            else -> {
                cancelTimer.value=true
                callLoginApi()
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

    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_LOGIN_INIT -> {
                if (responseData is LoginInitResponse) {
                    resendOtpSuccess.value=true
                    // set the button text to continue
                    when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                           otp.set("")
                            Utility.showToast("OTP sent")
                        }
                    }
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
                    callGetCustomerProfileApi()

                }
            }

            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    getCustomerInfoSuccess.value = responseData
                    // Save the user id in shared preference
                    SharedPrefUtils.putLong(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID,
                        value = responseData.id!!
                    )
                    // Save the user phone in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE,
                        value = responseData.mobile
                    )
                    val interestList=ArrayList<String>()
                    if(responseData.userInterests?.isNullOrEmpty() == false) {
                        responseData.userInterests.forEach {
                            interestList.add(it.name!!)
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST,interestList

                        )

                    }




                }
            }
            ApiConstant.API_GET_INTEREST -> {
                if (responseData is InterestResponse) {
                    if (!responseData.interestDetails.isNullOrEmpty())
                        interestRepository.deleteAllInterest()
                    interestRepository.insertAllInterest(responseData.interestDetails!!)
                }
            }


        }

    }

    override fun onError(purpose: String,errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose,errorResponseInfo)
    }

    /*
    *This method is used to call get customer profile API
    * */
    private fun callGetCustomerProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CUSTOMER_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CUSTOMER_INFO),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }


}