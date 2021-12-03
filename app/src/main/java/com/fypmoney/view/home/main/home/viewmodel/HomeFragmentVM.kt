package com.fypmoney.view.home.main.home.viewmodel

import android.app.Application
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.currentFormattedDate
import com.fyp.trackr.models.CUSTOM_USER_WALLET_BALANCE
import com.fyp.trackr.models.CUSTOM_USER_WALLET_BALANCE_UPDATE_TIME
import com.fyp.trackr.models.UserTrackr
import com.fyp.trackr.models.push
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetWalletBalanceResponse
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.home.model.CallToActionUiModel
import com.fypmoney.view.home.main.home.model.QuickActionUiModel

class HomeFragmentVM(application: Application): BaseViewModel(application) {
    val event:LiveData<HomeFragmentEvent>
        get() = _event
    private val _event = LiveEvent<HomeFragmentEvent>()

    val state:LiveData<HomeFragmentState>
        get() = _state
    private val _state = MutableLiveData<HomeFragmentState>()

    init {
    }

    fun onViewDetailsClicked(){
        _event.value = HomeFragmentEvent.ViewCardDetails
    }

     fun fetchBalance(){
         _state.value = HomeFragmentState.LoadingBalanceState
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
     fun prepareQuickActionList() {
        val quickActionList = mutableListOf<QuickActionUiModel>()
        quickActionList.add(QuickActionUiModel(id = QuickActionEvent.AddAction,
            image = AppCompatResources.getDrawable(PockketApplication.instance,R.drawable.ic_add),
            name = "Add"))
        quickActionList.add(QuickActionUiModel(id = QuickActionEvent.PayAction,
            image = AppCompatResources.getDrawable(PockketApplication.instance,R.drawable.ic_pay), name = "Pay"))
        quickActionList.add(QuickActionUiModel(id = QuickActionEvent.OfferAction,
            image = AppCompatResources.getDrawable(PockketApplication.instance,R.drawable.ic_offers), name = "Offer"))
        _event.value = HomeFragmentEvent.QuickActionListReady(quickActionList)
    }
     fun prepareCallToActionList() {
        val callToActionList = mutableListOf<CallToActionUiModel>()
         callToActionList.add(
             CallToActionUiModel(id = 1,
            bannerImage = "https://ibb.co/SXxw3tz",
                 callToAction = "Add Parent")
         )
         callToActionList.add(
             CallToActionUiModel(id = 2,
            bannerImage = "https://ibb.co/BN20RBZ",
                 callToAction = "Add Money")
         )
         callToActionList.add(
             CallToActionUiModel(id = 3,
            bannerImage = "https://ibb.co/SXxw3tz",
                 callToAction = "Add Intrest")
         )
         _state.value = HomeFragmentState.SuccessCallToActionState(callToActionList)
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    responseData.getWalletBalanceResponseDetails.accountBalance.toIntOrNull()?.let { accountBalance->
                        currentFormattedDate()?.let {
                            val map = hashMapOf<String,Any>()
                            map[CUSTOM_USER_WALLET_BALANCE] = accountBalance
                            map[CUSTOM_USER_WALLET_BALANCE_UPDATE_TIME] = it
                            UserTrackr.push(map)
                        }
                        _state.value = HomeFragmentState.SuccessBalanceState(accountBalance)
                        if(accountBalance<10000){
                            _state.value = HomeFragmentState.LowBalanceAlertState(true)
                        }else{
                            _state.value = HomeFragmentState.LowBalanceAlertState(false)

                        }
                    }
                }
            }

        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.API_GET_WALLET_BALANCE->{
                _state.value = HomeFragmentState.ErrorBalanceState
            }
        }
    }
    sealed class HomeFragmentState{
        object LoadingBalanceState:HomeFragmentState()
        data class SuccessBalanceState(var balance:Int):HomeFragmentState()
        object ErrorBalanceState:HomeFragmentState()
        data class LowBalanceAlertState(var balanceIsLow:Boolean):HomeFragmentState()

        data class SuccessCallToActionState(var callToActionList:List<CallToActionUiModel>):HomeFragmentState()
    }
    sealed class HomeFragmentEvent{
        data class QuickActionListReady(var quickActionList:List<QuickActionUiModel>):HomeFragmentEvent()
        object ViewCardDetails:HomeFragmentEvent()
    }
    sealed class QuickActionEvent{
        object AddAction:QuickActionEvent()
        object PayAction:QuickActionEvent()
        object OfferAction:QuickActionEvent()
    }
}