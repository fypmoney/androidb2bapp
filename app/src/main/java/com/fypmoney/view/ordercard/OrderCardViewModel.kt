package com.fypmoney.view.ordercard

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.ordercard.model.UserOfferCard
import com.fypmoney.view.ordercard.model.UserOfferCardResponse
import com.google.gson.Gson


class OrderCardViewModel(application: Application) : BaseViewModel(application) {

    val event: LiveData<OrderCardEvent>
        get() = _event
    private val _event = LiveEvent<OrderCardEvent>()


    val state:LiveData<OrderCardState>
        get() = _state
    private val _state = MutableLiveData<OrderCardState>()
    var userOfferCard: UserOfferCard? = null
    init {
        getCardOfferDetails()
    }

    fun onOrderCardClicked() {
        _event.value = OrderCardEvent.GetOrderCardEvent
    }
    fun onAlreadyHaveACard() {
        _event.value = OrderCardEvent.AlreadyHaveACardEvent
    }
    fun onPromoCodeClicked() {
        _event.value = OrderCardEvent.HaveAPromoCode
    }


    private fun getCardOfferDetails() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_USER_OFFER_CARD,
                NetworkUtil.endURL(ApiConstant.API_USER_OFFER_CARD),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_USER_OFFER_CARD->{
                val data = Gson().fromJson(responseData.toString(), UserOfferCardResponse::class.java)
                if(data is UserOfferCardResponse){
                    if(!data.data.isNullOrEmpty()){
                        userOfferCard = data.data[0]
                        _state.value = OrderCardState.Success(userOfferCard!!)
                    }
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_USER_OFFER_CARD->{
                _state.value = OrderCardState.Error(errorResponseInfo)
            }
        }
    }
    sealed class OrderCardEvent {
        object GetOrderCardEvent : OrderCardEvent()
        object AlreadyHaveACardEvent : OrderCardEvent()
        object HaveAPromoCode : OrderCardEvent()
    }

    sealed class OrderCardState{
        data class Success(var userOfferCard: UserOfferCard):OrderCardState()
        data class Error(var error: ErrorResponseInfo):OrderCardState()
    }
}