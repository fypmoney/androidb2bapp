package com.fypmoney.view.arcadegames.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.livedata.LiveEvent

class LimitOverVM(application: Application): BaseViewModel(application) {
    val event: LiveData<LimitOverEvent>
        get() = _event

    private val _event = LiveEvent<LimitOverEvent>()

    fun onMoreRewardsClicked(){
        _event.value = LimitOverEvent.MoreRewardsEvent
    }

    fun onContinueClicked(){
        _event.value = LimitOverEvent.ContinueEvent
    }

    sealed class LimitOverEvent{
        object MoreRewardsEvent : LimitOverEvent()
        object ContinueEvent : LimitOverEvent()
    }
}