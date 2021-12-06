package com.fypmoney.view.home.main.home.viewmodel

import android.app.Application
import android.text.format.DateUtils
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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.home.model.CallToActionUiModel
import com.fypmoney.view.home.main.home.model.QuickActionUiModel
import com.fypmoney.view.home.main.home.model.networkmodel.CallToActionNetworkResponse

class HomeFragmentVM(application: Application): BaseViewModel(application) {
    val event:LiveData<HomeFragmentEvent>
        get() = _event
    private val _event = LiveEvent<HomeFragmentEvent>()

    val state:LiveData<HomeFragmentState>
        get() = _state
    private val _state = MutableLiveData<HomeFragmentState>()

    val isUserComesFirstTime = checkUserIsLandedFirstTime()

    init {
        callToAction()
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

     fun callToAction(){
         _state.value = HomeFragmentState.LoadingBalanceState
         WebApiCaller.getInstance().request(
             ApiRequest(
                 ApiConstant.API_CALLTO_ACTION,
                 NetworkUtil.endURL(ApiConstant.API_CALLTO_ACTION+"HOME_SCREEN"),
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

/*
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
*/

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
                        if(!isUserComesFirstTime){
                            if(accountBalance<10000){
                                _state.value = HomeFragmentState.LowBalanceAlertState(true)
                            }else{
                                _state.value = HomeFragmentState.LowBalanceAlertState(false)

                            }
                        }
                    }
                }
            }
            ApiConstant.API_CALLTO_ACTION->{
                if (responseData is CallToActionNetworkResponse) {
                    //Map network model to ui model
                    val callToActionList = mutableListOf<CallToActionUiModel>()
                    if(!responseData.data.isNullOrEmpty()){
                        val callToActionSections = responseData.data[0]?.sectionContent
                        if (callToActionSections != null) {
                            for (section in callToActionSections){
                                callToActionList.add(CallToActionUiModel(id = section?.id!!,
                                    bannerImage = section.contentResourceUri,
                                    contentX = section.contentDimensionX,
                                    contentY = section.contentDimensionY,
                                    redirectionType = section.redirectionType,
                                    redirectionResource = section.redirectionResource
                                ))
                            }
                        }
                        _state.value = HomeFragmentState.SuccessCallToActionState(callToActionList)
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
            ApiConstant.API_CALLTO_ACTION->{
                _state.value = HomeFragmentState.ErrorBalanceState
            }
        }
    }

    /**
     * This function check that user is landed first time on home screen.
     * return:- true if day is same else false
     */
    private fun checkUserIsLandedFirstTime():Boolean{
        SharedPrefUtils.getLong(
            PockketApplication.instance,
            SharedPrefUtils.SF_IS_USER_LANDED_ON_HOME_SCREEN_TIME
        ).let {
            return it == 0L || DateUtils.isToday(it)
        }
    }
    sealed class HomeFragmentState{
        object LoadingBalanceState:HomeFragmentState()
        object LoadingCallToActionState:HomeFragmentState()
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