package com.fypmoney.view.home.main.home.viewmodel

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.currentFormattedDate
import com.fyp.trackr.models.*
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.GetWalletBalanceResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.home.model.CallToActionUiModel
import com.fypmoney.view.home.main.home.model.networkmodel.CallToActionNetworkResponse
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class HomeFragmentVM(application: Application): BaseViewModel(application) {
    val event: LiveData<HomeFragmentEvent>
        get() = _event
    private val _event = LiveEvent<HomeFragmentEvent>()

    val state: LiveData<HomeFragmentState>
        get() = _state
    private val _state = MutableLiveData<HomeFragmentState>()
    //var offerList: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    private val isUserComesFirstTime = checkUserIsLandedFirstTime()

    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = LiveEvent()
    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    val isUnreadNotificationAvailable = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_NEW_MESSAGE
    )
    val userProfileUrl = SharedPrefUtils.getString(
        application,
        SharedPrefUtils.SF_KEY_PROFILE_IMAGE
    )

    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},")
    init {
        //callgetOffer()
        callExplporeContent()
    }

    /*fun callgetOffer() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.Api_LIGHTENING_DEALS,
                NetworkUtil.endURL(ApiConstant.Api_LIGHTENING_DEALS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }*/

    fun onProfileClicked() {
        _event.value = HomeFragmentEvent.ProfileClicked
    }

    fun onNotificationClicked() {
        _event.value = HomeFragmentEvent.NotificationClicked
    }
    fun onViewDetailsClicked() {
        _event.value = HomeFragmentEvent.ViewCardDetails
    }

    fun onAddMoneyClicked(){
        trackr {
            it.name = TrackrEvent.home_add_money_click
        }
        _event.value = HomeFragmentEvent.AddAction

    }
    fun onPayMoneyClicked(){
        trackr {
            it.name = TrackrEvent.home_pay_money_click
        }
        _event.value = HomeFragmentEvent.PayAction
    }
    fun onUpiScanClicked(){
        trackr {
            it.name = TrackrEvent.home_upi_click
        }
        _event.value = HomeFragmentEvent.UpiScanAction
    }
    fun onPrepaidRechargeClicked(){
        trackr {
            it.name = TrackrEvent.recharge_click
        }
        _event.value = HomeFragmentEvent.PrepaidRechargeEvent(AppConstants.PREPAID)
    }
    fun onPostpaidRechargeClicked(){
        trackr {
            it.name = TrackrEvent.postpaid_click
        }
        _event.value = HomeFragmentEvent.PostpaidRechargeEvent(AppConstants.POSTPAID)

    }
    fun onDTHRechargeClicked(){
        trackr {
            it.name = TrackrEvent.dth_click
        }
        _event.value = HomeFragmentEvent.DthRechargeEvent

    }
    fun onBroadbandRechargeClicked(){
        trackr {
            it.name = TrackrEvent.broadband_click
        }
        _event.value = HomeFragmentEvent.BroadbandRechargeEvent

    }

    fun fetchBalance() {
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
/*
     fun prepareQuickActionList() {
        val quickActionList = mutableListOf<QuickActionUiModel>()
         quickActionList.add(
             QuickActionUiModel(
                 id = QuickActionEvent.AddAction,
                 image = AppCompatResources.getDrawable(
                     PockketApplication.instance,
                     R.drawable.ic_add
                 ),
                 name = "Add"
             )
         )
         quickActionList.add(
             QuickActionUiModel(
                 id = QuickActionEvent.PayAction,
                 image = AppCompatResources.getDrawable(
                     PockketApplication.instance,
                     R.drawable.ic_pay
                 ), name = "Pay"
             )
         )
         quickActionList.add(
             QuickActionUiModel(
                 id = QuickActionEvent.OfferAction,
                 image = AppCompatResources.getDrawable(
                     PockketApplication.instance,
                     R.drawable.ic_offers
                 ), name = "Offer"
             )
         )
         _event.value = HomeFragmentEvent.QuickActionListReady(quickActionList)
     }
*/



    fun callExplporeContent() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "HOME_SCREEN",
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    fun callFetchFeedsApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_FEED_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_FEED_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    fun callFetchOfferApi(id: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_OFFER_DETAILS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_OFFER_DETAILS) + id,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    responseData.getWalletBalanceResponseDetails.accountBalance.toIntOrNull()
                        ?.let { accountBalance ->
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
                            /*if((accountBalance<10000) and !PockketApplication.isLoadMoneyPopupIsShown){
                                _state.value = HomeFragmentState.ShowLoadMoneySheetState
                                PockketApplication.isLoadMoneyPopupIsShown = true
                            }*/
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
                                callToActionList.add(
                                    CallToActionUiModel(
                                        id = section?.id!!,
                                        bannerImage = section.contentResourceUri,
                                        contentX = section.contentDimensionX,
                                        contentY = section.contentDimensionY,
                                        redirectionType = section.redirectionType,
                                        redirectionResource = section.redirectionResource
                                    )
                                )
                            }
                        }
                        _state.value = HomeFragmentState.SuccessCallToActionState(callToActionList,responseData.data[0]?.sectionDisplayText)
                    }
                }

            }
            /*ApiConstant.Api_LIGHTENING_DEALS -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject
//                var feeds = getObject(responseData.toString(),Array<offerDetailResponse>::class.java)
                if (json != null && json.get("data").toString() != "[]") {
                    val array = Gson().fromJson(
                        json.get("data").toString(),
                        Array<offerDetailResponse>::class.java
                    )
                    val arrayList = ArrayList(array.toMutableList())
                    offerList.postValue(arrayList)
                } else {
                    offerList.postValue(null)
                }
            }*/
            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)
            }

            ApiConstant.API_FETCH_FEED_DETAILS -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FeedDetails>(
                    json.get("data").toString(),
                    FeedDetails::class.java
                )

                feedDetail.postValue(array)


            }
            ApiConstant.API_FETCH_OFFER_DETAILS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<offerDetailResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                openBottomSheet.postValue(arrayList)


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
        object ShowLoadMoneySheetState:HomeFragmentState()
        data class SuccessCallToActionState(var callToActionList:List<CallToActionUiModel>,var sectionTitle:String?):HomeFragmentState()
    }
    sealed class HomeFragmentEvent{
        //data class QuickActionListReady(var quickActionList:List<QuickActionUiModel>):HomeFragmentEvent()
        object ViewCardDetails:HomeFragmentEvent()
        object AddAction:HomeFragmentEvent()
        object PayAction:HomeFragmentEvent()
        object UpiScanAction:HomeFragmentEvent()
        data class PrepaidRechargeEvent(val rechargeType:String):HomeFragmentEvent()
        data class PostpaidRechargeEvent(val rechargeType:String):HomeFragmentEvent()
        object DthRechargeEvent:HomeFragmentEvent()
        object BroadbandRechargeEvent:HomeFragmentEvent()
        object ProfileClicked : HomeFragmentEvent()
        object NotificationClicked : HomeFragmentEvent()
    }
   /* sealed class QuickActionEvent{
        object AddAction:QuickActionEvent()
        object PayAction:QuickActionEvent()
        object OfferAction:QuickActionEvent()
    }*/
}