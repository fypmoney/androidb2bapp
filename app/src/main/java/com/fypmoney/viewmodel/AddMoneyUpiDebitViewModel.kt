package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.model.UpiModel
import com.fypmoney.view.adapter.AddMoneyUpiAdapter
import com.fypmoney.view.adapter.SavedCardsAdapter

class AddMoneyUpiDebitViewModel(application: Application) : BaseViewModel(application) {
    var amountToAdd = ObservableField<String>()
    var addMoneyUpiAdapter = AddMoneyUpiAdapter()
    var savedCardsAdapter = SavedCardsAdapter()
    init {

    }
}

