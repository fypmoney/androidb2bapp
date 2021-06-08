package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
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
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility

class EnterAmountForPayRequestViewModel(application: Application) : BaseViewModel(application) {
    var contactName = ObservableField<String>()
    var onPayClicked = MutableLiveData(false)
    var setEdittextLength = MutableLiveData(false)
    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>()
    var action = ObservableField<String>()
    var message = ObservableField<String>()
    var contactResult = ObservableField(ContactEntity())
    var buttonText = ObservableField(application.getString(R.string.pay_btn_text))
    var onApiResponse = MutableLiveData<String>()
    var sendMoneyApiResponse = ObservableField<SendMoneyResponse>()

    /*
      * This method is used to handle on click of pay or request button
      * */
    fun onPayClicked() {
        when {
            TextUtils.isEmpty(amountSelected.get()) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
            }
            else -> {
                onPayClicked.value = true


            }
        }
    }

    fun onAmountSelected(amount: Int) {
        amountSelected.set(amount.toString())
        setEdittextLength.value = true
    }

    /*
   * This is used to set selected response
   * */
    fun setResponseAfterContactSelected(contactEntity: ContactEntity?, actionValue: String?) {
        try {
            if (contactEntity?.contactNumber != null) {
                contactResult.set(contactEntity)
                action.set(actionValue)

                if (action.get() != AppConstants.PAY) {
                    buttonText.set(PockketApplication.instance.getString(R.string.request_btn_text))
                }

                if (contactResult.get()?.lastName.isNullOrEmpty()) {
                    if (action.get() == AppConstants.PAY)
                        contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + contactEntity.firstName)
                    else {
                        contactName.set(PockketApplication.instance.getString(R.string.request_from) + " " + contactEntity.firstName)

                    }
                } else {
                    if (action.get() == AppConstants.PAY)
                        contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + contactEntity.firstName + " " + contactEntity.lastName)
                    else {
                        contactName.set(PockketApplication.instance.getString(R.string.request_from) + " " + contactEntity.firstName + " " + contactEntity.lastName)

                    }


                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*
     * This method is used to call send money api on click of pay button
     * */
    fun callSendMoneyApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FUND_TRANSFER,
                NetworkUtil.endURL(ApiConstant.API_FUND_TRANSFER),
                ApiUrl.POST,
                SendMoneyRequest(
                    mobileNo = contactResult.get()?.contactNumber,
                    txnType = AppConstants.FUND_TRANSFER_TRANSACTION_TYPE,
                    amount = amountSelected.get(),
                    remarks = message.get()
                ),
                this, isProgressBar = true
            )
        )
    }

    /*
   * This method is used to call send money api on click of pay button
   * */
    fun callRequestMoneyApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REQUEST_MONEY,
                NetworkUtil.endURL(ApiConstant.API_REQUEST_MONEY),
                ApiUrl.POST,
                RequestMoneyRequest(
                    requesteeMobile = contactResult.get()?.contactNumber,
                    amount = amountSelected.get(),
                    remarks = message.get(),emoji = ""
                ),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_FUND_TRANSFER -> {
                if (responseData is SendMoneyResponse) {
                    sendMoneyApiResponse.set(responseData)
                    onApiResponse.value = AppConstants.API_SUCCESS

                }
            }
            ApiConstant.API_REQUEST_MONEY -> {
                if (responseData is RequestMoneyResponse) {
                    onApiResponse.value = AppConstants.API_SUCCESS

                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_FUND_TRANSFER -> {
                onApiResponse.value = AppConstants.API_FAIL
            }
        }
    }

}