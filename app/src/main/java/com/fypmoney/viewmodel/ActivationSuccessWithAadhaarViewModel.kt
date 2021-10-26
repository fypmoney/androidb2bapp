package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.UserTrackr
import com.fyp.trackr.models.push
import com.fyp.trackr.models.trackr
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.moengage.core.internal.MoEConstants

/*
* This class is is used to display message in case aadhaar verification success
* */
class ActivationSuccessWithAadhaarViewModel(application: Application) : BaseViewModel(application) {
    var onContinueClicked = MutableLiveData<Boolean>()
    var postKycScreenCode = MutableLiveData<String>()

    /*
    * This method is used to handle continue
    * */
    init {
        trackr {
            it.name = TrackrEvent.Aadhar_verification
        }
        callGetCustomerProfileApi()
    }
    fun onContinueClicked() {
        if (Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.YES)
            onContinueClicked.value = true
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
                    postKycScreenCode.value =
                        responseData.customerInfoResponseDetails?.postKycScreenCode!!


                    responseData.customerInfoResponseDetails?.dob?.let {
                        val map = hashMapOf<String,Any>()
                        map[MoEConstants.USER_ATTRIBUTE_USER_BDAY] = it
                        UserTrackr.push(map)
                    }
                }



            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}