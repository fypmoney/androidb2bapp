package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.*
import com.fypmoney.view.adapter.BankTransactionHistoryAdapter

class BankTransactionHistoryViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)
    var bankTransactionHistoryAdapter = BankTransactionHistoryAdapter(this)
    var onItemClicked = MutableLiveData<BankTransactionHistoryResponseDetails>()

    /*
      * This method is used to call get transaction history
      * */

    init {
        callGetBankTransactionHistoryApi()
    }

    /*
    * This method is used to bank transaction history
    * */
    fun callGetBankTransactionHistoryApi(fromDate: String? = null, toDate: String? = null) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_BANK_TRANSACTION_HISTORY,
                endpoint = NetworkUtil.endURL(ApiConstant.API_BANK_TRANSACTION_HISTORY),
                request_type = ApiUrl.POST,
                param = BankTransactionHistoryRequest(startDate = fromDate, endDate = toDate),
                onResponse = this,
                isProgressBar = true
            )
        )


    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_BANK_TRANSACTION_HISTORY -> {
                if (responseData is BankTransactionHistoryResponse) {
                    if (!responseData.transactions.bankTransactionHistoryResponseDetails.isNullOrEmpty())
                        bankTransactionHistoryAdapter.setList(responseData.transactions.bankTransactionHistoryResponseDetails)
                    else
                        noDataFoundVisibility.set(true)
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}