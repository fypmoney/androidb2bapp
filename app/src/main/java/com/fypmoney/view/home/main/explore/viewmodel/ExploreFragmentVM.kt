package com.fypmoney.view.home.main.explore.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class ExploreFragmentVM(application: Application) : BaseViewModel(application) {


    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    var feedDetail: MutableLiveData<FeedDetails> = MutableLiveData()

    init {
        callExplporeContent(0)
    }

    fun callExplporeContent(page: Int) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "EXPLORE",
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

    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }
}