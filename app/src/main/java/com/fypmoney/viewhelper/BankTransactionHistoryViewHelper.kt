package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel
import com.fypmoney.viewmodel.ContactListViewModel


/*
* This is used to display all the bank transaction history in the list
* */
class BankTransactionHistoryViewHelper(
    var position: Int? = -1,
    var bankHistory: BankTransactionHistoryResponseDetails?,
    var viewModel: BankTransactionHistoryViewModel
) {

    var isContactSelected = ObservableField(false)
    var isAppUser = ObservableField(false)
    var isBackgroundHighlight = ObservableField<Boolean>()
    var isApiError = ObservableField(false)

    fun init() {
      }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
    }



}