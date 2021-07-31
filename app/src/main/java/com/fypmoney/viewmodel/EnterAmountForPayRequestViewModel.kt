package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
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
    var buttonText = ObservableField(application.getString(R.string.request_btn_text))
    var onApiResponse = MutableLiveData<String>()
    var sendMoneyApiResponse = ObservableField<SendMoneyResponse>()
    private var qrCodeValue = ObservableField<String>()
    var isCircularImageVisible = ObservableField(false)

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
    fun setResponseAfterContactSelected(
        contactEntity: ContactEntity?,
        actionValue: String?,
        qrCode: String?
    ) {
        try {
            action.set(actionValue)
            qrCodeValue.set(qrCode)
            contactResult.set(contactEntity)

            if (contactResult.get()?.profilePicResourceId.isNullOrEmpty()) {
                isCircularImageVisible.set(false)
            }
            else
            {
                isCircularImageVisible.set(true)

            }
            if (action.get() == AppConstants.PAY || action.get() == AppConstants.PAY_USING_QR) {
                buttonText.set(PockketApplication.instance.getString(R.string.pay_btn_text))
            }

            if (contactResult.get()?.lastName.isNullOrEmpty()) {
                when {
                    action.get() == AppConstants.PAY -> contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + contactEntity?.firstName)
                    action.get() == AppConstants.PAY_USING_QR -> contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + qrCodeValue.get())
                    action.get() == AppConstants.REQUEST -> {
                        contactName.set(PockketApplication.instance.getString(R.string.request_from) + " " + contactEntity?.firstName)

                    }
                }
            } else {
                when {
                    action.get() == AppConstants.PAY -> contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + contactEntity?.firstName + " " + contactEntity?.lastName)
                    action.get() == AppConstants.PAY_USING_QR -> contactName.set(PockketApplication.instance.getString(R.string.pay_btn_text) + " " + qrCodeValue.get())
                    action.get() == AppConstants.REQUEST -> {
                        contactName.set(PockketApplication.instance.getString(R.string.request_from) + " " + contactEntity?.firstName + " " + contactEntity?.lastName)

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
                    amount = Utility.convertToPaise(amountSelected.get()!!),
                    remarks = message.get()
                ),
                this, isProgressBar = true
            )
        )
    }

    /*
  * This method is used to call send money api on click of pay button
  * */
    fun callSendMoneyUsingQrApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_QR_CODE_SCANNER,
                NetworkUtil.endURL(ApiConstant.API_QR_CODE_SCANNER),
                ApiUrl.POST,
                QrCodeScannerRequest(
                    txnType = AppConstants.FUND_TRANSFER_QR_CODE,
                    qr = qrCodeValue.get(),
                    amount = Utility.convertToPaise(amountSelected.get()!!),
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
                    amount = Utility.convertToPaise(amountSelected.get()!!),
                    remarks = message.get(), emoji = ""
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
                    Utility.showToast(responseData.msg)
                    sendMoneyApiResponse.set(responseData)
                    onApiResponse.value = AppConstants.API_SUCCESS

                }
            }
            ApiConstant.API_REQUEST_MONEY -> {
                if (responseData is RequestMoneyResponse) {
                    Utility.showToast(responseData.msg)
                    onApiResponse.value = AppConstants.API_SUCCESS

                }
            }
            ApiConstant.API_QR_CODE_SCANNER -> {
                if (responseData is QrCodeScannerResponse) {
                    Utility.showToast(PockketApplication.instance.getString(R.string.qr_transaction_success))
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