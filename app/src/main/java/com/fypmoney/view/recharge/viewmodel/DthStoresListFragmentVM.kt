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
import com.fypmoney.model.*
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.adapter.StoreItemAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class DthStoresListFragmentVM(application: Application) : BaseViewModel(application),
    StoreItemAdapter.OnStoreItemClick {



    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()

    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    var storeAdapter = StoreItemAdapter(this, application.applicationContext)

    val state: LiveData<DthStoresListState>
        get() = _state
    private val _state = MutableLiveData<DthStoresListState>()


    val event: LiveData<DthStoreListEvent>
        get() = _event
    private val _event = LiveEvent<DthStoreListEvent>()


    fun callExplporeContent(s: String?) {
        _state.value = DthStoresListState.Loading
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


    override fun onStoreItemClicked(position: Int, upiModel: StoreDataModel?) {
        _event.value = DthStoreListEvent.ShowDTHDetailsScreen(upiModel!!)
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                _state.value = DthStoresListState.ExploreSuccess(arrayList)

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
            ApiConstant.API_Explore -> {
                _state.value = DthStoresListState.Error(errorResponseInfo,ApiConstant.API_Explore)
            }
        }

    }
    sealed class DthStoresListState{
        object Loading:DthStoresListState()
        data class Error(val errorResponseInfo: ErrorResponseInfo,val errorFromApi:String):DthStoresListState()
        data class ExploreSuccess(val explore:ArrayList<ExploreContentResponse>):DthStoresListState()
    }
    sealed class DthStoreListEvent{
        data class ShowDTHDetailsScreen(val model:StoreDataModel):DthStoreListEvent()
    }

}