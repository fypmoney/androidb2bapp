package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BankTransactionHistoryRequestwithpage
import com.fypmoney.model.BankTransactionHistoryResponse
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.view.adapter.BankTransactionHistoryAdapter

class BankTransactionHistoryViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)
    var bankTransactionHistoryAdapter = BankTransactionHistoryAdapter(this)
    var onItemClicked = MutableLiveData<BankTransactionHistoryResponseDetails>()
    var LoadedList: MutableLiveData<List<BankTransactionHistoryResponseDetails>> = MutableLiveData()
    var invalidRequest = MutableLiveData<Boolean>()
    /*
      * This method is used to call get transaction history
      * */

    init {
        callGetBankTransactionHistoryApi(page = 1)
    }

    /*
    * This method is used to bank transaction history
    * */
    fun callGetBankTransactionHistoryApi(
        fromDate: String? = null,
        toDate: String? = null,
        page: Int? = null
    ) {
        var progressbar = false
        progressbar = page == 1

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_BANK_TRANSACTION_HISTORY,
                endpoint = NetworkUtil.endURL(ApiConstant.API_BANK_TRANSACTION_HISTORY),
                request_type = ApiUrl.POST,
                param = BankTransactionHistoryRequestwithpage(
                    startDate = fromDate,
                    endDate = toDate,
                    page = page
                ),
                onResponse = this,
                isProgressBar = progressbar
            )
        )


    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_BANK_TRANSACTION_HISTORY -> {
                if (responseData is BankTransactionHistoryResponse) {
                    LoadedList.postValue(responseData.transactions.bankTransactionHistoryResponseDetails)
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_BANK_TRANSACTION_HISTORY -> {
                if(errorResponseInfo.errorCode == "PKT_1039"){
                    invalidRequest.value = true
                }
            }
        }
    }


}