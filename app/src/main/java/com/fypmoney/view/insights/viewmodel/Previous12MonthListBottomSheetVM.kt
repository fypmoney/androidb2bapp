package com.fypmoney.view.insights.viewmodel

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

class Previous12MonthListBottomSheetVM(application: Application):BaseViewModel(application) {
    lateinit var monthList:List<MonthItem>
    val state:LiveData<Previous12MonthState>
        get() = _state
    private val _state = MutableLiveData<Previous12MonthState>()
    fun showMonthList(){
        _state.value  = Previous12MonthState.ShowMonths(monthList)
    }
    sealed class Previous12MonthState{
        data class ShowMonths(var monthList:List<MonthItem>):Previous12MonthState()
    }
}

@Keep
data class MonthItem(
    var monthName:String,
    var position:Int,
    var isSelected:Boolean
)