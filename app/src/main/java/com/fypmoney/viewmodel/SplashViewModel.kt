package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.adjust.sdk.Adjust
import com.fyp.trackr.base.Trackr
import com.fyp.trackr.models.*
import com.fypmoney.BuildConfig
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
import com.fypmoney.model.SettingsRequest
import com.fypmoney.model.SettingsResponse
import com.fypmoney.model.checkappupdate.CheckAppUpdateResponse
import com.fypmoney.util.AppConstants.CARD_ORDER_FLAG
import com.fypmoney.util.AppConstants.ERROR_MESSAGE_HOME
import com.fypmoney.util.AppConstants.IS_NEW_FEED_AVAILABLE
import com.fypmoney.util.AppConstants.ONBOARD_SHARE_1
import com.fypmoney.util.AppConstants.ONBOARD_SHARE_90
import com.fypmoney.util.AppConstants.REFEREE_CASHBACK
import com.fypmoney.util.AppConstants.REFER_LINE1
import com.fypmoney.util.AppConstants.REFER_LINE2
import com.fypmoney.util.AppConstants.REFER_MSG_SHARED_1
import com.fypmoney.util.AppConstants.REFER_MSG_SHARED_2
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KEY_APP_VERSION_CODE
import com.fypmoney.util.Utility
import com.fypmoney.view.referandearn.model.ReferMessageResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.moengage.core.internal.MoEConstants
import com.moengage.firebase.MoEFireBaseHelper


/*
* This class is launcher screen
* */
class  SplashViewModel(val  app: Application) : BaseViewModel(app) {

    var moveToNextScreen = MutableLiveData(false)
    var callCustomer = MutableLiveData(false);

    val appUpdateState: LiveData<AppUpdateState>
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
            callReferScreenMessages()
            SharedPrefUtils.getInt(app,SF_KEY_APP_VERSION_CODE)?.let {
                if(Utility.getCustomerDataFromPreference()==null ||
                    (it < BuildConfig.VERSION_CODE)||(it > BuildConfig.VERSION_CODE)){
                    SharedPrefUtils.putInt(app,SF_KEY_APP_VERSION_CODE, BuildConfig.VERSION_CODE)
                    SharedPrefUtils.putBoolean(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_IS_INSTALLED_APPS_SYNCED,
                        false
                    )
                    callGetCustomerProfileApi()
                }
                if(it==0){
                    Trackr.appIsInstallFirst(isFirstTime = true)
                    SharedPrefUtils.getString(PockketApplication.instance,SharedPrefUtils.SF_KEY_FIREBASE_TOKEN)
                        ?.let { it2 ->
                            MoEFireBaseHelper.getInstance().passPushToken(PockketApplication.instance,
                                it2
                            )
                            Adjust.setPushToken(it2, PockketApplication.instance);
                        }
                }else{
                    Trackr.appIsInstallFirst(isFirstTime = false)
                    SharedPrefUtils.getString(PockketApplication.instance,SharedPrefUtils.SF_KEY_FIREBASE_TOKEN)
                        ?.let { it2 ->
                            MoEFireBaseHelper.getInstance().passPushToken(PockketApplication.instance,
                                it2
                            )
                            Adjust.setPushToken(it2, PockketApplication.instance);
                        }
                    Utility.getCustomerDataFromPreference()?.let {
                        val map = hashMapOf<String,Any>()
                        map[MoEConstants.USER_ATTRIBUTE_UNIQUE_ID] = it.mobile.toString()
                        map[MoEConstants.USER_ATTRIBUTE_USER_MOBILE] = it.mobile.toString()
                        map[MoEConstants.USER_ATTRIBUTE_USER_FIRST_NAME] = it.firstName.toString()
                        map[MoEConstants.USER_ATTRIBUTE_USER_LAST_NAME] = it.lastName.toString()
                        map[MoEConstants.USER_ATTRIBUTE_USER_EMAIL] = it.email.toString()

                        map[MoEConstants.USER_ATTRIBUTE_USER_GENDER] = it.userProfile?.gender.toString()
                        map[CUSTOM_USER_POST_KYC_CODE] = it.postKycScreenCode.toString()
                        it.userProfile?.dob?.let {dob->
                            UserTrackr.setDateOfBirthDate(dob)
                        }
                        UserTrackr.push(map)
                        UserTrackr.login( it.mobile.toString())
                    }
                }
            }

        }
    }


    /*
    *This method is used to call get customer profile API
    * */
    fun callGetCustomerProfileApi() {
        callCustomer.value = true
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
            "ADD_MONEY_VIDEO"
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
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)

                    SharedPrefUtils.putString(PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.kycType)
                    // Save the user id in shared preference
                    responseData.customerInfoResponseDetails?.id?.let {
                        SharedPrefUtils.putLong(
                            getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID,
                            value = it
                        )
                    }
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
                    SharedPrefUtils.putString(PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.kycType)
                    val interestList = ArrayList<String>()
                    if (responseData.customerInfoResponseDetails?.userInterests?.isNullOrEmpty() == false) {
                        responseData.customerInfoResponseDetails?.userInterests?.forEach {
                            it.name?.let { it1 -> interestList.add(it1) }
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    }

                    val map = hashMapOf<String,Any>()
                    map[MoEConstants.USER_ATTRIBUTE_UNIQUE_ID] = responseData.customerInfoResponseDetails!!.mobile.toString()
                    map[MoEConstants.USER_ATTRIBUTE_USER_MOBILE] = responseData.customerInfoResponseDetails!!.mobile.toString()
                    map[MoEConstants.USER_ATTRIBUTE_USER_FIRST_NAME] = responseData.customerInfoResponseDetails!!.firstName.toString()
                    map[MoEConstants.USER_ATTRIBUTE_USER_LAST_NAME] = responseData.customerInfoResponseDetails!!.lastName.toString()
                    map[MoEConstants.USER_ATTRIBUTE_USER_EMAIL] = responseData.customerInfoResponseDetails!!.email.toString()
                    UserTrackr.push(map)
                    UserTrackr.login( responseData.customerInfoResponseDetails!!.mobile.toString())
                    responseData.customerInfoResponseDetails?.dob?.let {
                        UserTrackr.setDateOfBirthDate(
                            it
                        )
                    }

                    moveToNextScreen.value = true
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
                            REFER_MSG_SHARED_1 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_REFFERAL_MSG,
                                    it.value
                                )
                            }
                            ONBOARD_SHARE_90 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_REGISTER_MSG_90,
                                    it.value
                                )
                            }
                            ONBOARD_SHARE_1 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_REGISTER_MSG_1,
                                    it.value
                                )
                            }
                            REFER_MSG_SHARED_2 -> {
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_REFFERAL_MSG_2,
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
                            ERROR_MESSAGE_HOME -> {
                                PockketApplication.homeScreenErrorMsg = it.value
                            }
                            IS_NEW_FEED_AVAILABLE -> {
                                PockketApplication.isNewFeedAvailableData = it
                            }
                            IS_NEW_FEED_AVAILABLE -> {
                                PockketApplication.isNewFeedAvailableData = it
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

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        moveToNextScreen.value = true

    }


    sealed class AppUpdateState{
        object FLEXIBLE:AppUpdateState()
        object FORCEUPDATE:AppUpdateState()
        object NOTUPDATE:AppUpdateState()
        object NOTALLOWED:AppUpdateState()
    }
}