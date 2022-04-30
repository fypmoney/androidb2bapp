package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ErrorResponseInfo
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
    var operatorResponse:OperatorResponse? = null

    var success = MutableLiveData<PayAndRechargeResponse>()

    val event:LiveData<SelectedPlanDetailsRechargeEvent>
        get() = _event
    private val _event = LiveEvent<SelectedPlanDetailsRechargeEvent>()

    fun onContinueClick(){
        _event.value = SelectedPlanDetailsRechargeEvent.Pay(PayAndRechargeRequest(
            cardNo = mobile,
            operator = operatorResponse?.operatorId,
            planPrice = Utility.convertToPaise(selectedPlan?.rs)?.toLong(),
            planType =  planType,
            amount = Utility.convertToPaise(selectedPlan?.rs)?.toLong()
        ))
    }

    fun onChangePlanClick(){
        _event.value = SelectedPlanDetailsRechargeEvent.ChangePlan
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

                Utility.showToast("Success")
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<PayAndRechargeResponse>(
                    json.get("data").toString(),
                    PayAndRechargeResponse::class.java
                )

                success.postValue(array)


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

        }

    }

    sealed class SelectedPlanDetailsRechargeEvent{
        data class Pay(val payRequest:PayAndRechargeRequest):SelectedPlanDetailsRechargeEvent()
        object ChangePlan:SelectedPlanDetailsRechargeEvent()
    }

}