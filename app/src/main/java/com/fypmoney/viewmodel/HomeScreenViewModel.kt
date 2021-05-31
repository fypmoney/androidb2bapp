package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel

class HomeScreenViewModel(application: Application) : BaseViewModel(application) {
    var availableAmount =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var onAddMoneyClicked = MutableLiveData(false)
    var onChoreClicked = MutableLiveData(false)

    /*
    * This is used to handle add money
    * */
    fun onAddMoneyClicked() {
        onAddMoneyClicked.value = true

    }

    /*
   * This is used to handle Open Chore
   * */
   /* fun onChoreClicked() {
        onChoreClicked.value = true

    }*/
}