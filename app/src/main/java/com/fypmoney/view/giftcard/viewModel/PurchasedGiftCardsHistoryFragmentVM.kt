package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiConstant.GET_GIFT_VOUCHER_STATUS
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.giftcard.model.GiftCardHistoryItem
import com.fypmoney.view.giftcard.model.GiftCardHistoryListNetworkResponse
import com.fypmoney.view.giftcard.model.GiftCardStatusNetworkResponse

class PurchasedGiftCardsHistoryFragmentVM(application: Application) : BaseViewModel(application) {

    val state:LiveData<PurchasedGiftCardsHistoryState>
        get() = _state
    private val _state = MutableLiveData<PurchasedGiftCardsHistoryState>()

    val event:LiveData<PurchasedGiftCardsHistoryEvent>
        get() = _event
    private val _event = LiveEvent<PurchasedGiftCardsHistoryEvent>()

    private var purchasedGiftCardsList = listOf<GiftCardHistoryItem>()

    fun onGiftCardClick(giftCardDetailId:String){
        _event.value = PurchasedGiftCardsHistoryEvent.NavigateToGiftCardDetail(giftCardDetailId)
    }

    fun callGiftCardHistory(){
        _state.postValue(PurchasedGiftCardsHistoryState.Loading)
        //TODO Implement pagination later
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.GET_HISTORY_LIST,
                NetworkUtil.endURL(ApiConstant.GET_HISTORY_LIST),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }
    fun callVoucherStatus(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = GET_GIFT_VOUCHER_STATUS,
                endpoint = NetworkUtil.endURL(GET_GIFT_VOUCHER_STATUS) + id,
                request_type = ApiUrl.GET,
                param = BaseRequest(), onResponse = this,
                isProgressBar = true
            )

        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.GET_HISTORY_LIST->{
                if(responseData is GiftCardHistoryListNetworkResponse){
                    if(responseData.data.isNullOrEmpty()){
                        _state.value = PurchasedGiftCardsHistoryState.Empty
                    }else{
                        responseData.data?.let {
                            purchasedGiftCardsList = it
                        }
                        _state.value = PurchasedGiftCardsHistoryState.Success(purchasedGiftCardsList)
                    }
                }
            }
            GET_GIFT_VOUCHER_STATUS->{
                if(responseData is GiftCardStatusNetworkResponse){
                    purchasedGiftCardsList = purchasedGiftCardsList.mapIndexed { index, giftCardHistoryItem ->
                        if(giftCardHistoryItem.id==responseData.data?.id){
                            GiftCardHistoryItem.updateObject(responseData.data!!)
                        }else{
                            giftCardHistoryItem
                        }
                    }
                    _state.value = PurchasedGiftCardsHistoryState.Success(purchasedGiftCardsList)
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.GET_HISTORY_LIST->{
                _state.value = PurchasedGiftCardsHistoryState.Error
            }
        }
    }
    sealed class PurchasedGiftCardsHistoryState{
        object Loading:PurchasedGiftCardsHistoryState()
        object Error:PurchasedGiftCardsHistoryState()
        object Empty:PurchasedGiftCardsHistoryState()
        data class Success(val list:List<GiftCardHistoryItem>):PurchasedGiftCardsHistoryState()
    }
    sealed class PurchasedGiftCardsHistoryEvent{
        data class NavigateToGiftCardDetail(var giftDetailsId:String):PurchasedGiftCardsHistoryEvent()
    }
}