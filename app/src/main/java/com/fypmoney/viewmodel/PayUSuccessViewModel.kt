package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.model.AddMoneyStep2ResponseDetails
import com.fypmoney.util.Utility

/*
* This class is used to pay u success response
* */
class PayUSuccessViewModel(application: Application) : BaseViewModel(application) {
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var paymentDateTime = ObservableField<String>()
    var onContinueClicked = MutableLiveData<Boolean>()
    var payUResponse = ObservableField<AddMoneyStep2ResponseDetails>()

    /*
    * This is used to set the initial parameters
    * */
    fun setInitialData() {
        availableAmount.set(Utility.convertToRs(payUResponse.get()?.amount!!))
        paymentDateTime.set(Utility.parseDateTime(payUResponse.get()?.accountTxnTime))

    }

    /*
     * This is used to handle on continue click
     * */
    fun onContinueClicked() {
        onContinueClicked.value = true
    }
}