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
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.PayAndRechargeRequest
import com.fypmoney.view.recharge.model.PayAndRechargeResponse
import com.fypmoney.view.recharge.model.ValueItem
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class SelectedPlanDetailsRechargeFragmentVM(application: Application) : BaseViewModel(application) {
    var failedresponse: MutableLiveData<String> = MutableLiveData()
    var selectedPlan: ValueItem? = null
    var mobile: String? = null
    var planType: String? = null
    var rechargeType: String? = null
    var operatorResponse:OperatorResponse? = null

    var success = MutableLiveData<PayAndRechargeResponse>()

    val event:LiveData<SelectedPlanDetailsRechargeEvent>
        get() = _event
    private val _event = LiveEvent<SelectedPlanDetailsRechargeEvent>()

    val state:LiveData<SelectedPlanDetailsRechargeState>
        get() = _state
    private val _state = LiveEvent<SelectedPlanDetailsRechargeState>()

    fun onContinueClick(){
        fetchBalance()
       /* _event.value = SelectedPlanDetailsRechargeEvent.Pay(PayAndRechargeRequest(
            cardNo = mobile,
            operator = operatorResponse?.operatorId,
            planPrice = Utility.convertToPaise(selectedPlan?.rs)?.toLong(),
            planType =  planType,
            amount = Utility.convertToPaise(selectedPlan?.rs)?.toLong()
        ))*/
    }

    fun onChangePlanClick(){
        _event.value = SelectedPlanDetailsRechargeEvent.ChangePlan
    }

    fun fetchBalance() {
        _state.value = SelectedPlanDetailsRechargeState.Loading
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
    /*fun callMobileRecharge(
        selectedpaln: ValueItem?,
        number: String?,
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
                    cardNo = number,
                    operator = operatorResponse.value?.operatorId,
                    planPrice = Utility.convertToPaise(selectedpaln?.rs)?.toLong(),
                    planType = value3,
                    amount = Utility.convertToPaise(selectedpaln?.rs)?.toLong()
                )
            )
        )
    }*/


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_MOBILE_RECHARGE -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson<PayAndRechargeResponse>(
                    json.get("data").toString(),
                    PayAndRechargeResponse::class.java
                )
                success.postValue(array)
            }
            ApiConstant.API_GET_WALLET_BALANCE -> {
            if (responseData is GetWalletBalanceResponse) {
                responseData.getWalletBalanceResponseDetails.accountBalance.toIntOrNull()
                    ?.let { accountBalance ->
                        _state.value = SelectedPlanDetailsRechargeState.Success(accountBalance)
                        if(accountBalance< Utility.convertToPaise(selectedPlan?.rs)?.toLong()!!){
                            _event.value = SelectedPlanDetailsRechargeEvent.ShowLowBalanceAlert(((Utility.convertToPaise(selectedPlan?.rs)?.toLong()!!)-(accountBalance)).toString())
                        }else{
                            _event.value =
                                SelectedPlanDetailsRechargeEvent.ShowPaymentProcessingScreen(PayAndRechargeRequest(
                                    cardNo = mobile,
                                    operator = operatorResponse?.operatorId,
                                    planPrice = Utility.convertToPaise(selectedPlan?.rs)?.toLong(),
                                    planType =  planType,
                                    amount = Utility.convertToPaise(selectedPlan?.rs)?.toLong()
                                ))
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
                failedresponse.postValue(
                    "failed"
                )
            }
            ApiConstant.API_GET_WALLET_BALANCE->{
                _state.value = SelectedPlanDetailsRechargeState.Error(errorResponseInfo)
            }

        }

    }

    sealed class SelectedPlanDetailsRechargeEvent{
        data class ShowPaymentProcessingScreen(val payRequest:PayAndRechargeRequest):SelectedPlanDetailsRechargeEvent()
        data class ShowLowBalanceAlert(val amount:String?):SelectedPlanDetailsRechargeEvent()
        object ChangePlan:SelectedPlanDetailsRechargeEvent()
    }
    sealed class SelectedPlanDetailsRechargeState{
        object Loading:SelectedPlanDetailsRechargeState()
        data class Error(val errorResponseInfo: ErrorResponseInfo):SelectedPlanDetailsRechargeState()
        data class Success(var balance:Int):SelectedPlanDetailsRechargeState()
    }

}