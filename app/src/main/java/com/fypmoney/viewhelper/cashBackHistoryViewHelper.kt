package com.fypmoney.viewhelper

import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.model.CashbackWonResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.BankTransactionHistoryAdapter
import com.fypmoney.view.adapter.CashbackHistoryAdapter
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import com.fypmoney.viewmodel.BankTransactionHistoryViewModel
import java.lang.Exception


/*
* This is used to display all the bank transaction history in the list
* */
class cashBackHistoryViewHelper(
    var position: Int? = -1,
    var bankHistory: CashbackWonResponse?,
    var viewModel: RewardsCashbackwonVM,
    var adapter: CashbackHistoryAdapter
) {
    var date = ObservableField<String>()
    var amount = ObservableField<String>()
    var dateWithoutTime = ObservableField<String>()
    var isCredited = ObservableField<Boolean>()
    var isLineVisible = ObservableField<Boolean>()
    fun init() {
        setInitialData()
    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
        viewModel.onItemClicked.value = bankHistory
    }

    /*
    * This is used to set initial data
    * */
    private fun setInitialData() {
        // set date value

        when (bankHistory?.transactionType) {
            AppConstants.CREDITED -> {
                isCredited.set(true)
                amount.set(
                    PockketApplication.instance.getString(R.string.add) + " " + PockketApplication.instance.getString(
                        R.string.Rs
                    ) + Utility.convertToRs(bankHistory?.amount)
                )

            }
            AppConstants.DEBITED -> {
                isCredited.set(false)
                amount.set(
                    PockketApplication.instance.getString(R.string.subtract) + " " + PockketApplication.instance.getString(
                        R.string.Rs
                    ) + Utility.convertToRs(bankHistory?.amount)
                )
            }

        }

        try {
            val dateVal = bankHistory?.transactionDate?.split("+")

            date.set(
                Utility.parseDateTime(
                    dateVal!![0],
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT3
                )
            )
            dateWithoutTime.set(
                Utility.parseDateTime(
                    dateVal[0],
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                )
            )
            // set line visibility
            if (position!! > 0 && position!! < adapter.transactionList!!.size) {
                val previousDateAfterParse = Utility.parseDateTime(
                    adapter.transactionList!![position!! - 1].transactionDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                )

                val nextDateAfterParse = Utility.parseDateTime(
                    adapter.transactionList!![position!! + 1].transactionDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                )
                if (dateWithoutTime.get() == previousDateAfterParse && dateWithoutTime.get() == nextDateAfterParse) {
                    isLineVisible.set(false)
                } else if (dateWithoutTime.get() != previousDateAfterParse && dateWithoutTime.get() == nextDateAfterParse) {
                    isLineVisible.set(false)
                } else {
                    isLineVisible.set(true)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}