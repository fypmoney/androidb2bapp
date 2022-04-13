package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargeTypeModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class SelectOperatorViewModel(application: Application) : BaseViewModel(application) {
    init {
//        callGetOperatorList(AppConstants.POSTPAID)
    }

    var opertaorList: MutableLiveData<ArrayList<OperatorResponse>> = MutableLiveData()
    var operatorResponse = ObservableField<OperatorResponse>()

    var circleGot = MutableLiveData<String>()
    var mobileNumber = MutableLiveData<String>()
    var OperatorGot = MutableLiveData<String>()
    var rechargeType = MutableLiveData<String>()

    /*

 *This method is used to call profile pic upload api
 * */
    fun callGetOperatorList(postpaid: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_OPERATOR_LIST_MOBILE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_OPERATOR_LIST_MOBILE),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = RechargeTypeModel(type = postpaid)
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_OPERATOR_LIST_MOBILE -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<OperatorResponse>>(
                    json.get("data").toString(),
                    Array<OperatorResponse>::class.java
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