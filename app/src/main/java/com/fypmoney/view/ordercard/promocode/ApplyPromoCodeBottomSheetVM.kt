package com.fypmoney.view.ordercard.promocode

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant.API_CHECK_PROMO_CODE
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.ordercard.model.UserOfferCard
import com.fypmoney.view.ordercard.model.UserOfferCardResponse

class ApplyPromoCodeBottomSheetVM(application: Application):BaseViewModel(application) {

    val event: LiveData<ApplyPromoCodeEvent>
        get() = _event
    private val _event = LiveEvent<ApplyPromoCodeEvent>()
    val state: LiveData<ApplyPromoCodeState>
        get() = _state
    private val _state = MutableLiveData<ApplyPromoCodeState>()


    fun onApplyPromoCodeClicked(){
        _event.value = ApplyPromoCodeEvent.OnApplyClickedEvent
    }
    fun callCheckPromoCodeApi(promoCode:String){
        _state.value = ApplyPromoCodeState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = API_CHECK_PROMO_CODE,
                endpoint = NetworkUtil.endURL(API_CHECK_PROMO_CODE +"/${promoCode}"),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            API_CHECK_PROMO_CODE->{
                if(responseData is UserOfferCardResponse){
                    if(responseData.data.isNotEmpty()){
                        _state.value = ApplyPromoCodeState.PromoCodeAppliedSuccessfully(responseData.data[0])
                    }
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            API_CHECK_PROMO_CODE->{
                _state.value = ApplyPromoCodeState.InvalidPromoCode(errorResponseInfo)
            }
        }
    }
    sealed class ApplyPromoCodeEvent{
        object OnApplyClickedEvent:ApplyPromoCodeEvent()
    }
    sealed class ApplyPromoCodeState{
        data class PromoCodeAppliedSuccessfully(val offerCard: UserOfferCard):ApplyPromoCodeState()
        data class InvalidPromoCode(val errorResponseInfo: ErrorResponseInfo):ApplyPromoCodeState()
        object Loading:ApplyPromoCodeState()
    }
}