package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.google.gson.Gson

/*
* This class is used to show login success
* */
class LoginSuccessViewModel(application: Application) : BaseViewModel(application) {
    var onApiSuccess = MutableLiveData<Boolean>()

    /*
    * This method is used to handle click of continue
    * */
    fun onContinueClicked() {
        callGetCustomerProfileApi()
        callSettingsApi()
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

    /*
      *This method is used to call get customer profile API
      * */
    private fun callGetCustomerProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CUSTOMER_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CUSTOMER_INFO),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
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
                    onApiSuccess.value=true
                    // Save the user id in shared preference

                    // save first name, last name, date of birth

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_FIRST_NAME,
                        responseData.customerInfoResponseDetails?.firstName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.customerInfoResponseDetails?.profilePicResourceId
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_LAST_NAME,
                        responseData.customerInfoResponseDetails?.lastName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_DOB, responseData.customerInfoResponseDetails?.userProfile?.dob
                    )

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_EMAIL, responseData.customerInfoResponseDetails?.email
                    )

                    // again update the saved data in preference
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)

                    SharedPrefUtils.putLong(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID,
                        value = responseData.customerInfoResponseDetails?.id!!
                    )

                    // Save the user phone in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE,
                        value = responseData.customerInfoResponseDetails?.mobile
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

            ApiConstant.API_SETTINGS -> {
                val data = Gson().fromJson(responseData.toString(), SettingsResponse::class.java)
                if (data is SettingsResponse) {
                    data.data.keysFound.forEach {
                        when (it.key) {
                            AppConstants.CARD_ORDER_FLAG -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_CARD_FLAG,
                                    it.value
                                )
                            }
                            AppConstants.REFER_MSG_SHARED_1 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_REFFERAL_MSG,
                                    it.value
                                )
                            }

                            AppConstants.REFER_MSG_SHARED_2 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_REFFERAL_MSG_2,
                                    it.value
                                )
                            }
                            AppConstants.REFER_LINE1 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_REFER_LINE1,
                                    it.value
                                )
                            }

                            AppConstants.REFER_LINE2 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_REFER_LINE2,
                                    it.value
                                )
                            }
                            AppConstants.REFEREE_CASHBACK -> {
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

}