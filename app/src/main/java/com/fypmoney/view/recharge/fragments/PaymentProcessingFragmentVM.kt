package com.fypmoney.view.recharge.fragments

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
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.recharge.model.PayAndRechargeRequest
import com.fypmoney.view.recharge.model.PayAndRechargeResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class PaymentProcessingFragmentVM(application: Application) : BaseViewModel(application) {
    var rechargeRequest:PayAndRechargeRequest? = null

    val state:LiveData<PaymentProcessingState>
        get() = _state
    private val _state = MutableLiveData<PaymentProcessingState>()

    val event:LiveData<PaymentProcessingEvent>
        get() = _event
    private var _event = LiveEvent<PaymentProcessingEvent>()

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
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_MOBILE_RECHARGE ->{
                _state.value = PaymentProcessingState.Error(errorResponseInfo)

            }
        }
    }

    sealed class PaymentProcessingState{
        object Loading:PaymentProcessingState()
        data class Error(val errorResponseInfo: ErrorResponseInfo):PaymentProcessingState()
        data class Success(val payAndRechargeResponse: PayAndRechargeResponse):PaymentProcessingState()
    }
    sealed class PaymentProcessingEvent{
        data class ShowSuccessScreen(val payAndRechargeResponse: PayAndRechargeResponse):PaymentProcessingEvent()
        data class ShowFailedScreen(val payAndRechargeResponse: PayAndRechargeResponse):PaymentProcessingEvent()
        data class ShowPendingScreen(val payAndRechargeResponse: PayAndRechargeResponse):PaymentProcessingEvent()
    }
}