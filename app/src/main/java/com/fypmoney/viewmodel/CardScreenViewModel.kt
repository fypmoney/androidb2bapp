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
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.KycActivateAccountResponse

class CardScreenViewModel(application: Application) : BaseViewModel(application) {
    var isCardDetailsVisible =
        ObservableField(false)
    var balance =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var name =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_name))
    var cardNumber =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_card))
    var cvv =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_cvv))
    var expiry =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_expiry))
    var onViewDetailsClicked = MutableLiveData<Boolean>()

    init {
        callKycAccountActivationApi()
    }

    /*
    * This is used to see the card details
    * */
    fun onViewDetailsClicked() {
        onViewDetailsClicked.value = true
    }

    /*
      * This method is used to call auth login API
      * */
    private fun callKycAccountActivationApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_ACTIVATE_ACCOUNT,
                NetworkUtil.endURL(ApiConstant.API_KYC_ACTIVATE_ACCOUNT),
                ApiUrl.PUT,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
     * This method is used to call get virtual card details
     * */
    private fun callGetVirtualCardDetailsApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_VIRTUAL_CARD_REQUEST,
                NetworkUtil.endURL(ApiConstant.API_GET_VIRTUAL_CARD_REQUEST),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_VIRTUAL_CARD_REQUEST -> {
                if (responseData is KycActivateAccountResponse) {

                }
            }

            ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                if (responseData is KycActivateAccountResponse) {
                    callGetVirtualCardDetailsApi()

                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

}