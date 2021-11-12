package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.gson.Gson

/*
* This class is used to pay u success response
* */
class CashbackSuccessViewModel(application: Application) : BaseViewModel(application) {
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var paymentDateTime = ObservableField<String>()
    var fypId = ObservableField<String>()
    var bankId = ObservableField<String>()
    var userName = ObservableField<String>()
    var fromWhichScreen = ObservableField<String>()
    var phoneNo = ObservableField<String>()
    var isAddMoneyLayoutVisible = ObservableField<Boolean>()
    var payUResponse = ObservableField<AddMoneyStep2ResponseDetails>()
    var cashbankResponse = ObservableField<BankTransactionHistoryResponseDetails>()
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

            AppConstants.BANK_TRANSACTION -> {
                isAddMoneyLayoutVisible.set(false)
                availableAmount.set(Utility.convertToRs(cashbankResponse.get()?.amount))
                val date = cashbankResponse.get()?.transactionDate?.split("+")

                when (cashbankResponse.get()?.transactionType) {
                    AppConstants.LOAD -> {
                        paymentDateTime.set(
                            PockketApplication.instance.getString(R.string.received_text) +
                                    Utility.parseDateTime(
                                        date!![0],
                                        inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                                        outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT9
                                    )
                        )
                    }
                }

                fypId.set(cashbankResponse.get()?.accReferenceNumber)
                bankId.set(cashbankResponse.get()?.bankReferenceNumber)
                userName.set(cashbankResponse.get()?.userName)
                phoneNo.set(cashbankResponse.get()?.mobileNo)
            }


        }

    }


    fun callCashbackEarned() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CASHBACK_EARNED,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CASHBACK_EARNED + "${cashbankResponse.get()?.mrn}/"),
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
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_REWARDS_EARNED + "${cashbankResponse.get()?.mrn}/"),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = ""
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_CASHBACK_EARNED -> {
                val data =
                    Gson().fromJson(responseData.toString(), CashbackEarnedResponse::class.java)
                cashbackEarnedData.set(
                    data.data.getAmountInRuppes()
                )
                if (data.data.amount != 0) {
                    cashbackEarnedError.value = true
                }

            }
            ApiConstant.API_GET_REWARDS_EARNED -> {
                val data =
                    Gson().fromJson(responseData.toString(), RewardsEarnedResponse::class.java)

                rewardEarnedData.set(
                    data.data.points
                )
                data.data.points?.let {
                    if (it != "0") {
                        rewardEarnedError.value = true
                    }
                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_GET_CASHBACK_EARNED -> {
                rewardEarnedError.value = false

            }
            ApiConstant.API_GET_REWARDS_EARNED -> {
                rewardEarnedError.value = false

            }
        }
    }


}