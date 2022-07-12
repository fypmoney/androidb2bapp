package com.fypmoney.view.insights.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.insights.model.SpendAndIncomeNetworkResponse

class InsightsFragmentVM(application: Application) : BaseViewModel(application) {

    val state:LiveData<InsightsState>
        get() = _state
    private val _state = MutableLiveData<InsightsState>()

    val event:LiveData<InsightsEvent>
        get() = _event
    private val _event = LiveEvent<InsightsEvent>()


    sealed class InsightsState{
        object Loading:InsightsState()
        object Error:InsightsState()
        data class Success(val data : SpendAndIncomeNetworkResponse):InsightsState()
    }

    sealed class InsightsEvent{

    }

}