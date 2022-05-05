package com.fypmoney.view.recharge.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.recharge.model.CircleResponse
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.model.RechargePlansRequest
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


@FlowPreview
@ObsoleteCoroutinesApi
class SelectCircleFragmentVM(application: Application) : BaseViewModel(application) {


    var selectedOperator = MutableLiveData<OperatorResponse>()

    var mobile = MutableLiveData<String>()

    var rechargeType = MutableLiveData<String>()

    val state:LiveData<SelectCircleState>
        get() = _state
    private val _state = MediatorLiveData<SelectCircleState>()

    val event:LiveData<SelectCircleEvent>
        get() = _event
    private val _event = LiveEvent<SelectCircleEvent>()

    val searchQuery = MutableLiveData<String>()

    private var circles = listOf<CircleResponse>()

    @ObsoleteCoroutinesApi
    private val circleSearchQueryBroadcastChannel = ConflatedBroadcastChannel<String>()

    init {
        emitSearchQuery()
        observeSearchQuery()
    }

    @ObsoleteCoroutinesApi
    private fun emitSearchQuery() {
        _state.addSource(searchQuery) {
            it?.let { circleSearchQueryBroadcastChannel.trySend(it) }
        }
    }

    @FlowPreview
    private fun observeSearchQuery(){
        viewModelScope.launch {
            circleSearchQueryBroadcastChannel.asFlow().debounce(800).collect {
                if(circles.isNotEmpty()){
                    val filteredList = circles.filterIndexed { index, circleResponse ->
                        circleResponse.name?.contains(it,true)!!
                    }
                    _state.value = SelectCircleState.Success(filteredList)
                }else{
                    _state.value = SelectCircleState.Empty
                }
            }
        }
    }
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
                circles = array.toList()
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
        object Empty:SelectCircleState()
        data class Error(val errorResponseInfo: ErrorResponseInfo):SelectCircleState()
    }
    sealed class SelectCircleEvent{

    }
}


