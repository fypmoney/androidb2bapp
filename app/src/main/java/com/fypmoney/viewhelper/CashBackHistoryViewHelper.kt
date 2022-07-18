package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.CHANGED_DATE_TIME_FORMAT3
import com.fypmoney.util.AppConstants.SERVER_DATE_TIME_FORMAT1
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.CashbackHistoryAdapter
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsCashbackwonVM
import java.lang.Exception


/*
* This is used to display all the bank transaction history in the list
* */
class CashBackHistoryViewHelper(
    var position: Int? = -1,
    var cashbackHistory: BankTransactionHistoryResponseDetails?,
    var viewModel: RewardsCashbackwonVM,
    var adapter: CashbackHistoryAdapter
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
        viewModel.onItemClicked.value = cashbackHistory
    }

    /*
    * This is used to set initial data
    * */
    private fun setInitialData() {
        // set date value

        when (cashbackHistory?.transactionType) {
            AppConstants.LOAD -> {
                isCredited.set(true)
                amount.set(
                    PockketApplication.instance.getString(R.string.add) + " " + PockketApplication.instance.getString(
                        R.string.Rs
                    ) + Utility.convertToRs(cashbackHistory?.amount)
                )

            }


        }

        date.set(Utility.parseDateTimeWithPlusFiveThirty(cashbackHistory?.transactionDate,
            inputFormat = SERVER_DATE_TIME_FORMAT1,
            outputFormat = CHANGED_DATE_TIME_FORMAT3))

        /*try {
            val dateVal = cashbackHistory?.transactionDate?.split("+")

            date.set(
                Utility.parseDateTime(
                    dateVal!![0],
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT3
                )
            )
            dateWithoutTime.set(
                Utility.parseDateTime(
                    dateVal[0],
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                )
            )
            // set line visibility
            if (position!! > 0 && position!! < adapter.transactionList!!.size) {
                val previousDateAfterParse = Utility.parseDateTime(
                    adapter.transactionList!![position!! - 1].transactionDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                )

                val nextDateAfterParse = Utility.parseDateTime(
                    adapter.transactionList!![position!! + 1].transactionDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
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
        }*/


    }

}