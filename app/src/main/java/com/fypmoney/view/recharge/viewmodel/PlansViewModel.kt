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
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.ProfileImageUploadResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KYC_TYPE
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargePlansRequest
import com.fypmoney.view.recharge.model.RechargePlansResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MultipartBody

/*
* This is used to handle user profile
* */
class PlansViewModel(application: Application) : BaseViewModel(application) {
    init {

    }

    var selectedOperator = MutableLiveData<OperatorResponse>()
    var selectedCircle = MutableLiveData<String>()
    var opertaorList: MutableLiveData<ArrayList<RechargePlansResponse>> = MutableLiveData()

    /*

 *This method is used to call profile pic upload api
 * */
    fun callGetOperatorList() {


        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_RECHARGE_PLANS,
                endpoint = NetworkUtil.endURL(ApiConstant.API_RECHARGE_PLANS),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = RechargePlansRequest(
                    operator = selectedOperator.value?.name,
                    circle = selectedCircle.value
                )
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_RECHARGE_PLANS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<RechargePlansResponse>>(
                    json.get("data").toString(),
                    Array<RechargePlansResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                opertaorList.postValue(arrayList)
            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {


        }

    }

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


}