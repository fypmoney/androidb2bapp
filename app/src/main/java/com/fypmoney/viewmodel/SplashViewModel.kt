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
import com.fypmoney.model.SettingsRequest
import com.fypmoney.model.SettingsResponse
import com.fypmoney.model.checkappupdate.CheckAppUpdateResponse
import com.fypmoney.model.homemodel.TopTenUsersResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.CARD_ORDER_FLAG
import com.fypmoney.util.AppConstants.REFEREE_CASHBACK
import com.fypmoney.util.AppConstants.REFER_LINE1
import com.fypmoney.util.AppConstants.REFER_LINE2
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KEY_REFEREE_CASHBACK
import com.fypmoney.util.Utility
import com.google.gson.Gson


/*
* This class is launcher screen
* */
class SplashViewModel(val  app: Application) : BaseViewModel(app) {
    var getCustomerInfoSuccess = MutableLiveData<CustomerInfoResponse>()
    var moveToNextScreen = MutableLiveData(false)

    val appUpdateState:LiveData<AppUpdateState>
        get() = _appUpdateState
    private val _appUpdateState = MutableLiveData<AppUpdateState>()

    init {
        setUpApp()
    }

     fun setUpApp() {
        callCheckAppUpdate()
        if (SharedPrefUtils.getBoolean(
                app,
                SharedPrefUtils.SF_KEY_IS_LOGIN
            )!!
        ) {
            callSettingsApi()
            if(Utility.getCustomerDataFromPreference()==null){
                callGetCustomerProfileApi()
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
    private fun callSettingsApi() {
        val request = SettingsRequest()
        request.keyList = listOf("CARD_ORDER_FLAG","REFER_LINE1", "REFER_LINE2", "REFEREE_CASHBACK")
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_SETTINGS,
                endpoint = NetworkUtil.endURL(ApiConstant.API_SETTINGS),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = request
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
                val data = Gson().fromJson(responseData.toString(), CheckAppUpdateResponse::class.java)
                if(data is CheckAppUpdateResponse){
                        when(data.appUpdateData?.updateFlag){
                            "FLEXIBLE"->{
                                _appUpdateState.value = AppUpdateState.FLEXIBLE
                            }
                            "FORCE_UPDATE"->{
                            _appUpdateState.value = AppUpdateState.FORCEUPDATE

                            }
                            "NOT_UPDATE" -> {
                                _appUpdateState.value = AppUpdateState.NOTUPDATE

                            }
                            "NOT_ALLOWED" -> {
                                _appUpdateState.value = AppUpdateState.NOTALLOWED

                            }
                        }
                }

                moveToNextScreen.value = true
            }
            ApiConstant.API_SETTINGS -> {
                val data = Gson().fromJson(responseData.toString(), SettingsResponse::class.java)
                if (data is SettingsResponse) {
                    data.data.keysFound.forEach {
                        when (it.key) {
                            CARD_ORDER_FLAG -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_CARD_FLAG,
                                    it.value
                                )
                            }
                            REFER_LINE1 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_REFER_LINE1,
                                    it.value
                                )
                            }
                            REFER_LINE2 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_REFER_LINE2,
                                    it.value
                                )
                            }
                            REFEREE_CASHBACK -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_REFEREE_CASHBACK,
                                    it.value
                                )
                            }
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