package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.model.AddMoneyStep2ResponseDetails

/*
* This class is used to pay u success response
* */
class PayUSuccessViewModel(application: Application) : BaseViewModel(application) {
     var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
     var paymentDateTime = ObservableField<String>()
     var fypTransactionId = ObservableField<String>()
     var bankTransactionId = ObservableField<String>()
     var payUResponse = ObservableField<AddMoneyStep2ResponseDetails>()


     fun setInitialData()
     {


     }
}