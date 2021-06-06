package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.viewmodel.TransactionHistoryViewModel


/*
* This is used to display all the transaction history in the list
* */
class TransactionHistoryViewHelper(
    var position: Int? = -1,
    var transactionHistory: TransactionHistoryResponseDetails?,
    var viewModel: TransactionHistoryViewModel
) {
    var availableAmount =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))



    fun init() {

    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {

    }




}