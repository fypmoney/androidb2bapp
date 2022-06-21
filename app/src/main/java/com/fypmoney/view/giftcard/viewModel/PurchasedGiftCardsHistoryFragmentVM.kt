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
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.giftcard.model.GiftCardHistoryItem
import com.fypmoney.view.giftcard.model.GiftCardHistoryListNetworkResponse

class PurchasedGiftCardsHistoryFragmentVM(application: Application) : BaseViewModel(application) {

    val state:LiveData<PurchasedGiftCardsHistoryState>
        get() = _state
    private val _state = MutableLiveData<PurchasedGiftCardsHistoryState>()

    val event:LiveData<PurchasedGiftCardsHistoryEvent>
        get() = _event
    private val _event = LiveEvent<PurchasedGiftCardsHistoryEvent>()


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

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.GET_HISTORY_LIST->{
                if(responseData is GiftCardHistoryListNetworkResponse){
                    if(responseData.data.isNullOrEmpty()){
                        _state.value = PurchasedGiftCardsHistoryState.Empty
                    }else{
                        _state.value = PurchasedGiftCardsHistoryState.Success(responseData.data)
                    }
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
        data class Success(val list:List<GiftCardHistoryItem?>?):PurchasedGiftCardsHistoryState()
    }
    sealed class PurchasedGiftCardsHistoryEvent{

    }
}