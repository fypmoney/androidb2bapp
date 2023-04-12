package com.fypmoney.view.kycagent.viewmodel

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
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.model.EarningItem
import com.fypmoney.view.kycagent.model.MyEarningsListResponse

class MyEarningsFragmentVM(application: Application) : BaseViewModel(application) {

    val state : LiveData<MyEarningsState>
        get() = _state
    private val _state = MutableLiveData<MyEarningsState>()

    var totalEarnings: String = "0.0"

    init {
        getEarningsList()
    }

    private fun getEarningsList(){
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_AGENTS_EARNINGS_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_AGENTS_EARNINGS_LIST),
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when(purpose){
            ApiConstant.API_AGENTS_EARNINGS_LIST -> {
                if (responseData is MyEarningsListResponse) {
                    totalEarnings =
                        Utility.convertToRs(responseData.data?.totalEarnings).toString()
                    _state.value = responseData.data?.earning?.let { MyEarningsState.Success(it) }
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when(purpose){
            ApiConstant.API_AGENTS_EARNINGS_LIST -> {
                _state.value = MyEarningsState.Error(errorResponseInfo)
            }
        }

    }

    sealed class MyEarningsState{
        object Loading : MyEarningsState()
        data class Success(var listOfEarnings : List<EarningItem?>) : MyEarningsState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : MyEarningsState()
    }

}