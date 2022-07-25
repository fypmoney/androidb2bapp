package com.fypmoney.view.insights.viewmodel

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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.insights.model.InsightsNetworkRequest
import com.fypmoney.view.insights.model.SpendAndIncomeNetworkResponse
import com.google.gson.Gson
import org.json.JSONObject

class InsightsFragmentVM(application: Application) : BaseViewModel(application) {

    val state:LiveData<InsightsState>
        get() = _state
    private val _state = MutableLiveData<InsightsState>()

    val event:LiveData<InsightsEvent>
        get() = _event
    private val _event = LiveEvent<InsightsEvent>()

    var startDate:String? = null
    var endDate:String? = null

    var last12Month:List<Utility.Last12MonthItem>
    init {
        val startDateAndEndDateOfCurrentDate = Utility.getStartDateAndEndDateOfMonth(0,
            AppConstants.CHANGED_DATE_TIME_FORMAT5
        )
        startDate = startDateAndEndDateOfCurrentDate.first.trim()
        endDate = startDateAndEndDateOfCurrentDate.second.trim()
        last12Month = Utility.getLast12Months("0")
        _state.postValue(InsightsState.ShowCurrentMonth(last12Month[0].monthFullName))
        getInsights()
    }
    private fun getInsights() {
        _state.postValue(InsightsState.Loading)
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_30_DAYS_TRANSACTION,
                NetworkUtil.endURL(ApiConstant.API_30_DAYS_TRANSACTION),
                ApiUrl.POST,
                InsightsNetworkRequest(startDate,endDate),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_30_DAYS_TRANSACTION->{
                val data = JSONObject(responseData.toString())
                val parseData = getObject(data.getString("data"),SpendAndIncomeNetworkResponse::class.java)
                if(parseData is SpendAndIncomeNetworkResponse){
                    _state.value = InsightsState.Success(parseData)
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_30_DAYS_TRANSACTION->{
                _state.value = InsightsState.Error
            }

        }
    }

    sealed class InsightsState{
        object Loading:InsightsState()
        object Error:InsightsState()
        data class Success(val data : SpendAndIncomeNetworkResponse):InsightsState()
        data class ShowCurrentMonth(val currentMonth:String):InsightsState()
    }

    sealed class InsightsEvent{

    }
    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }
}