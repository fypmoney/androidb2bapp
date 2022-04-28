package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.ProfileImageUploadResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KYC_TYPE
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.recharge.model.CircleResponse
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargePlansRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MultipartBody


class SelectCircleFragmentVM(application: Application) : BaseViewModel(application) {


    var opertaorList: MutableLiveData<ArrayList<CircleResponse>> = MutableLiveData()
    var selectedOperator = MutableLiveData<OperatorResponse>()

    var mobile = MutableLiveData<String>()

    var rechargeType = MutableLiveData<String>()

    val state:LiveData<SelectCircleState>
        get() = _state
    private val _state = MutableLiveData<SelectCircleState>()

    val event:LiveData<SelectCircleEvent>
        get() = _event
    private val _event = LiveEvent<SelectCircleEvent>()

    fun callGetCircleList() {
        _state.value = SelectCircleState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CIRCLE_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CIRCLE_LIST + selectedOperator.value?.operatorId),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = RechargePlansRequest()
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_CIRCLE_LIST -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<CircleResponse>>(
                    json.get("data").toString(),
                    Array<CircleResponse>::class.java
                )
                _state.value = SelectCircleState.Success(array.toList())
            }
        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_GET_CIRCLE_LIST -> {
                _state.value = SelectCircleState.Error(errorResponseInfo)
            }
        }

    }

    sealed class SelectCircleState{
        object Loading:SelectCircleState()
        data class Success(val circles:List<CircleResponse>):SelectCircleState()
        data class Error(val errorResponseInfo: ErrorResponseInfo):SelectCircleState()
    }
    sealed class SelectCircleEvent{

    }
}


