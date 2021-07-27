package com.fypmoney.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This is used to set pin
* */
class SetPinViewModel(application: Application) : BaseViewModel(application) {
    var onCrossClicked = MutableLiveData<Boolean>()


    /*
    * This method is used to handle cross
    * */
    fun onCrossClicked() {
        onCrossClicked.value = true
    }


}