package com.fypmoney.view.kycagent.delegates

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*

class StateDelegate {
    val state: MutableLiveData<String> = MutableLiveData<String>()

    val isValid = Transformations.map(state) {
        validateState(it)
    }

    fun getValue():String{
        return state.value.toString()
    }

    private fun validateState(state: String?): Boolean {
        return state != null && state.isNotEmpty()
    }

}