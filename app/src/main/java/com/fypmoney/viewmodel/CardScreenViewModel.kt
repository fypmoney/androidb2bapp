package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel

class CardScreenViewModel(application: Application) : BaseViewModel(application) {
    var isCardDetailsVisible =
        ObservableField(false)
    var balance =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var name =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_name))
    var cardNumber =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_card))
    var cvv =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_cvv))
    var expiry =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_expiry))

    /*
    * This is used to see the card details
    * */
    fun onViewDetailsClicked() {
        isCardDetailsVisible.set(true)
    }

}