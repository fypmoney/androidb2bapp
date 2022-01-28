package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
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
    var cvvNumber: String? = "***"
    var balance =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var isFetchBalanceVisible = ObservableField(true)
    var isCvvVisible = ObservableField(false)
    var isFrontVisible = ObservableField(true)
    var isBackVisible = ObservableField(false)
    var name =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_name))
    var cardNumber =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_card))
    var cardNumber1 =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_star))
    var cardNumber2 =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_star))
    var cardNumber3 =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_star))
    var cardNumber4 =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_star))
    var cvv =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_cvv))
    var expiry =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_expiry))
    var onViewDetailsClicked = MutableLiveData<Boolean>()
    var onGetCardDetailsSuccess = MutableLiveData<Boolean>()
    var onActivateCardInit = MutableLiveData<Boolean>()
    var onSetPinSuccess = MutableLiveData<SetPinResponseDetails>()
    var onBankProfileSuccess = MutableLiveData<Boolean>()
    var isViewDetailsVisible = ObservableField(true)
    var bankProfileResponse = ObservableField<BankProfileResponseDetails>()
    var rotateCardClicked = MutableLiveData<Boolean>()
    var fetchingBankDetails = MutableLiveData<Boolean>()

    /*
    * This is used to see the card details
    * */
    fun onViewDetailsClicked() {
        onViewDetailsClicked.value = true
    }

    fun onRotateCard(){
        rotateCardClicked.value = true
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
     * This method is used to call activate card api
     * */
    fun callActivateCardApi(kitFourDigit: String?) {
        val additionalInfo = System.currentTimeMillis().toString()
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ACTIVATE_CARD,
                NetworkUtil.endURL(ApiConstant.API_ACTIVATE_CARD),
                ApiUrl.POST,
                ActivateCardRequest(
                    validationNo = kitFourDigit,
                    additionalInfo = additionalInfo,
                    cardIdentifier = SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER
                    )
                ),
                this, isProgressBar = true
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
        fetchingBankDetails.postValue(true)
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
                this, isProgressBar = true
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
                    setCardNumber()
                    isViewDetailsVisible.set(false)
                    cvvNumber = responseData.fetchVirtualCardResponseDetails.cvv
                    expiry.set(responseData.fetchVirtualCardResponseDetails.expiry_month + "/" + responseData.fetchVirtualCardResponseDetails.expiry_year)
                    onGetCardDetailsSuccess.value = true

                }
            }
            ApiConstant.API_UPDATE_CARD_LIMIT -> {
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
                                SharedPrefUtils.putString(
                                    getApplication(),
                                    SharedPrefUtils.SF_KEY_KIT_NUMBER, it.kitNumber
                                )}
                        }

                    }
                    onBankProfileSuccess.value = true

                }

            }

            /*ApiConstant.API_ACTIVATE_CARD_INIT -> {
                progressDialog.value = false
                if (responseData is ActivateCardInitResponse) {
                    *//*when (responseData.msg) {
                        ApiConstant.API_SUCCESS -> {
                        }
                    }*//*
                }

            }*/
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    isFetchBalanceVisible.set(false)
                    balance.set(Utility.getFormatedAmount(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance)!!))

                }
            }
            ApiConstant.API_SET_CHANGE_PIN -> {
                if (responseData is SetPinResponse) {
                    trackr { it1->
                        it1.name = TrackrEvent.pin_success
                    }
                    onSetPinSuccess.value = responseData.setPinResponseDetails!!
                }
            }
            ApiConstant.API_ACTIVATE_CARD -> {
                if (responseData is ActivateCardResponse) {
                    trackr { it1->
                        it1.name = TrackrEvent.card_activate_success
                    }
                    callGetBankProfileApi()
                    Utility.showToast(responseData.activateCardResponseDetails?.message)
                    onActivateCardInit.value = true
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_ACTIVATE_CARD -> {
                Utility.showToast(errorResponseInfo.msg)
            }
        }
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

    /*
    * this is used to set the data in card numbers
    * */
    fun setCardNumber() {
        cardNumber1.set(cardNumber.get()?.substring(0, 4))
        cardNumber2.set(cardNumber.get()?.substring(5, 9))
        cardNumber3.set(cardNumber.get()?.substring(10, 14))
        cardNumber4.set(cardNumber.get()?.substring(15, 19))

    }

}