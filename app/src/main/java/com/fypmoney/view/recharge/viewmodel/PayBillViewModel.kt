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
class PayBillViewModel(application: Application) : BaseViewModel(application) {
    init {

    }

    var paymentResponse: MutableLiveData<BillPaymentResponse> = MutableLiveData()
    var failedRecharge: MutableLiveData<String> = MutableLiveData()

    fun callGetDthInfo(toString: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_DTH_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_DTH_INFO),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = FetchbillRequest(
                    canumber = toString.toLong(),
                    operator = operatorResponse.get()?.operatorId,
                    mode = "online"
                )
            )
        )
    }

    fun callMobileRecharge(
        operator: String?,

        dth: String,
        amount: String
    ) {

            WebApiCaller.getInstance().request(
                ApiRequest(
                    purpose = ApiConstant.API_PAY_BILL,
                    endpoint = NetworkUtil.endURL(ApiConstant.API_PAY_BILL),
                    request_type = ApiUrl.POST,
                    onResponse = this, isProgressBar = true,
                    param =
                    BillPaymentRequest(
                        cardNo = dth,
                        operator = operatorResponse.get()?.operatorId?.toInt(),
                        amount = amount.toDouble(),
                        planPrice = amount.toDouble(),
                        planType = "",
                        billAmount = amount.toDouble(),
                        billnetamount = fetchedBill.value!!.bill_fetch?.billnetamount?.toDoubleOrNull(),
                        mode = "online",
                        dueDate = fetchedBill.value!!.bill_fetch?.dueDate,
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

    var opertaorList: MutableLiveData<ArrayList<OperatorResponse>> = MutableLiveData()
    var fetchedBill: MutableLiveData<FetchbillResponse> = MutableLiveData()


    var operatorResponse = ObservableField<OperatorResponse>()

    var circleGot = MutableLiveData<String>()
    var mobileNumber = MutableLiveData<String>()

    /*

 *This method is used to call profile pic upload api
 * */


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

            ApiConstant.API_PAY_BILL -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<BillPaymentResponse>(
                    json.get("data").toString(),
                    BillPaymentResponse::class.java
                )

                paymentResponse.postValue(array)
            }
            ApiConstant.API_DTH_INFO -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FetchbillResponse>(
                    json.get("data").toString(),
                    FetchbillResponse::class.java
                )

                fetchedBill.postValue(array)
            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_PAY_BILL -> {
                failedRecharge.postValue(null)
            }


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