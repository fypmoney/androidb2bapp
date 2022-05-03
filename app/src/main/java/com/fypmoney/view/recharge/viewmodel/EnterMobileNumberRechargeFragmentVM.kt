package com.fypmoney.view.recharge.viewmodel

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
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants.PREPAID
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.recharge.model.*
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class EnterMobileNumberRechargeFragmentVM(application: Application) : BaseViewModel(application) {

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()

    val state:LiveData<EnterMobileNumberRechargeState>
        get() = _state
    private val _state = MutableLiveData<EnterMobileNumberRechargeState>()

    val event:LiveData<EnterMobileNumberRechargeEvent>
        get() = _event
    private val _event = LiveEvent<EnterMobileNumberRechargeEvent>()

    lateinit var rechargeType:String

    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    init {
        callRecentRecharge()
    }

    fun onSelectContactFromPhonebook(){
        _event.value = EnterMobileNumberRechargeEvent.PickContactFromContactBookEvent
    }
    fun onContinueClick(){
        _event.value = EnterMobileNumberRechargeEvent.OnContinueEvent
    }
    fun callGetMobileHrl(mobile: String) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_HLR_CHECK,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_HLR_CHECK),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = MobileHRLRequest(mobile = mobile, type = "mobile")
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

    fun callExplporeContent(s: String?) {
        _state.value = EnterMobileNumberRechargeState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + s,
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }
    //TODO will integarate Paging in next phase.
    fun callRecentRecharge() {
        _state.value = EnterMobileNumberRechargeState.RecentRechargeLoading
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_RECENT_RECHARGE,
                NetworkUtil.endURL(ApiConstant.API_RECENT_RECHARGE) +"?page=0&size=50&sort=createdDate,desc",
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_HLR_CHECK -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val hlrResponse = Gson().fromJson<HLRResponse>(
                    json.get("data").toString(),
                    HLRResponse::class.java
                )
                _state.value = EnterMobileNumberRechargeState.HLRSuccess(hlrResponse.info)
                _event.value = EnterMobileNumberRechargeEvent.ShowNextScreenWithHlrInfo(hlrResponse.info)

            }
            ApiConstant.API_RECENT_RECHARGE -> {
                /*val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val response = Gson().fromJson<RecentRechargesResponse>(
                    json.get("data").toString(),
                    RecentRechargesResponse::class.java
                )*/
                if(responseData is RecentRechargesResponse){
                    val prepaidRecentRecharge = responseData.data.filter { it.cardType==PREPAID }
                    _state.value = EnterMobileNumberRechargeState.RecentRechargeSuccess(prepaidRecentRecharge)
                }

            }

            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                _state.value = EnterMobileNumberRechargeState.ExploreSuccess(arrayList)

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
        when (purpose) {
            ApiConstant.API_GET_HLR_CHECK -> {
                _state.value = EnterMobileNumberRechargeState.Error(errorResponseInfo,ApiConstant.API_GET_HLR_CHECK)
                _event.value = EnterMobileNumberRechargeEvent.ShowNextScreenWithHlrInfo(HLRInfo())
            }
            ApiConstant.API_Explore -> {
                _state.value = EnterMobileNumberRechargeState.Error(errorResponseInfo,ApiConstant.API_Explore)
            }
            ApiConstant.API_RECENT_RECHARGE -> {
                _state.value = EnterMobileNumberRechargeState.Error(errorResponseInfo,ApiConstant.API_RECENT_RECHARGE)
            }
        }

    }

    sealed class EnterMobileNumberRechargeState{
        object Loading:EnterMobileNumberRechargeState()
        object RecentRechargeLoading:EnterMobileNumberRechargeState()
        data class Error(val errorResponseInfo: ErrorResponseInfo,val errorFromApi:String):EnterMobileNumberRechargeState()
        data class ExploreSuccess(val explore:ArrayList<ExploreContentResponse>):EnterMobileNumberRechargeState()
        data class RecentRechargeSuccess(val recentItem:List<RecentRechargeItem>):EnterMobileNumberRechargeState()
        data class HLRSuccess(val hlrInfo:HLRInfo?):EnterMobileNumberRechargeState()
    }
    sealed class EnterMobileNumberRechargeEvent{
        object OnContinueEvent:EnterMobileNumberRechargeEvent()
        object PickContactFromContactBookEvent:EnterMobileNumberRechargeEvent()
        data class ShowNextScreenWithHlrInfo(val hlrInfo:HLRInfo?):EnterMobileNumberRechargeEvent()
    }

}