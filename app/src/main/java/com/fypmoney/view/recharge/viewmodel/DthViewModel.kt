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
import com.fypmoney.view.recharge.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class DthViewModel(application: Application) : BaseViewModel(application) {
    init {

    }

    var opertaorList: MutableLiveData<FetchbillResponse> = MutableLiveData()
    var paymentResponse: MutableLiveData<BillPaymentResponse> = MutableLiveData()


    var operatorResponse = ObservableField<OperatorResponse>()
    var Selectedoperator = MutableLiveData<String>()


    /*

 *This method is used to call profile pic upload api
 * */
    fun callGetDthInfo(toString: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_DTH_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_DTH_INFO),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = FetchbillRequest(
                    canumber = toString.toLong(),
                    operator = Selectedoperator.value,
                    mode = "online"
                )
            )
        )
    }

    fun callMobileRecharge(
        operator: String?,
        value1: FetchbillResponse?,
        dth: String,
        amount: String
    ) {
        value1?.bill_fetch?.billAmount?.let {
            WebApiCaller.getInstance().request(
                ApiRequest(
                    purpose = ApiConstant.API_PAY_BILL,
                    endpoint = NetworkUtil.endURL(ApiConstant.API_PAY_BILL),
                    request_type = ApiUrl.POST,
                    onResponse = this, isProgressBar = true,
                    param =
                    BillPaymentRequest(
                        cardNo = dth,
                        operator = operator?.toInt(),
                        amount = amount.toDouble(),
                        planPrice = it.toDouble(),
                        planType = "",
                        billAmount = it.toDouble(),
                        billnetamount = value1.bill_fetch.billnetamount?.toDoubleOrNull(),
                        mode = "online",
                        dueDate = value1.bill_fetch.dueDate,
                        acceptPartPay = false,
                        acceptPayment = true,
                        cellNumber = dth,
                        userName = "Raghu",
                        latitude = "27.2232",
                        longitude = "27.2232"


                    )

                )
            )
        }
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_DTH_INFO -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FetchbillResponse>(
                    json.get("data").toString(),
                    FetchbillResponse::class.java
                )

                opertaorList.postValue(array)
            }

            ApiConstant.API_PAY_BILL -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<BillPaymentResponse>(
                    json.get("data").toString(),
                    BillPaymentResponse::class.java
                )

                paymentResponse.postValue(array)
            }
        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {


        }

    }


}