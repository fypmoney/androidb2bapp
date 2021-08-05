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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This class is used to show login success
* */
class LoginSuccessViewModel(application: Application) : BaseViewModel(application) {
    var onApiSuccess = MutableLiveData<Boolean>()

    init {
    }

    /*
    * This method is used to handle click of continue
    * */
    fun onContinueClicked() {
        callGetCustomerProfileApi()
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


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

}