package com.fypmoney.view.insights.viewmodel

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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.getCurrentMonth
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.insights.model.AllTxnItem
import com.fypmoney.view.insights.model.InsightsNetworkRequest
import com.fypmoney.view.insights.model.SpendAndIncomeNetworkResponse
import com.google.gson.Gson
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.json.JSONObject

@FlowPreview
@ObsoleteCoroutinesApi
class InsightsFragmentVM(application: Application) : BaseViewModel(application) {

    val state:LiveData<InsightsState>
        get() = _state
    private val _state = MediatorLiveData<InsightsState>()

    val event:LiveData<InsightsEvent>
        get() = _event
    private val _event = LiveEvent<InsightsEvent>()

    var startDate:String? = null
    var endDate:String? = null
    var selectedMonth = MutableLiveData(0)

     var allTxnItem:List<AllTxnItem?>? = null
    @ObsoleteCoroutinesApi
    private val selectedMonthBroadcastChannel = ConflatedBroadcastChannel<Int>()

    private lateinit var last12Month:List<Utility.Last12MonthItem>
    init {
        setupData()
        emitSelectedMonth()
        observeSearchQuery()
    }
    @ObsoleteCoroutinesApi
    private fun emitSelectedMonth() {
        _state.addSource(selectedMonth) {
            it?.let { selectedMonthBroadcastChannel.trySend(it) }
        }
    }
    @ObsoleteCoroutinesApi
    @FlowPreview
    private fun observeSearchQuery(){
        viewModelScope.launch {
            selectedMonthBroadcastChannel.asFlow().debounce(800).collect {
                setUpStartAndEndDate()
                getInsights()
            }
        }
    }
    fun selectPreviousMonthClick(){
        if((0<=selectedMonth.value!!)&&(selectedMonth.value!!<last12Month.size)){
            selectedMonth.value = (selectedMonth.value!!)+1
            _state.value = InsightsState.SelectPreviousMonth(last12Month[selectedMonth.value!!].monthFullName)

        }
    }
    fun selectNextMonthClick(){
        if(selectedMonth.value!!>=0){
            selectedMonth.value = (selectedMonth.value!!)-1
            _state.value = InsightsState.SelectPreviousMonth(last12Month[selectedMonth.value!!].monthFullName)
           /* setUpStartAndEndDate()
            getInsights()*/
        }
    }
    fun showMonthFilterClick(){
        val listOfMonth = mutableListOf<MonthItem>()
        last12Month.forEachIndexed { index, last12MonthItem ->
            listOfMonth.add(MonthItem(monthName = last12MonthItem.monthFullName, position = index, isSelected = index==selectedMonth.value!!))
        }
        _event.value = InsightsEvent.ShowMonthListClickEvent(listOfMonth,selectedMonth.value!!)
    }
    private fun setupData(){
        setUpStartAndEndDate()
        last12Month = Utility.getLast12Months(getCurrentMonth())
    }

    fun showTransactionHistory(){
        _event.value = InsightsEvent.ShowTransactionHistory
    }

    private fun setUpStartAndEndDate() {
        val startDateAndEndDateOfCurrentDate = Utility.getStartDateAndEndDateOfMonth(
            selectedMonth.value!!,
            AppConstants.CHANGED_DATE_TIME_FORMAT5
        )
        startDate = startDateAndEndDateOfCurrentDate.first.trim()
        endDate = startDateAndEndDateOfCurrentDate.second.trim()
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
                    allTxnItem = parseData.allTxn
                    _state.value = InsightsState.Success(parseData,last12Month[selectedMonth.value!!].monthFullName)
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
        data class Success(val data : SpendAndIncomeNetworkResponse,val currentMonth:String):InsightsState()
        data class SelectPreviousMonth(var prevMonth:String):InsightsState()
        data class SelectNextMonth(var nextMonth:String):InsightsState()
    }

    sealed class InsightsEvent{
        object ShowTransactionHistory:InsightsEvent()
        data class ShowMonthListClickEvent(val listOfMonth:List<MonthItem>,val selectedPosition:Int):InsightsEvent()
    }
    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }
}