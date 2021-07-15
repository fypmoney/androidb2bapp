package com.fypmoney.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel

/*
* This is used for card ordering
* */
class OrderCardViewModel(application: Application) : BaseViewModel(application) {
    var onOrderCardClicked = MutableLiveData(false)
    /*
    * This is used to handle order card
    * */

    fun onOrderCardClicked() {
        onOrderCardClicked.value = true
    }
}