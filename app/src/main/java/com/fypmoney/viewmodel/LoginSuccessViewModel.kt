package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.*
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.SettingsRequest
import com.fypmoney.model.SettingsResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.referandearn.model.ReferMessageResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.moengage.core.internal.MoEConstants.*

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
        callSettingsApi()
        callReferScreenMessages()
    }

    private fun callSettingsApi() {
        val request = SettingsRequest()
        request.keyList = listOf(
            "CARD_ORDER_FLAG",
            "REFER_LINE1",
            "REFER_LINE2",
            "REFEREE_CASHBACK",
            "REFERAL_PKYC0",
            "REFERAL_PKYC1",
            "ERROR_MESSAGE_HOME",
            "IS_NEW_FEED_AVAILABLE",
            "ONBOARD_SHARE_90",
            "ONBOARD_SHARE_1",
            "ADD_MONEY_VIDEO",
            "SHOW_RECHARGE_SCREEN",
            "ADD_MONEY_VIDEO_NEW",
            "IS_GIFT_CARD_IS_AVAILABLE",
            "SERVER_IS_UNDER_MAINTENANCE",
            "SERVER_MAINTENANCE_DESCRIPTION",
            "CASHBACK_RECHARGE_ALLOWED",
            "MESSAGE_ON_RECHARGE"
        )
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

    private fun callReferScreenMessages() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.REFERRAL_SCREEN_MESSAGES_API,
                NetworkUtil.endURL(ApiConstant.REFERRAL_SCREEN_MESSAGES_API),
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
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)
//                    Utility.getCustomerDataFromPreference()?.postKycScreenCode = "0"

                    SharedPrefUtils.putString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,
                        responseData.customerInfoResponseDetails?.bankProfile?.kycType
                    )

                    trackr {
                        it.services = arrayListOf(TrackrServices.ADJUST, TrackrServices.FIREBASE)
                        it.name = TrackrEvent.LOGINSUCCESS
                    }

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

                    Utility.getCustomerDataFromPreference()?.let {
                        val map = hashMapOf<String, Any>()
                        map[USER_ATTRIBUTE_UNIQUE_ID] = it.mobile.toString()
                        map[USER_ATTRIBUTE_USER_MOBILE] = it.mobile.toString()
                        map[USER_ATTRIBUTE_USER_FIRST_NAME] = it.firstName.toString()
                        map[USER_ATTRIBUTE_USER_LAST_NAME] = it.lastName.toString()
                        map[USER_ATTRIBUTE_USER_GENDER] = it.userProfile?.gender.toString()
                        map[CUSTOM_USER_POST_KYC_CODE] = it.postKycScreenCode.toString()
                        map[USER_ATTRIBUTE_USER_EMAIL] = it.email.toString()

                        UserTrackr.push(map)
                        UserTrackr.login(it.mobile.toString())
                        it.userProfile?.dob?.let { it1 ->
                            UserTrackr.setDateOfBirthDate(
                                it1
                            )
                        }
                    }
                    val interestList = ArrayList<String>()
                    if (responseData.customerInfoResponseDetails?.userInterests?.isNullOrEmpty() == false) {
                        responseData.customerInfoResponseDetails?.userInterests!!.forEach {
                            interestList.add(it.name!!)
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    } else {
                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    }
                    onApiSuccess.value = true

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
                            AppConstants.ADD_MONEY_VIDEO -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_ADD_MONEY_VIDEO,
                                    it.value
                                )
                            }
                            AppConstants.ADD_MONEY_VIDEO_NEW -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_ADD_MONEY_VIDEO_NEW,
                                    it.value
                                )
                            }
                            AppConstants.ERROR_MESSAGE_HOME -> {
                                PockketApplication.homeScreenErrorMsg = it.value
                            }
                            AppConstants.IS_NEW_FEED_AVAILABLE -> {
                                PockketApplication.isNewFeedAvailableData = it
                            }
                            AppConstants.SHOW_RECHARGE_SCREEN -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_SHOW_RECHARGE_IN_HOME_SCREEN,
                                    it.value
                                )
                            }

                            AppConstants.IS_GIFT_CARD_IS_AVAILABLE ->{
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_SHOW_MY_ORDERS,
                                    it.value
                                )
                            }
                            AppConstants.SERVER_IS_UNDER_MAINTENANCE ->{
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_SERVER_IS_UNDER_MAINTENANCE,
                                    it.value
                                )
                            }
                            AppConstants.SERVER_MAINTENANCE_DESCRIPTION ->{
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_SERVER_MAINTENANCE_DESCRIPTION,
                                    it.value
                                )
                            }
                            "CASHBACK_RECHARGE_ALLOWED"->{
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_CASHBACK_RECHARGE_ALLOWED,
                                    it.value
                                )
                            }

                            "MESSAGE_ON_RECHARGE"->{
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_MESSAGE_ON_RECHARGE,
                                    it.value
                                )
                            }


                        }
                    }
                }


            }
            ApiConstant.REFERRAL_SCREEN_MESSAGES_API -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val data = Gson().fromJson(
                    json.get("data").toString(),
                    ReferMessageResponse::class.java
                )
                SharedPrefUtils.putString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_REFER_LINE2,
                    data.referLine2
                )
                SharedPrefUtils.putString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_REFERAL_GLOBAL_MSG,
                    data.referMessage
                )


            }
        }

    }

}