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
import com.fypmoney.util.Utility
import org.json.JSONObject


class CardDetailsViewModel(application: Application) : BaseViewModel(application) {
    var isCardDetailsVisible =
        ObservableField(false)
    var balance =
        MutableLiveData<String>()
    var isFetchBalanceVisible = ObservableField(true)
    var isCvvVisible = ObservableField(false)
    var name =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_name))

    var cardNumber =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_card))
    var cvv =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_cvv))
    var expiry =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_expiry))
    var onViewDetailsClicked = MutableLiveData<Boolean>()
    var onGetCardDetailsSuccess = MutableLiveData<Boolean>()
    var onActivateCardInit = MutableLiveData<Boolean>()
    var bankProfileResponse = ObservableField<BankProfileResponseDetails>()
    var availableAmount =
        MutableLiveData<String>()
    var carddetails =
        MutableLiveData<CardInfoDetailsBottomSheet>()
    init {
        callGetWalletBalanceApi()
        callGetVirtualRequestApi()

    }

    /*
    * This is used to see the card details
    * */
    fun onViewDetailsClicked() {
        onViewDetailsClicked.value = true
    }

    /*
        * This method is used to get the balance of wallet
        * */
    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    isFetchBalanceVisible.set(false)
                    availableAmount.postValue(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance)?.let {
                        Utility.getFormatedAmount(it)
                    })

                }
            }
            ApiConstant.API_GET_VIRTUAL_CARD_REQUEST -> {
                if (responseData is VirtualCardRequestResponse) {
                    callGetVirtualCardDetailsApi(makeFetchCardRequest(responseData.virtualCardRequestResponseDetails.requestData))
                }
            }
            ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS -> {
                if (responseData is FetchVirtualCardResponse) {
                    var cardInfoDetails= CardInfoDetailsBottomSheet(responseData.fetchVirtualCardResponseDetails.card_number,

                        responseData.fetchVirtualCardResponseDetails.cvv,responseData.fetchVirtualCardResponseDetails.expiry_month
                               , responseData.fetchVirtualCardResponseDetails.expiry_year)
                    carddetails.postValue(cardInfoDetails)
                    cardNumber.set(responseData.fetchVirtualCardResponseDetails.card_number)
                    cvv.set(responseData.fetchVirtualCardResponseDetails.cvv)
                    expiry.set(responseData.fetchVirtualCardResponseDetails.expiry_month + "/" + responseData.fetchVirtualCardResponseDetails.expiry_year)
                    onGetCardDetailsSuccess.value = true

                }
            }
        }
    }
    private fun callGetWalletBalanceApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
      * This method is used to call add card
      * */


    /*
        * This method is used to call update card settings
        * */
    fun callCardSettingsUpdateApi(upDateCardSettingsRequest: UpDateCardSettingsRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_UPDATE_CARD_SETTINGS,
                NetworkUtil.endURL(ApiConstant.API_UPDATE_CARD_SETTINGS),
                ApiUrl.PUT,
                upDateCardSettingsRequest,
                this, isProgressBar = true
            )
        )
    }

    /*
      * This method is used to call activate card init api
      * */


    /*
     * This method is used to call get virtual card details
     * */
     fun callGetVirtualCardDetailsApi(fetchVirtualCardRequest: FetchVirtualCardRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS),
                ApiUrl.POST,
                fetchVirtualCardRequest,
                this, isProgressBar = false
            )
        )
    }
    fun callGetVirtualRequestApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_VIRTUAL_CARD_REQUEST,
                NetworkUtil.endURL(ApiConstant.API_GET_VIRTUAL_CARD_REQUEST),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }
    /*
 * This method is used to set bank profile
 * */



    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    fun makeFetchCardRequest(requestData: String): FetchVirtualCardRequest {
        val fetchVirtualCardRequest = FetchVirtualCardRequest()
        val mainObject = JSONObject(requestData)
        fetchVirtualCardRequest.action_name = mainObject.getString("action_name")
        fetchVirtualCardRequest.wlap_secret_key = mainObject.getString("wlap_secret_key")
        fetchVirtualCardRequest.wlap_code = mainObject.getString("wlap_code")
        fetchVirtualCardRequest.p1 = mainObject.getString("p1")
        fetchVirtualCardRequest.p2 = mainObject.getString("p2")
        fetchVirtualCardRequest.checksum = mainObject.getString("checksum")
        return fetchVirtualCardRequest


    }

}