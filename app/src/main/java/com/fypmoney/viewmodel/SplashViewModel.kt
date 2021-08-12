package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.BuildConfig
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.checkappupdate.CheckAppUpdateResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility


/*
* This class is launcher screen
* */
class SplashViewModel(application: Application) : BaseViewModel(application) {
    var getCustomerInfoSuccess = MutableLiveData<CustomerInfoResponse>()
    var moveToNextScreen = MutableLiveData(false)
    var splashTime = ObservableField(AppConstants.SPLASH_TIME)

    val appUpdateState:LiveData<AppUpdateState>
        get() = _appUpdateState
    private val _appUpdateState = MutableLiveData<AppUpdateState>()

    init {
        callCheckAppUpdate()
        if (SharedPrefUtils.getBoolean(
                application,
                SharedPrefUtils.SF_KEY_IS_LOGIN
            )!!
        ) {
            // api call in case preference not contains the data
            if (SharedPrefUtils.getString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_USER_PROFILE_INFO
                ) == null
            ) {
                callGetCustomerProfileApi()
            } else {
                moveToNextScreen.value = true
            }
        } else {
            moveToNextScreen.value = true
        }
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
    /*
    *This method is used to call get customer profile API
    * */
    private fun callCheckAppUpdate() {

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.CHECK_APP_UPDATE,
                endpoint = NetworkUtil.endURL(ApiConstant.CHECK_APP_UPDATE+"/${BuildConfig.VERSION_CODE}/"),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)
                    moveToNextScreen.value = true
                    getCustomerInfoSuccess.value = responseData
                    // Save the user id in shared preference
                    SharedPrefUtils.putLong(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID,
                        value = responseData.customerInfoResponseDetails?.id!!
                    )
                    // Save the user phone in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE,
                        value = responseData.customerInfoResponseDetails?.mobile
                    )

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.customerInfoResponseDetails?.profilePicResourceId
                    )
                    val interestList = ArrayList<String>()
                    if (responseData.customerInfoResponseDetails?.userInterests?.isNullOrEmpty() == false) {
                        responseData.customerInfoResponseDetails?.userInterests!!.forEach {
                            interestList.add(it.name!!)
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    }


                }
            }
            ApiConstant.CHECK_APP_UPDATE->{
                    if(responseData is CheckAppUpdateResponse){
                        when(responseData.appUpdateData?.updateFlag){
                            "FLEXIBLE"->{
                                _appUpdateState.value = AppUpdateState.FLEXIBLE
                            }
                            "FORCE_UPDATE"->{
                            _appUpdateState.value = AppUpdateState.FORCEUPDATE

                             }
                            "NOT_UPDATE"->{
                                _appUpdateState.value = AppUpdateState.NOTUPDATE

                            }"NOT_ALLOWED"->{
                            _appUpdateState.value = AppUpdateState.NOTALLOWED

                        }
                        }
                    }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


    sealed class AppUpdateState{
        object FLEXIBLE:AppUpdateState()
        object FORCEUPDATE:AppUpdateState()
        object NOTUPDATE:AppUpdateState()
        object NOTALLOWED:AppUpdateState()
    }
}