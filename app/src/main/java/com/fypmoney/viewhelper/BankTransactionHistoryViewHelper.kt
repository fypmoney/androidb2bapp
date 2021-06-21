package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel


/*
* This is used to display all the bank transaction history in the list
* */
class BankTransactionHistoryViewHelper(
    var position: Int? = -1,
    var bankHistory: BankTransactionHistoryResponseDetails?,
    var viewModel: BankTransactionHistoryViewModel

) {
    var date = ObservableField<String>()
    var amount = ObservableField<String>()
    var isCredited = ObservableField<Boolean>()
    fun init() {
        setInitialData()
    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
    }

    /*
    * This is used to set initial data
    * */
    private fun setInitialData() {
        when (bankHistory?.transaction_type) {
            AppConstants.CREDITED -> {
                isCredited.set(true)
                amount.set(
                    PockketApplication.instance.getString(R.string.add) + PockketApplication.instance.getString(
                        R.string.Rs
                    ) + bankHistory?.amount
                )

            }

        }

    }

}