package com.fypmoney.viewhelper

import androidx.databinding.ObservableField
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
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
    var comment = ObservableField<String?>()
    var commentIsVisible = ObservableField<Boolean?>(true)
    var date = ObservableField<String?>()


    fun init() {
        try {
            amount.set(
                Utility.convertToRs(
                    transactionHistory?.txnAmount!!
                )
            )
            when(transactionHistory?.isSender){
                AppConstants.YES->{
                    msg.set(
                        PockketApplication.instance.getString(R.string.paid_on) + " " + Utility.parseDateTime(
                            transactionHistory?.txnTime,
                            inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                            outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT8
                        )
                    )
                }
                AppConstants.NO->{
                    msg.set(
                        PockketApplication.instance.getString(R.string.received_on) + Utility.parseDateTime(
                            transactionHistory?.txnTime,
                            inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                            outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT8
                        )
                    )
                }
            }

            date.set(
                Utility.parseDateTime(
                    transactionHistory?.txnTime,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT2
                )
            )
            if(transactionHistory?.comments.isNullOrEmpty()){
                commentIsVisible.set(false)
            }else{
                comment.set(
                    transactionHistory?.comments)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * called when any item is selected
     * */
    fun onItemClicked() {
        viewModel.onItemClicked.value = transactionHistory
    }


}