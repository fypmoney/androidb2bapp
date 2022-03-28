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
import com.fypmoney.model.*
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
class PayAndRechargeViewModel(application: Application) : BaseViewModel(application) {
    init {

    }

    var opertaorList: MutableLiveData<ValueItem> = MutableLiveData()
    var mobile: MutableLiveData<String> = MutableLiveData()
    var planType: MutableLiveData<String> = MutableLiveData()
    var operatorResponse = MutableLiveData<OperatorResponse>()

    var success = MutableLiveData<PayAndRechargeResponse>()


    /*

 *This method is used to call profile pic upload api
 * */
    fun callMobileRecharge(
        value: ValueItem?,
        value1: String?,
        value2: OperatorResponse?,
        value3: String?
    ) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_MOBILE_RECHARGE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_MOBILE_RECHARGE),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = PayAndRechargeRequest(
                    cardNo = value1,
                    operator = "11",
                    planPrice = Utility.convertToPaise(value?.rs)?.toLong(),
                    planType = value3,
                    amount = Utility.convertToPaise(value?.rs)?.toLong()


                )
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_MOBILE_RECHARGE -> {

                Utility.showToast("Success")
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<PayAndRechargeResponse>(
                    json.get("data").toString(),
                    PayAndRechargeResponse::class.java
                )

                success.postValue(array)


            }
        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {


        }

    }


}