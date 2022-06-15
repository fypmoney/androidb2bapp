package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.recharge.model.BillPaymentRequest
import com.fypmoney.view.recharge.model.BillPaymentResponse
import com.fypmoney.view.recharge.model.PayAndRechargeRequest
import com.fypmoney.view.recharge.model.PayAndRechargeResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentProcessingFragmentVM(application: Application) : BaseViewModel(application) {
    var rechargeRequest:PayAndRechargeRequest? = null
    //in case of post paid
    var billPaymentRequest:BillPaymentRequest? = null
    var rechargeType:String? = null
    val state:LiveData<PaymentProcessingState>
        get() = _state
    private val _state = MutableLiveData<PaymentProcessingState>()

    val event:LiveData<PaymentProcessingEvent>
        get() = _event
    private var _event = LiveEvent<PaymentProcessingEvent>()

    var title = MutableLiveData<String>()


    //TODO Need to do refactor
    fun updateText(){
        viewModelScope.launch {
             rechargeRequest?.let { it1 ->
                 _state.value = PaymentProcessingState.UpdateTitle(
                     String.format(
                         PockketApplication.instance.resources.getString(R.string.processing_payment_of),
                         Utility.convertToRs(it1.amount.toString())
                     )
                 )
                delay(1000)
                 _state.value = PaymentProcessingState.UpdateTitle(
                     String.format(
                         PockketApplication.instance.resources.getString(R.string.processing_recharge_of),
                         it1.cardNo.toString()
                     )
                 )
            }
             billPaymentRequest?.let { it1 ->
                 _state.value = PaymentProcessingState.UpdateTitle(
                     String.format(
                         PockketApplication.instance.resources.getString(R.string.processing_payment_of),
                         Utility.convertToRs(it1.amount.toString())
                     )
                 )
                 delay(1000)
                 _state.value = PaymentProcessingState.UpdateTitle(
                     String.format(
                         PockketApplication.instance.resources.getString(R.string.processing_recharge_of),
                         it1.cardNo.toString()
                     )
                 )
             }
        }
    }

    fun callMobileRecharge(rechargeRequest: PayAndRechargeRequest) {
        _state.value = PaymentProcessingState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
            purpose = ApiConstant.API_MOBILE_RECHARGE,
            endpoint = NetworkUtil.endURL(ApiConstant.API_MOBILE_RECHARGE),
            request_type = ApiUrl.POST,
            onResponse = this, isProgressBar = false,
            param = rechargeRequest)
        )
    }

    fun payPostpaidBill() {
        _state.value = PaymentProcessingState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_PAY_BILL,
                endpoint = NetworkUtil.endURL(ApiConstant.API_PAY_BILL),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = billPaymentRequest!!

            )
        )

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_MOBILE_RECHARGE ->{
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val data = Gson().fromJson<PayAndRechargeResponse>(
                    json.get("data").toString(),
                    PayAndRechargeResponse::class.java
                )
                _state.value = PaymentProcessingState.Success(data)
                _event.value = PaymentProcessingEvent.ShowSuccessScreen(data)

            }
            ApiConstant.API_PAY_BILL -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    BillPaymentResponse::class.java
                )
                _state.value = PaymentProcessingState.BillSuccess(array)
                _event.value = PaymentProcessingEvent.ShowPostPaidSuccessScreen(array)

               // paymentResponse.postValue(array)
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_MOBILE_RECHARGE ->{
                _state.value = PaymentProcessingState.Error(
                    errorResponseInfo,
                    ApiConstant.API_GET_WALLET_BALANCE
                )
                _event.value = PaymentProcessingEvent.ShowPrepaidFailedScreen(null)

            }
            ApiConstant.API_PAY_BILL -> {
                _state.value =
                    PaymentProcessingState.Error(errorResponseInfo, ApiConstant.API_PAY_BILL)
                _event.value = PaymentProcessingEvent.ShowPostPaidFailedScreen(null)
            }

            ApiConstant.API_GET_WALLET_BALANCE->{
                _state.value = PaymentProcessingState.Error(
                    errorResponseInfo,
                    ApiConstant.API_GET_WALLET_BALANCE
                )
            }
        }
    }

    sealed class PaymentProcessingState{
        object Loading: PaymentProcessingState()
        data class Error(val errorResponseInfo: ErrorResponseInfo,val api:String):
            PaymentProcessingState()
        data class Success(val payAndRechargeResponse: PayAndRechargeResponse):
            PaymentProcessingState()
        data class BillSuccess(val billPaymentResponse: BillPaymentResponse):
            PaymentProcessingState()
        data class UpdateTitle(val title: String): PaymentProcessingState()
    }
    sealed class PaymentProcessingEvent{
        data class ShowSuccessScreen(val payAndRechargeResponse: PayAndRechargeResponse):
            PaymentProcessingEvent()
        data class ShowPostPaidSuccessScreen(val billPaymentResponse: BillPaymentResponse):
            PaymentProcessingEvent()
        data class ShowPostPaidFailedScreen(val billPaymentResponse: BillPaymentResponse?):
            PaymentProcessingEvent()
        data class ShowPrepaidFailedScreen(val payAndRechargeResponse: PayAndRechargeResponse?):
            PaymentProcessingEvent()
        data class ShowPendingScreen(val payAndRechargeResponse: PayAndRechargeResponse):
            PaymentProcessingEvent()
    }
}