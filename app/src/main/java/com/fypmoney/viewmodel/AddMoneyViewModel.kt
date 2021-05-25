package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.util.Utility

/*
* This class is used to handle add money functionality
* */
class AddMoneyViewModel(application: Application) : BaseViewModel(application) {
    var onAddClicked = MutableLiveData(false)
    var setEdittextLength = MutableLiveData(false)
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>()

    /*
      * This method is used to handle on click of add
      * */
    fun onAddClicked() {
        when {
            TextUtils.isEmpty(amountSelected.get()) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
            }
            else -> {
                onAddClicked.value = true
            }
        }
    }

        fun onAmountSelected(amount: Int) {
            amountSelected.set(amount.toString())
            setEdittextLength.value=true
        }


    }


