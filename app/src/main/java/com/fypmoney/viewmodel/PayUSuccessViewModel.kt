package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.model.AddMoneyStep2ResponseDetails
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import java.text.SimpleDateFormat

/*
* This class is used to pay u success response
* */
class PayUSuccessViewModel(application: Application) : BaseViewModel(application) {
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var paymentDateTime = ObservableField<String>()
    var fypId = ObservableField<String>()
    var bankId = ObservableField<String>()
    var userName = ObservableField<String>()
    var fromWhichScreen = ObservableField<String>()
    var phoneNo = ObservableField<String>()
    var isAddMoneyLayoutVisible = ObservableField<Boolean>()
    var payUResponse = ObservableField<AddMoneyStep2ResponseDetails>()
    var bankResponse = ObservableField<BankTransactionHistoryResponseDetails>()
    var transactionHistoryResponse = ObservableField<TransactionHistoryResponseDetails>()

    /*
    * This is used to set the initial parameters
    * */
    fun setInitialData() {
        when (fromWhichScreen.get()) {
            AppConstants.ADD_MONEY -> {
                isAddMoneyLayoutVisible.set(true)
                availableAmount.set(Utility.convertToRs(payUResponse.get()?.amount))
                paymentDateTime.set(
                    Utility.parseDateTime(
                        payUResponse.get()?.txnTime,
                        inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT,
                        outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT
                    )
                )
                fypId.set(payUResponse.get()?.accountTxnNo)
                bankId.set(payUResponse.get()?.bankExternalId)
            }
            AppConstants.BANK_TRANSACTION -> {
                isAddMoneyLayoutVisible.set(false)
                availableAmount.set(Utility.convertToRs(bankResponse.get()?.amount))
                val date = bankResponse.get()?.transactionDate?.split("+")

                when (bankResponse.get()?.transactionType) {
                    AppConstants.CREDITED -> {
                        paymentDateTime.set(
                            PockketApplication.instance.getString(R.string.received_text) +
                                    Utility.parseDateTime(
                                        date!![0],
                                        inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                                        outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT3
                                    )
                        )
                    }
                    AppConstants.DEBITED -> {
                        paymentDateTime.set(
                            PockketApplication.instance.getString(R.string.sent_text) +
                                    Utility.parseDateTime(
                                        date!![0],
                                        inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                                        outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT3
                                    )
                        )
                    }
                }

                fypId.set(bankResponse.get()?.accReferenceNumber)
                bankId.set(bankResponse.get()?.bankReferenceNumber)
                userName.set(bankResponse.get()?.userName)
                phoneNo.set(bankResponse.get()?.mobileNo)
            }

            AppConstants.TRANSACTION -> {
                isAddMoneyLayoutVisible.set(false)
                availableAmount.set(Utility.convertToRs(transactionHistoryResponse.get()?.txnAmount))

                val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val date = outputFormat.parse(transactionHistoryResponse.get()?.txnTime)
                val postFormater = SimpleDateFormat(AppConstants.CHANGED_DATE_TIME_FORMAT7)

                val newDateStr = postFormater.format(date)
                when (transactionHistoryResponse.get()?.isSender) {
                    AppConstants.YES -> {
                        paymentDateTime.set(
                            PockketApplication.instance.getString(R.string.sent_text) +
                                    newDateStr
                        )
                    }
                    AppConstants.NO -> {
                        paymentDateTime.set(
                            PockketApplication.instance.getString(R.string.received_text) +
                                    newDateStr
                        )
                    }
                }





                fypId.set(transactionHistoryResponse.get()?.accountTxnNo)
                bankId.set(transactionHistoryResponse.get()?.bankTxnId)
                userName.set(transactionHistoryResponse.get()?.destinationUserName)
                phoneNo.set(transactionHistoryResponse.get()?.destinationAccountIdentifier)
            }
        }

    }


}