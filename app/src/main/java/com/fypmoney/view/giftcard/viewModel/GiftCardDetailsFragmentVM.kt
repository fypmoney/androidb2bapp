package com.fypmoney.view.giftcard.viewModel

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
import com.fypmoney.util.HOW_TO_REDEEM_LINK_EXCEPTION
import com.fypmoney.util.HOW_TO_REDEEM_TXT_EXCEPTION
import com.fypmoney.view.giftcard.model.GiftCardDetail
import com.fypmoney.view.giftcard.model.GiftCardDetailsNetworkResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics

class GiftCardDetailsFragmentVM(application: Application) : BaseViewModel(application) {

    lateinit var giftCardId:String
    lateinit var giftCardDetails: GiftCardDetail
    val state:LiveData<GiftCardDetailsState>
        get() = _state
    private val _state = MutableLiveData<GiftCardDetailsState>()

    val event:LiveData<GiftCardDetailsEvent>
        get() = _event
    private val _event = MutableLiveData<GiftCardDetailsEvent>()

    fun playNowClick(){
        _event.value = GiftCardDetailsEvent.NavigateToArcade
    }

    fun onVoucherValueCopyClicked(){
        _event.value = GiftCardDetailsEvent.VoucherValueCopyEvent
    }
    fun onVoucherPinCopyClicked(){
        _event.value = GiftCardDetailsEvent.VoucherValuePinEvent

    }
    fun redeemNowClick(){
        giftCardDetails.howToRedeem?.let { howToRedeem->
                giftCardDetails.redeemLink?.let { redeemLink->
                _event.value = GiftCardDetailsEvent.RedeemNow(howToRedeem,redeemLink)
            }?: kotlin.run {
                    FirebaseCrashlytics.getInstance().recordException(Throwable(HOW_TO_REDEEM_LINK_EXCEPTION))
                }
        }?: kotlin.run {
                FirebaseCrashlytics.getInstance().recordException(Throwable(HOW_TO_REDEEM_TXT_EXCEPTION))
            giftCardDetails.redeemLink?.let { redeemLink->
                _event.value = GiftCardDetailsEvent.RedeemNowWithoutSteps(redeemLink)
            }?: kotlin.run {
                FirebaseCrashlytics.getInstance().recordException(Throwable(HOW_TO_REDEEM_LINK_EXCEPTION))
            }
        }
    }

    fun onShareClick(){
        _event.value = GiftCardDetailsEvent.ShareGiftCardDetails(giftCardDetails)
    }

    fun getGiftCardDetails() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GIFT_CARD_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_GIFT_CARD_DETAILS+giftCardId),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_GIFT_CARD_DETAILS->{
                if(responseData is GiftCardDetailsNetworkResponse){
                    giftCardDetails = responseData.giftCardDetail
                    _state.value = GiftCardDetailsState.Success(giftCardDetails)
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_GIFT_CARD_DETAILS->{
                _state.value = GiftCardDetailsState.Error(errorResponseInfo)
            }
        }
    }

    sealed class GiftCardDetailsState{
        object Loading:GiftCardDetailsState()
        data class Error(var errorResponseInfo: ErrorResponseInfo):GiftCardDetailsState()
        data class Success(var giftCardDetails: GiftCardDetail):GiftCardDetailsState()
    }
    sealed class GiftCardDetailsEvent{
        object NavigateToArcade:GiftCardDetailsEvent()
        object NavigateToHomeScreen:GiftCardDetailsEvent()
        object VoucherValueCopyEvent:GiftCardDetailsEvent()
        object VoucherValuePinEvent:GiftCardDetailsEvent()
        data class ShareGiftCardDetails(var giftCardDetails:GiftCardDetail):GiftCardDetailsEvent()
        data class RedeemNow(var redeemNowTxt:String,var redeemLink:String):GiftCardDetailsEvent()
        data class RedeemNowWithoutSteps(var redeemLink:String):GiftCardDetailsEvent()
    }
}