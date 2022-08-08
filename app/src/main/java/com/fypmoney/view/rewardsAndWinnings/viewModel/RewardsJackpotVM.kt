package com.fypmoney.view.rewardsAndWinnings.viewModel

import android.app.Application
import androidx.databinding.ObservableField
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
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.rewardsAndWinnings.model.TotalJackpotResponse
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class RewardsJackpotVM(application: Application) : BaseViewModel(application) {


    var latitude = ObservableField<Double>()

    val longitude = ObservableField<Double>()
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)

    val isApiLoading = ObservableField(true)

    var orderNumber = MutableLiveData("")

    val pagejackpot = ObservableField(0)
    var totalJackpotAmount: MutableLiveData<TotalJackpotResponse> = MutableLiveData()

    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()

    var totalCountJackpot = ObservableField(0)


    //Explore integration
    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = LiveEvent()
    var feedDetail: MutableLiveData<FeedDetails> = MutableLiveData()

    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

    private fun callTotalJackpotCards() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_JACKPOT_CARDS,
                NetworkUtil.endURL(ApiConstant.API_GET_JACKPOT_CARDS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    init {
        callTotalJackpotCards()
        callExplporeContent(0)

    }

    fun callExplporeContent(page: Int) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "JACKPOT",
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
        loading.postValue(false)
        when (purpose) {
            ApiConstant.API_GET_JACKPOT_CARDS -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                if (json.get("data").toString() != "[]") {
                    val array = Gson().fromJson<TotalJackpotResponse>(
                        json.get("data").toString(),
                        TotalJackpotResponse::class.java
                    )

                    totalJackpotAmount.postValue(array)
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
        loading.postValue(false)
        when (purpose) {
            ApiConstant.API_REDEEM_REWARD -> {
                error.postValue(errorResponseInfo)

            }

        }
    }
}