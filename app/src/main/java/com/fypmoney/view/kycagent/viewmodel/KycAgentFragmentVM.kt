package com.fypmoney.view.kycagent.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.kycagent.model.FetchShopDetailsResponse
import com.fypmoney.view.kycagent.model.ShopData
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KycAgentFragmentVM(application: Application) : BaseViewModel(application) {

    val state : LiveData<KycAgentState>
        get() = _state
    private val _state = MutableLiveData<KycAgentState>()

    val event : LiveData<KycAgentEvent>
        get() = _event
    private val _event = MutableLiveData<KycAgentEvent>()

    var isShopPhotoUpload: String ?= null
    var shopName: String ?= null
    var fullKycDone: String ?= null
    var isShopListed: String ?= null

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = LiveEvent()
    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    val textColor:MutableLiveData<String> by lazy {
        SharedPrefUtils.getString(
            application,
            SharedPrefUtils.SF_HOME_SCREEN_TEXT_COLOR
        )?.let {
            if(it.isNotEmpty()){
                MutableLiveData(it)
            }else{
                MutableLiveData("#000000")
            }
        }?: kotlin.run { MutableLiveData("#000000") }
    }

    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},")

    init {
        getShopData()
        callExplporeContent()
    }

    fun logOut(){
        _event.value = KycAgentEvent.LogOutEvent
    }

    private fun getShopData(){
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_FETCH_SHOP_DETAILS,
                endpoint = NetworkUtil.endURL(ApiConstant.API_FETCH_SHOP_DETAILS),
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )
    }

    fun callLogOutApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_LOGOUT,
                endpoint = NetworkUtil.endURL(ApiConstant.API_LOGOUT),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = BaseRequest()
            )
        )
    }

    fun callExplporeContent() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "AGENT_HOME",
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

        when(purpose){
            ApiConstant.API_FETCH_SHOP_DETAILS -> {
                if (responseData is FetchShopDetailsResponse) {
                    _state.value = responseData.data?.let { KycAgentState.Success(it) }
                }
            }

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
            ApiConstant.API_FETCH_SHOP_DETAILS -> {
                _state.value = KycAgentState.Error(errorResponseInfo)
            }

            ApiConstant.API_LOGOUT -> {
                viewModelScope.launch {
                    delay(DialogUtils.alertDialogTime)
                    Utility.resetPreferenceAfterLogout()
                    _event.value = KycAgentEvent.LogOutSuccess
                }

            }
        }

    }

    sealed class KycAgentState{
        object Loading : KycAgentState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : KycAgentState()
        data class Success(var shopData: ShopData) : KycAgentState()
    }

    sealed class KycAgentEvent{
        object LogOutEvent : KycAgentEvent()
        object LogOutSuccess : KycAgentEvent()
    }

}