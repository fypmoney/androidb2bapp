package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
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
import com.fypmoney.view.adapter.TransactionHistoryAdapter

class TransactionHistoryViewModel(application: Application) : BaseViewModel(application) {
    var onPayOrRequestClicked = MutableLiveData<View>()
    var contactResult = ObservableField(ContactEntity())
    var transactionHistoryAdapter = TransactionHistoryAdapter(this)
    var contactName = ObservableField<String>()

    var LoadedList: MutableLiveData<List<TransactionHistoryResponseDetails>> = MutableLiveData()


    var isNoDataFoundVisible = ObservableField(false)
    var onItemClicked = MutableLiveData<TransactionHistoryResponseDetails>()


    init {
    }
    /*
  * This is used to handle pay or request button click
  * */

    fun onPayOrRequestClicked(view: View) {
        onPayOrRequestClicked.value = view

    }
    /*
      * This method is used to call get transaction history
      * */

    fun callGetTransactionHistoryApi(page: Int) {
        Log.d("chackpaginat", "req")
        var progressbar = false
        progressbar = page == 0

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_TRANSACTION_HISTORY,
                endpoint = NetworkUtil.endURL(ApiConstant.API_TRANSACTION_HISTORY),
                request_type = ApiUrl.POST,//TODO Remove country code from model
                param = if (contactResult.get()?.contactNumber?.length!! > 10) {
                    TransactionHistoryRequestwithPage(
                        destinationUserId = contactResult.get()?.contactNumber?.substring(
                            3,
                            contactResult.get()?.contactNumber?.length!!
                        ),
                        page = page
                    )

                } else {
                    TransactionHistoryRequestwithPage(
                        destinationUserId = contactResult.get()?.contactNumber, page = page
                    )

                }, onResponse = this,
                isProgressBar = progressbar
            )
        )


    }

    /*
     * This is used to set selected response
     * */
    fun setResponseAfterContactSelected(contactEntity: ContactEntity?) {
        try {
            if (contactEntity?.contactNumber != null) {
                contactResult.set(contactEntity)
                if (contactResult.get()?.lastName.isNullOrEmpty()) {
                    contactName.set(contactEntity.firstName)
                } else {
                    contactName.set(contactEntity.firstName + " " + contactEntity.lastName)
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_TRANSACTION_HISTORY -> {

                if (responseData is TransactionHistoryResponse) {
                    LoadedList.postValue(responseData.transactionHistoryResponseDetails)
//                    if (!responseData.transactionHistoryResponseDetails.isNullOrEmpty()){
//
//
//
//                    }


                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}