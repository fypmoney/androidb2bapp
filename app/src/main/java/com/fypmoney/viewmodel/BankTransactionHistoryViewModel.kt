package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.BankTransactionHistoryRequest
import com.fypmoney.model.BankTransactionHistoryResponse
import com.fypmoney.model.TransactionHistoryRequest
import com.fypmoney.model.TransactionHistoryResponse
import com.fypmoney.view.adapter.BankTransactionHistoryAdapter

class BankTransactionHistoryViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)
    var bankTransactionHistoryAdapter = BankTransactionHistoryAdapter(this)

    /*
      * This method is used to call get transaction history
      * */

    init {
        callGetBankTransactionHistoryApi()
    }

    /*
    * This method is used to bank transaction history
    * */
    private fun callGetBankTransactionHistoryApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_BANK_TRANSACTION_HISTORY,
                endpoint = NetworkUtil.endURL(ApiConstant.API_BANK_TRANSACTION_HISTORY),
                request_type = ApiUrl.POST,
                param = BankTransactionHistoryRequest(), onResponse = this,
                isProgressBar = true
            )
        )


    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_BANK_TRANSACTION_HISTORY -> {
                if (responseData is BankTransactionHistoryResponse) {
                    Log.d("kvssknvs",responseData.toString())
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