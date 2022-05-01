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
import com.fypmoney.util.AppConstants.POSTPAID
import com.fypmoney.util.AppConstants.PREPAID
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargeTypeModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This is used to handle user profile
* */
class SelectOperatorFragmentVM(application: Application) : BaseViewModel(application) {
    var rechargeType:String? = null
    var mobileNo:String? = null

    val state:LiveData<SelectOperatorState>
        get() = _state
    private val _state = MutableLiveData<SelectOperatorState>()

    fun callGetOperatorList(postpaid: String) {
        _state.value = SelectOperatorState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_OPERATOR_LIST_MOBILE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_OPERATOR_LIST_MOBILE),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = RechargeTypeModel(type = postpaid)
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_OPERATOR_LIST_MOBILE -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<OperatorResponse>>(
                    json.get("data").toString(),
                    Array<OperatorResponse>::class.java
                )
                //add Postpaid user item in list
                val newData = array.toMutableList()
                if(rechargeType==PREPAID){
                    newData.add(OperatorResponse(name = "I am Postpaid User",id = "postpaid"))
                }else if(rechargeType== POSTPAID){
                    newData.add(OperatorResponse(name = "I am Prepaid User",id = "prepaid"))
                }
                _state.value = SelectOperatorState.Success(newData)
            }


        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_GET_OPERATOR_LIST_MOBILE -> {
                _state.value = SelectOperatorState.Error(errorResponseInfo)
            }

        }

    }

    sealed class SelectOperatorState{
        object Loading:SelectOperatorState()
        data class Success(val operatorList:List<OperatorResponse>):SelectOperatorState()
        data class Error(val errorResponseInfo: ErrorResponseInfo):SelectOperatorState()
    }
    sealed class SelectOperatorEvent{

    }

}