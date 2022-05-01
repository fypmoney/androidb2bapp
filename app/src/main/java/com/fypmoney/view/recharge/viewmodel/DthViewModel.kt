package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.StoreDataModel
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.recharge.model.*
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class DthViewModel(application: Application) : BaseViewModel(application) {
    init {
        callExplporeContent()
    }

    var dthinfoList: MutableLiveData<FetchbillResponse> = MutableLiveData()
    var paymentResponse: MutableLiveData<PayAndRechargeResponse> = MutableLiveData()

    var failedresponse: MutableLiveData<PayAndRechargeResponse> = MutableLiveData()
    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

//    var operatorResponse = ObservableField<OperatorResponse>()

    var selectedOfflineOperator = MutableLiveData<StoreDataModel>()
    fun callExplporeContent() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "DTH",
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    fun callFetchFeedsApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_FEED_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_FEED_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    fun callFetchOfferApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_OFFER_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_OFFER_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    /*

 *This method is used to call profile pic upload api
 * */
    fun callGetDthInfo(toString: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_FETCH_BILL,
                endpoint = NetworkUtil.endURL(ApiConstant.API_FETCH_BILL),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = FetchbillRequest(
                    canumber = toString,
                    operator = selectedOfflineOperator.value?.operator_id,
                    mode = "online"
                )
            )
        )
    }

//    fun callMobileRecharge(
//        operator: String?,
//        value1: FetchbillResponse?,
//        dth: String,
//        amount: String
//    ) {
//        value1?.bill_fetch?.billAmount?.let {
//            WebApiCaller.getInstance().request(
//                ApiRequest(
//                    purpose = ApiConstant.API_PAY_BILL,
//                    endpoint = NetworkUtil.endURL(ApiConstant.API_PAY_BILL),
//                    request_type = ApiUrl.POST,
//                    onResponse = this, isProgressBar = true,
//                    param =
//                    BillPaymentRequest(
//                        cardNo = dth,
//                        operator = operator?.toInt(),
//                        amount = amount.toDouble(),
//                        planPrice = it.toDouble(),
//                        planType = "",
//                        billAmount = it.toDouble(),
//                        billnetamount = value1.bill_fetch.billnetamount?.toDoubleOrNull(),
//                        mode = "online",
//                        dueDate = value1.bill_fetch.dueDate,
//                        acceptPartPay = false,
//                        acceptPayment = true,
//                        cellNumber = dth,
//                        userName = "Raghu",
//                        latitude = "27.2232",
//                        longitude = "27.2232"
//
//
//                    )
//
//                )
//            )
//        }
//    }

    fun callMobileRecharge(
        selectedpaln: String?,
        number: String?,
        value3: String?
    ) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_MOBILE_RECHARGE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_MOBILE_RECHARGE),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = PayAndRechargeRequest(
                    cardNo = number,
                    operator = selectedOfflineOperator.value?.operator_id,
                    planPrice = Utility.convertToPaise(selectedpaln)?.toLong(),
                    planType = "",
                    amount = Utility.convertToPaise(selectedpaln)?.toLong()
                )
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)
            }

            ApiConstant.API_FETCH_FEED_DETAILS -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FeedDetails>(
                    json.get("data").toString(),
                    FeedDetails::class.java
                )

                feedDetail.postValue(array)


            }
            ApiConstant.API_FETCH_OFFER_DETAILS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<offerDetailResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                openBottomSheet.postValue(arrayList)


            }

            ApiConstant.API_FETCH_BILL -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FetchbillResponse>(
                    json.get("data").toString(),
                    FetchbillResponse::class.java
                )

                dthinfoList.postValue(array)
            }
            ApiConstant.API_MOBILE_RECHARGE -> {

                Utility.showToast("Success")
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<PayAndRechargeResponse>(
                    json.get("data").toString(),
                    PayAndRechargeResponse::class.java
                )

                paymentResponse.postValue(array)


            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_MOBILE_RECHARGE -> {
                failedresponse.postValue(null)
            }
        }

    }


}