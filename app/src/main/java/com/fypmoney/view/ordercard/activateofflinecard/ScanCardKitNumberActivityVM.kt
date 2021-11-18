package com.fypmoney.view.ordercard.activateofflinecard

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.google.gson.Gson

class ScanCardKitNumberActivityVM(application: Application):BaseViewModel(application) {

    val event: LiveData<ScanCardKitNumberEvent>
        get() = _event
    private val _event = LiveEvent<ScanCardKitNumberEvent>()

    val state: LiveData<ScanCardKitNumberState>
        get() = _state
    private val _state = MutableLiveData<ScanCardKitNumberState>()
    val errorRecived = MutableLiveData<ErrorResponseInfo>()

    var isAlreadyCaptured = false


    fun verifyKitNumber() {
        _event.value = ScanCardKitNumberEvent.OnVerifyKitNumberClickedEvent
    }

    fun checkKitNumberIsValid(kitFourDigit: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_POST_CHECK_OFFLINE_CARD,
                NetworkUtil.endURL(ApiConstant.API_POST_CHECK_OFFLINE_CARD),
                ApiUrl.POST,
                CheckKitNumberRequest(
                    kitNumber = kitFourDigit
                ),
                this, isProgressBar = true
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
    fun callSetOrChangeApi(kitNumber:String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_SET_CHANGE_PIN,
                NetworkUtil.endURL(
                    ApiConstant.API_SET_CHANGE_PIN + kitNumber
                ),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_POST_CHECK_OFFLINE_CARD -> {
                val data = Gson().fromJson(responseData.toString(), OrderCardResponse::class.java)
                if (data is OrderCardResponse) {
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER,
                        data.orderCardResponseDetails?.kitNumber
                    )
                    isAlreadyCaptured = true
                    _state.value = ScanCardKitNumberState.KitNumberVerified
                }
            }
            ApiConstant.API_SET_CHANGE_PIN -> {
                if (responseData is SetPinResponse) {
                    trackr { it1->
                        it1.name = TrackrEvent.pin_success
                    }
                    _event.value = ScanCardKitNumberEvent.SetPinSuccess(responseData.setPinResponseDetails!!)
                }
            }
            ApiConstant.API_ACTIVATE_CARD -> {
                if (responseData is ActivateCardResponse) {
                    trackr { it1->
                        it1.name = TrackrEvent.card_activate_success
                    }
                    Utility.showToast(responseData.activateCardResponseDetails?.message)
                    _event.value = ScanCardKitNumberEvent.OnActivateCardSuccess
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_ACTIVATE_CARD -> {
                Utility.showToast(errorResponseInfo.msg)
                _event.value = ScanCardKitNumberEvent.OnActivateCardFaliure
            }
            ApiConstant.API_POST_CHECK_OFFLINE_CARD -> {

                errorRecived.postValue(errorResponseInfo)
            }

        }
    }
    sealed class ScanCardKitNumberEvent{
        object OnVerifyKitNumberClickedEvent:ScanCardKitNumberEvent()
        object OnActivateCardSuccess:ScanCardKitNumberEvent()
        object OnActivateCardFaliure:ScanCardKitNumberEvent()
        data class SetPinSuccess(val setpinResponseDetails: SetPinResponseDetails):ScanCardKitNumberEvent()

    }
    sealed class ScanCardKitNumberState{
        object KitNumberVerified:ScanCardKitNumberState()
    }
}