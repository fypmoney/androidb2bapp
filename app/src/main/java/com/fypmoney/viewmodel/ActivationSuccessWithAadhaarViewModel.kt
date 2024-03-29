package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.*
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.moengage.core.internal.MoEConstants

/*
* This class is is used to display message in case aadhaar verification success
* */
class ActivationSuccessWithAadhaarViewModel(application: Application) : BaseViewModel(application) {
    private val TAG: String = ActivationSuccessWithAadhaarViewModel::class.java.simpleName
    var onContinueClicked = MutableLiveData<Boolean>()

    var GiftsSuccess = MutableLiveData<Boolean>()
    var postKycScreenCode = MutableLiveData<String>()

    /*
    * This method is used to handle continue
    * */
    init {

        callGetCustomerProfileApi()
        callUserGiftsApi()
    }

    fun onContinueClicked() {
        if (Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.YES)
            onContinueClicked.value = true
    }

    fun callUserGiftsApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.Api_Your_Gifts,
                NetworkUtil.endURL(ApiConstant.Api_Your_Gifts),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
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
            ApiConstant.Api_Your_Gifts -> {
                GiftsSuccess.value = true

            }
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)
                    SharedPrefUtils.putString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.bankProfile?.kycType)
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
                    if (postKycScreenCode.value.isNullOrEmpty()) {
                        responseData.customerInfoResponseDetails?.postKycScreenCode?.let {
                            postKycScreenCode.value = it
                        }


                    }

                    responseData.customerInfoResponseDetails?.postKycScreenCode?.let {
                        when (it) {
                            "0" -> {
                                trackr {
                                    it.services = arrayListOf(
                                        TrackrServices.FIREBASE,
                                        TrackrServices.MOENGAGE,
                                        TrackrServices.FB
                                    )
                                    it.name = TrackrEvent.kyc_verification_teen
                                }
                            }
                            "1"->{
                                trackr {
                                    it.services = arrayListOf(
                                        TrackrServices.FIREBASE,
                                        TrackrServices.MOENGAGE,
                                        TrackrServices.FB
                                    )
                                    it.name = TrackrEvent.kyc_verification_adult
                                }
                            }
                            "90"->{
                                trackr {
                                    it.services = arrayListOf(
                                        TrackrServices.FIREBASE,
                                        TrackrServices.MOENGAGE,
                                        TrackrServices.FB
                                    )
                                    it.name = TrackrEvent.kyc_verification_other
                                }

                            }
                            else -> {
                                Log.d(TAG , "No data found for KYC $it")
                            }
                        }
                    }

                    responseData.customerInfoResponseDetails?.postKycScreenCode?.let {
                        val map = hashMapOf<String, Any>()
                        map[CUSTOM_USER_POST_KYC_CODE] = it
                        UserTrackr.push(map)
                    }
                    responseData.customerInfoResponseDetails?.userProfile?.let { userProfile ->
                        val map1 = hashMapOf<String, Any>()
                        map1[MoEConstants.USER_ATTRIBUTE_USER_GENDER] =
                            userProfile.gender.toString()
                        UserTrackr.push(map1)
                        userProfile.dob?.let { it1 -> UserTrackr.setDateOfBirthDate(it1) }

                    }
//                    Utility.getCustomerDataFromPreference()?.postKycScreenCode="90"
//                    postKycScreenCode.value = "90"


                }


            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}