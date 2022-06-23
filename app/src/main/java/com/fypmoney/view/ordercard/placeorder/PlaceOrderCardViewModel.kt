package com.fypmoney.view.ordercard.placeorder

import android.app.Application
import androidx.lifecycle.LiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.OrderCardRequest
import com.fypmoney.model.OrderCardResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.ordercard.model.PinCodeData
import com.fypmoney.view.ordercard.model.PinCodeResponse
import com.fypmoney.view.ordercard.model.UserOfferCard
import com.google.gson.Gson

/*
* This is used for card ordering
* */
class PlaceOrderCardViewModel(application: Application) : BaseViewModel(application) {
    var userOfferCard: UserOfferCard? = null
    var placeOrderRequest: OrderCardRequest? = OrderCardRequest()

    val event:LiveData<PlaceOrderCardEvent>
        get() = _event
    private val _event = LiveEvent<PlaceOrderCardEvent>()

    val state:LiveData<PlaceOrderCardState>
        get() = _state
    private val _state = LiveEvent<PlaceOrderCardState>()



    fun mapRequestOrderObject() {
        placeOrderRequest?.productId = userOfferCard?.id
        placeOrderRequest?.paymentMode = "WALLET"
        placeOrderRequest?.amount = userOfferCard?.mrp
        placeOrderRequest?.productMasterCode = userOfferCard?.code
        placeOrderRequest?.taxMasterCode = userOfferCard?.taxMasterCode
        placeOrderRequest?.totalTax = userOfferCard?.totalTax.toString()
        placeOrderRequest?.discount = userOfferCard?.discount.toString()
    }


    /*
    * This method is used to place the order
    * */
    fun onPriceBreakupClicked() {
        _event.value = PlaceOrderCardEvent.OnViewPriceBreakUp
    }

    /*
    * This method is used to place the order
    * */
    fun onPlaceOrderClicked() {
        _event.value = PlaceOrderCardEvent.OnPlaceOrder
    }


     fun getPinCode(pinCode:String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_PIN_CODE,
                NetworkUtil.endURL(ApiConstant.API_PIN_CODE+"${pinCode}"),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    fun placeOrderCard() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ORDER_CARD,
                NetworkUtil.endURL(ApiConstant.API_ORDER_CARD),
                ApiUrl.POST,
                placeOrderRequest!!,
                this, isProgressBar = true
            )
        )
    }








    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ORDER_CARD -> {
                if (responseData is OrderCardResponse) {
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_KIT_NUMBER,
                        responseData.orderCardResponseDetails?.kitNumber
                    )
                    _state.value = PlaceOrderCardState.PlaceOrderSuccess(responseData)
                }
            }
            ApiConstant.API_PIN_CODE -> {
                val data = Gson().fromJson(responseData.toString(), PinCodeResponse::class.java)
                if (data is PinCodeResponse) {
                    placeOrderRequest?.pincode = data.data.pincode
                    placeOrderRequest?.stateCode = data.data.stateCode
                    placeOrderRequest?.city = data.data.city
                    placeOrderRequest?.state = data.data.state
                    _state.value = PlaceOrderCardState.PinCodeSuccess(data.data)

                }
            }


        }


    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_PIN_CODE -> {
                if(errorResponseInfo.errorCode=="LOY_5127"){
                    _event.value = PlaceOrderCardEvent.NotServicingInThisArea
                }
            }
            ApiConstant.API_ORDER_CARD -> {
                if(errorResponseInfo.errorCode== AppConstants.INSUFFICIENT_ERROR_CODE){
                    trackr {
                        it.name = TrackrEvent.insufficient_balance
                    }
                    _event.value = PlaceOrderCardEvent.InSufficientBalance(errorResponseInfo.data)
                }else{
                    _state.value= PlaceOrderCardState.PlaceOrderError
                }
            }
        }
    }

    sealed class PlaceOrderCardEvent{
        object OnViewPriceBreakUp: PlaceOrderCardEvent()
        object NotServicingInThisArea: PlaceOrderCardEvent()
        data class InSufficientBalance(val amount:String?): PlaceOrderCardEvent()
        object OnPlaceOrder: PlaceOrderCardEvent()
    }
    sealed class PlaceOrderCardState{
        data class PinCodeSuccess(val pinCode: PinCodeData): PlaceOrderCardState()
        data class PlaceOrderSuccess(val orderCardResponse: OrderCardResponse): PlaceOrderCardState()
        object PlaceOrderError: PlaceOrderCardState()
    }

}