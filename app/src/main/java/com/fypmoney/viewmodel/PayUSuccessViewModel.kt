package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.BuildConfig
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.model.checkappupdate.CheckAppUpdateResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.gson.Gson
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
    var cashbackEarnedData = ObservableField<String>()
    var rewardEarnedData = ObservableField<String>()
    var cashbackEarnedError = MutableLiveData<Boolean>()
    var rewardEarnedError = MutableLiveData<Boolean>()
    /*
    * This is used to set the initial parameters
    * */
    init {
    }
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
                                        outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT9
                                    )
                        )
                    }
                    AppConstants.DEBITED -> {
                        paymentDateTime.set(
                            PockketApplication.instance.getString(R.string.sent_text) +
                                    Utility.parseDateTime(
                                        date!![0],
                                        inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT2,
                                        outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT9
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


    fun callCashbackEarned() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CASHBACK_EARNED,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CASHBACK_EARNED+"${bankResponse.get()?.mrn}/"),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = ""
            )
        )
    }
    fun callRewardsEarned() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_REWARDS_EARNED,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_REWARDS_EARNED+"${bankResponse.get()?.mrn}/"),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = ""
            )
        )
    }
    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_GET_CASHBACK_EARNED->{
                val data = Gson().fromJson(responseData.toString(), CashbackEarnedResponse::class.java)
                cashbackEarnedData.set(
                    data.data.getAmountInRuppes()
                )
                if(data.data.amount!=0){
                    cashbackEarnedError.value = true
                }

            }
            ApiConstant.API_GET_REWARDS_EARNED->{
                val data = Gson().fromJson(responseData.toString(), RewardsEarnedResponse::class.java)

                rewardEarnedData.set(
                    data.data.points
                )
                data.data.points?.let {
                    if(it != "0"){
                        rewardEarnedError.value = true
                    }
                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_GET_CASHBACK_EARNED->{
                rewardEarnedError.value = false

            }
            ApiConstant.API_GET_REWARDS_EARNED->{
                rewardEarnedError.value = false

            }
        }
    }


}