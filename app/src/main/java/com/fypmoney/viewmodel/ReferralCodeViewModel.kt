package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.R
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
import com.fypmoney.model.ReferralCodeResponse
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This class is used to handle referral code
* */
class ReferralCodeViewModel(application: Application) : BaseViewModel(application) {
    var onContinueClicked = MutableLiveData(false)
    var onSkipClicked = MutableLiveData(false)
    var onReferralCodeSuccess = MutableLiveData(false)

    var onCustomerApiSuccess = MutableLiveData(false)
    var referralCode = MutableLiveData<String>()

    init {
        SharedPrefUtils.getString(application.applicationContext,
            SharedPrefUtils.SF_REFERRAL_CODE_FROM_INVITE_LINK
        )?.let {
            trackr { it1->
                it1.name = TrackrEvent.ref_from_invite_link
                it1.add(TrackrField.referral_code,it)
            }
            referralCode.value = it
        }
    }
    /*
      * This method is used to handle on click of skip
      * */
    fun onSkipClicked() {
        SharedPrefUtils.putString(PockketApplication.instance,
            SharedPrefUtils.SF_REFERRAL_CODE_FROM_INVITE_LINK,null)
        onSkipClicked.value = true
    }

    /*
    * This method is used to call verify referral code
    * */
    fun callGetCustomerProfileApi() {
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

    fun callVerifyReferralCode() {
        when {
            TextUtils.isEmpty(referralCode.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.referral_code_empty_error))
            }
            else -> {
                WebApiCaller.getInstance().request(
                    ApiRequest(
                        ApiConstant.API_VERIFY_REFERRAL_CODE,
                        NetworkUtil.endURL(ApiConstant.API_VERIFY_REFERRAL_CODE + referralCode.value?.trim()),
                        ApiUrl.POST,
                        BaseRequest(),
                        this, isProgressBar = true
                    )
                )
            }
        }

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_VERIFY_REFERRAL_CODE -> {
                if (responseData is ReferralCodeResponse) {
                    // set the button text to continue
                    when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                            trackr {
                                it.name = TrackrEvent.ref_applied_success
                            }
                            onReferralCodeSuccess.value = true

                        }
                    }
                }
            }
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {

                    var data = responseData.customerInfoResponseDetails
                    data?.isReferralAllowed = "NO"
                    Utility.saveCustomerDataInPreference(data)
                    SharedPrefUtils.putString(PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.bankProfile?.kycType)
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
                        SharedPrefUtils.SF_KEY_USER_DOB,
                        responseData.customerInfoResponseDetails?.userProfile?.dob
                    )

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_EMAIL,
                        responseData.customerInfoResponseDetails?.email
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
                    onCustomerApiSuccess.value = true


                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}


