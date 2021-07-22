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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import org.json.JSONObject


class CardScreenViewModel(application: Application) : BaseViewModel(application) {
    var isCardDetailsVisible =
        ObservableField(false)
    var balance =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
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
    var onActivateCardClicked = MutableLiveData<Boolean>()
    var onSetPinSuccess = MutableLiveData<SetPinResponse>()
    var isActivateCardVisible = ObservableField(true)
    var onBankProfileSuccess = MutableLiveData(false)
    var isOrderCard = ObservableField(true)
    var bankProfileResponse = ObservableField<BankProfileResponseDetails>()

    init {
        callGetBankProfileApi()
    }

    /*
    * This is used to see the card details
    * */
    fun onViewDetailsClicked() {
        onViewDetailsClicked.value = true
    }

    /*
    * This method is used to activate card
    * */
    fun onActivateCardClicked() {
        onActivateCardClicked.value = true
    }

    /*
        * This method is used to get the balance of wallet
        * */
    fun callGetWalletBalanceApi() {
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
      * This method is used to set or change pin
      * */
    fun callSetOrChangeApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_SET_CHANGE_PIN,
                NetworkUtil.endURL(
                    ApiConstant.API_SET_CHANGE_PIN + SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER
                    )
                ),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    /*
     * This method is used to call physical card init api
     * */
    fun callPhysicalCardInitApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_PHYSICAL_CARD_INIT,
                NetworkUtil.endURL(ApiConstant.API_PHYSICAL_CARD_INIT),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    /*
      * This method is used to call add card
      * */
    private fun callAddCardApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ADD_CARD,
                NetworkUtil.endURL(ApiConstant.API_ADD_CARD),
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

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
    fun callActivateCardInitApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ACTIVATE_CARD_INIT,
                NetworkUtil.endURL(ApiConstant.API_ACTIVATE_CARD_INIT),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    /*
        * This method is used to call update card limit api
        * */
    fun callUpdateCardLimitApi(updateCardLimitRequest: UpdateCardLimitRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_UPDATE_CARD_LIMIT,
                NetworkUtil.endURL(ApiConstant.API_UPDATE_CARD_LIMIT),
                ApiUrl.PUT,
                updateCardLimitRequest,
                this, isProgressBar = true
            )
        )
    }

    /*
     * This method is used to call get virtual card request
     * */
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
     * This method is used to call get virtual card details
     * */
    private fun callGetVirtualCardDetailsApi(fetchVirtualCardRequest: FetchVirtualCardRequest) {
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

    /*
 * This method is used to set bank profile
 * */
    fun callGetBankProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_BANK_PROFILE,
                NetworkUtil.endURL(ApiConstant.API_GET_BANK_PROFILE),
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
                if (responseData is VirtualCardRequestResponse) {
                    callGetVirtualCardDetailsApi(makeFetchCardRequest(responseData.virtualCardRequestResponseDetails.requestData))
                }
            }

            ApiConstant.API_FETCH_VIRTUAL_CARD_DETAILS -> {
                if (responseData is FetchVirtualCardResponse) {
                    cardNumber.set(responseData.fetchVirtualCardResponseDetails.card_number)
                    cvv.set(responseData.fetchVirtualCardResponseDetails.cvv)
                    expiry.set(responseData.fetchVirtualCardResponseDetails.expiry_month + "/" + responseData.fetchVirtualCardResponseDetails.expiry_year)
                    onGetCardDetailsSuccess.value = true
                    callAddCardApi()

                }
            }
            ApiConstant.API_UPDATE_CARD_LIMIT -> {
                progressDialog.value = false
                if (responseData is UpdateCardLimitResponse) {
                    callGetBankProfileApi()
                    Utility.showToast(responseData.msg)
                }

            }

            ApiConstant.API_UPDATE_CARD_SETTINGS -> {
                progressDialog.value = false
                if (responseData is UpdateCardSettingsResponse) {
                }

            }
            ApiConstant.API_GET_BANK_PROFILE -> {
                if (responseData is BankProfileResponse) {
                    bankProfileResponse.set(responseData.bankProfileResponseDetails)
                    bankProfileResponse.get()?.cardInfos?.forEach {
                        when (it.cardType) {
                            AppConstants.CARD_TYPE_PHYSICAL -> {
                                when {
                                    !it.kitNumber.isNullOrEmpty() -> {
                                        isOrderCard.set(false)
                                    }
                                    it.status == AppConstants.ENABLE -> {
                                        isActivateCardVisible.set(false)
                                    }
                                }


                            }
                        }

                    }
                    onBankProfileSuccess.value = true

                }

            }

            ApiConstant.API_ACTIVATE_CARD_INIT -> {
                progressDialog.value = false
                if (responseData is ActivateCardInitResponse) {
                    when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                            onActivateCardInit.value = true
                        }
                    }
                }

            }
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    isFetchBalanceVisible.set(false)
                    balance.set(Utility.getFormatedAmount(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance)!!))

                }
            }
            ApiConstant.API_SET_CHANGE_PIN -> {
                if (responseData is SetPinResponse) {
                    onSetPinSuccess.value = responseData
                }
            }
        }
    }

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