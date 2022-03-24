package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
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
import com.fypmoney.model.ProfileImageUploadResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KYC_TYPE
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MultipartBody

/*
* This is used to handle user profile
* */
class MobileRechargeViewModel(application: Application) : BaseViewModel(application) {
    var opertaorList = MutableLiveData<MobileValidationResponse>()


    fun callGetMobileHrl(mobile: String) {


        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_HLR_CHECK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_HLR_CHECK),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = MobileHRLRequest(mobile = mobile, type = "mobile")
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_HLR_CHECK -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<MobileValidationResponse>(
                    json.get("data").toString(),
                    MobileValidationResponse::class.java
                )

                opertaorList.postValue(array)

            }

        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_LOGOUT -> {

            }


        }

    }


}