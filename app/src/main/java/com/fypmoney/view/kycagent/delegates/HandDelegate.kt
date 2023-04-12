package com.fypmoney.view.delegate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*

class HandDelegate {
    val hand: MutableLiveData<String> = MutableLiveData<String>()

    val isValid = Transformations.map(hand) {
        validateState(it)
    }

    fun getValue():String{
        return hand.value.toString()
    }

    private fun validateState(hand: String?): Boolean {
        return hand != null && hand.isNotEmpty()
    }
}