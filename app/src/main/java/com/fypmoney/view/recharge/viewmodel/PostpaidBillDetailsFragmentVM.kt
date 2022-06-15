package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetWalletBalanceResponse
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.recharge.model.BillPaymentRequest
import com.fypmoney.view.recharge.model.FetchbillRequest
import com.fypmoney.view.recharge.model.FetchbillResponse
import com.fypmoney.view.recharge.model.OperatorResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class PostpaidBillDetailsFragmentVM(application: Application) : BaseViewModel(application) {

    var billDetails: FetchbillResponse? = null


    var operatorResponse:OperatorResponse? = null

    var circleGot:String? = null
    var rechargeType:String? = null
    var mobileNumber:String? = null
    var amount:String? = null

    val state:LiveData<PostpaidBillDetailsState>
        get() = _state
    private val _state = MutableLiveData<PostpaidBillDetailsState>()

    val event:LiveData<PostpaidBilDetailsEvent>
        get() = _event
    private val _event = LiveEvent<PostpaidBilDetailsEvent>()

    fun onPayClick(){
        _event.value = PostpaidBilDetailsEvent.OnPayClickEvent
    }
    fun callFetchBillsInformation(mobileNumber: String,operatorId:String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_FETCH_BILL,
                endpoint = NetworkUtil.endURL(ApiConstant.API_FETCH_BILL),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = FetchbillRequest(
                    canumber = mobileNumber,
                    operator = operatorId,
                    mode = "online"
                )
            )
        )
    }

    fun fetchBalance() {
        _state.postValue(PostpaidBillDetailsState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }




    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_FETCH_BILL -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson<FetchbillResponse>(
                    json.get("data").toString(),
                    FetchbillResponse::class.java
                )
                billDetails = array
                _state.value = PostpaidBillDetailsState.FetchBillSuccess(array)
            }
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    responseData.getWalletBalanceResponseDetails.accountBalance.toIntOrNull()
                        ?.let { accountBalance ->
                            _state.value = PostpaidBillDetailsState.BalanceSuccess(accountBalance)
                            if(accountBalance< Utility.convertToPaise(amount?.toDouble().toString())?.toLong()!!){
                                _event.value = PostpaidBilDetailsEvent.ShowLowBalanceAlert(
                                    ((Utility.convertToPaise(amount?.toDouble().toString())
                                        ?.toLong()!!) - (accountBalance)).toString()
                                )
                            }else{
                                _event.value = PostpaidBilDetailsEvent.ShowPaymentProcessingScreen(
                                    BillPaymentRequest(
                                        cardNo = mobileNumber,
                                        operator = operatorResponse?.operatorId,
                                        operatorName = operatorResponse?.name,
                                        amount = Utility.convertToPaise(amount?.toDoubleOrNull()?.toString()),
                                        planPrice = Utility.convertToPaise(amount?.toDoubleOrNull()?.toString()),
                                        planType = null,
                                        billAmount = Utility.convertToPaise(amount?.toDouble()?.toString())?.toLongOrNull(),
                                        billnetamount = billDetails?.bill_fetch?.billnetamount?.toDoubleOrNull()?.toString(),
                                        mode = "online",
                                        dueDate = billDetails?.bill_fetch?.dueDate,
                                        acceptPartPay = billDetails?.bill_fetch?.acceptPartPay,
                                        acceptPayment = billDetails?.bill_fetch?.acceptPayment,
                                        cellNumber = mobileNumber,
                                        userName = billDetails?.bill_fetch?.userName,
                                        latitude = "23.923828392",
                                        longitude = "75.82389283",
                                        billdate = billDetails?.bill_fetch?.billdate



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
            ApiConstant.API_FETCH_BILL -> {
                _state.value = PostpaidBillDetailsState.Error(errorResponseInfo,purpose)
            }
            ApiConstant.API_GET_WALLET_BALANCE->{
                _state.value = PostpaidBillDetailsState.Error(errorResponseInfo,purpose)
            }
        }
    }

    sealed class PostpaidBillDetailsState{
        object Loading:PostpaidBillDetailsState()
        data class Error(val errorResponseInfo: ErrorResponseInfo,val api:String):PostpaidBillDetailsState()
        data class FetchBillSuccess(val bill:FetchbillResponse):PostpaidBillDetailsState()
        data class BalanceSuccess(var balance:Int):PostpaidBillDetailsState()
    }
    sealed class PostpaidBilDetailsEvent{
        data class ShowPaymentProcessingScreen(val billPaymentRequest: BillPaymentRequest):PostpaidBilDetailsEvent()
        data class ShowLowBalanceAlert(val amount:String?): PostpaidBilDetailsEvent()
        object OnPayClickEvent:PostpaidBilDetailsEvent()


    }

}