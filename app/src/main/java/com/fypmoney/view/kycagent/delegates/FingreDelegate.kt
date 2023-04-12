package com.fypmoney.view.delegate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import java.util.*

class FingreDelegate {
    val fingre: MutableLiveData<String> = MutableLiveData<String>()

    val isValid = Transformations.map(fingre) {
        validateFingre(it)
    }

    fun getValue():String{
        return fingre.value.toString()
    }

    private fun validateFingre(hand: String?): Boolean {
        return hand != null && hand.isNotEmpty()
    }
}