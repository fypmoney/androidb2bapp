package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.view.adapter.BankTransactionHistoryAdapter

class BankTransactionHistoryViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)
    var bankTransactionHistoryAdapter = BankTransactionHistoryAdapter(this)
}