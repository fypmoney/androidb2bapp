package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.util.Utility
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
    var msg =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var amount = ObservableField<String?>()


    fun init() {
        try {
            amount.set(
              Utility.convertToRs(
                    transactionHistory?.txnAmount!!
                )
            )
            msg.set(PockketApplication.instance.getString(R.string.you_paid)+Utility.getDayMonth(transactionHistory?.txnTime))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {

    }


}