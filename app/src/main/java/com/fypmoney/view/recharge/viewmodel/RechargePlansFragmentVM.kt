package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant.API_RECHARGE_PLANS
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargePlansRequest
import com.fypmoney.view.recharge.model.RechargePlansResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class RechargePlansFragmentVM(application: Application) : BaseViewModel(application) {

    var selectedOperator = MutableLiveData<OperatorResponse>()
    var selectedCircle = MutableLiveData<String>()
    var mobile = MutableLiveData<String>()


    val state:LiveData<RechargePlanState>
        get() = _state
    private val _state = MutableLiveData<RechargePlanState>()

    fun callRechargePlan() {
        _state.value = RechargePlanState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = API_RECHARGE_PLANS,
                endpoint = NetworkUtil.endURL(API_RECHARGE_PLANS),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = RechargePlansRequest(
                    operator = selectedOperator.value?.name,
                    circle = selectedCircle.value
                )
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            API_RECHARGE_PLANS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson<Array<RechargePlansResponse>>(
                    json.get("data").toString(),
                    Array<RechargePlansResponse>::class.java
                )
                _state.value = RechargePlanState.Success(array.toMutableList())

            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            API_RECHARGE_PLANS->{
                _state.value = RechargePlanState.Error(errorResponseInfo)
            }
        }
    }

    sealed class RechargePlanState{
        object Loading:RechargePlanState()
        data class Error(val errorResponseInfo: ErrorResponseInfo):RechargePlanState()
        data class Success(val plans:List<RechargePlansResponse>):RechargePlanState()
    }

}