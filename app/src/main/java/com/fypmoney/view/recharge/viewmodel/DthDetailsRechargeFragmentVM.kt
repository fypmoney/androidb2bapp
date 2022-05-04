package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiConstant.API_GET_WALLET_BALANCE
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.GetWalletBalanceResponse
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
class DthDetailsRechargeFragmentVM(application: Application) : BaseViewModel(application) {


    var dthinfoList: MutableLiveData<FetchbillResponse> = MutableLiveData()
    var paymentResponse: MutableLiveData<PayAndRechargeResponse> = MutableLiveData()

    var failedresponse: MutableLiveData<PayAndRechargeResponse> = MutableLiveData()

    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()


    var selectedDthOperator:StoreDataModel? = null

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()

    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    var amountSelected = ObservableField<String>("0") //prefilled amount

    var subscriberId = ObservableField<String>()

    var oper: OperatorResponse? = null

    val state: LiveData<DthDetailsState>
        get() = _state
    private val _state = MutableLiveData<DthDetailsState>()

    val event: LiveData<DthDetailsEvent>
        get() = _event
    private val _event = LiveEvent<DthDetailsEvent>()

    init {
        callExplporeContent()
    }


    fun onAmountSelected(amount: Int) {
        amountSelected.set(amount.toString())
    }

    fun onPayClick(){
        _event.value = DthDetailsEvent.OnPayClickEvent
    }
    fun callExplporeContent() {
        _state.value = DthDetailsState.Loading(ApiConstant.API_Explore)
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

    fun fetchBalance() {
        _state.value = DthDetailsState.Loading(API_GET_WALLET_BALANCE)
        WebApiCaller.getInstance().request(
            ApiRequest(
                API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
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
                _state.value = DthDetailsState.ExploreSuccess(arrayList)

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
            API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    responseData.getWalletBalanceResponseDetails.accountBalance.toIntOrNull()
                        ?.let { accountBalance ->
                            _state.value = DthDetailsState.BalanceSuccess(accountBalance)
                            if(accountBalance< Utility.convertToPaise(amountSelected.get()?.toDouble().toString())?.toLong()!!){
                                _event.value = DthDetailsEvent.ShowLowBalanceAlert(
                                    ((Utility.convertToPaise(
                                        amountSelected.get()?.toDouble().toString()
                                    )?.toLong()!!) - (accountBalance)).toString()
                                )
                            }else{
                                _event.value = DthDetailsEvent.ShowPaymentProcessingScreen(
                                    PayAndRechargeRequest(
                                        cardNo = subscriberId.get(),
                                        operator = oper?.operatorId,
                                        planPrice = Utility.convertToPaise(amountSelected.get())?.toLong(),
                                        planType =  "",
                                        amount = Utility.convertToPaise(amountSelected.get())?.toLong(),
                                        operatorName = oper?.name

                                    )
                                )
                            }
                        }

                }
            }



        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_MOBILE_RECHARGE -> {
                failedresponse.postValue(null)
            }

            ApiConstant.API_Explore -> {
                _state.value = DthDetailsState.Error(errorResponseInfo,ApiConstant.API_Explore)
            }
            ApiConstant.API_GET_WALLET_BALANCE -> {
                _state.value = DthDetailsState.Error(errorResponseInfo,ApiConstant.API_Explore)
            }
        }

    }

    sealed class DthDetailsState {
        data class Loading(val api:String) : DthDetailsState()
        data class Error(val errorResponseInfo: ErrorResponseInfo, val errorFromApi: String) :
            DthDetailsState()

        data class ExploreSuccess(val explore: ArrayList<ExploreContentResponse>) :
            DthDetailsState()
        data class BalanceSuccess(val balance: Int) :
            DthDetailsState()
    }
    sealed class DthDetailsEvent{
        data class ShowLowBalanceAlert(val amount:String?): DthDetailsEvent()
        data class ShowPaymentProcessingScreen(val payAndRechargeRequest: PayAndRechargeRequest): DthDetailsEvent()
        object OnPayClickEvent:DthDetailsEvent()

    }

}