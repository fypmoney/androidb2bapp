package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.Utility

class EnterAmountForPayRequestViewModel(application: Application) : BaseViewModel(application)  {
    var contactName = ObservableField<String>()
    var onPayClicked = MutableLiveData(false)
    var setEdittextLength = MutableLiveData(false)
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>()
    var message = ObservableField<String>()

    /*
      * This method is used to handle on click of pay or request button
      * */
    fun onPayClicked() {
        when {
            TextUtils.isEmpty(amountSelected.get()) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
            }
            else -> {
                onPayClicked.value = true
            }
        }
    }

    fun onAmountSelected(amount: Int) {
        amountSelected.set(amount.toString())
        setEdittextLength.value=true
    }



}