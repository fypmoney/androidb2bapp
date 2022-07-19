package com.fypmoney.view.rewardsAndWinnings.viewModel

import android.app.Application
import androidx.databinding.ObservableField
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
import com.fypmoney.view.arcadegames.model.MultipleJackpotNetworkResponse
import com.fypmoney.view.arcadegames.viewmodel.FragmentSpinWheelVM
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.rewardsAndWinnings.model.TotalJackpotResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class RewardsAndVM(application: Application) : BaseViewModel(application) {

    //var rewardHistoryList: MutableLiveData<ArrayList<RewardHistoryResponseNew>> = MutableLiveData()
    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    var latitude = ObservableField<Double>()

    val longitude = ObservableField<Double>()
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var selectedPosition = ObservableField(-1)

    val isApiLoading = ObservableField(true)
    val detailsCalling = ObservableField(false)

    var totalMynts = MutableLiveData(false)
    var totalCash = MutableLiveData(false)
    var totalTickets = MutableLiveData(false)
    var noOfJackpotTickets: Int? = null

    val state: LiveData<RewardsTicket>
        get() = _state
    private val _state = MutableLiveData<RewardsTicket>()


    var orderNumber = MutableLiveData("")

    val page = ObservableField(0)

    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var totalRewardsResponse: MutableLiveData<totalRewardsResponse> = MutableLiveData()

    var totalJackpotAmount: MutableLiveData<TotalJackpotResponse> = MutableLiveData()

    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()
    var totalCount = ObservableField(0)

    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()
    /*var rewardfeedList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()*/
    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }


    init {
//        callRewardProductList()
        //callFetchFeedsApi()
        callRewardSummary()
        callExplporeContent()

        callTotalRewardsEarnings()
//        callTotalJackpotCards()

    }


    fun callExplporeContent() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "REWARDS",
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
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





    /*fun callFetchFeedsApi(
        isProgressBarVisible: Boolean? = false,
        latitude: Double? = 0.0,
        longitude: Double? = 0.0
    ) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_ALL_FEEDS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_ALL_FEEDS),
                ApiUrl.POST,
                makeFetchFeedRequest(
                    latitude = null, longitude = null,
                    pageValue = page.get()
                ),
                this, isProgressBar = false
            )
        )

    }*/



    fun callRewardSummary() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REWARD_SUMMARY,
                NetworkUtil.endURL(ApiConstant.API_REWARD_SUMMARY),
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

    fun callTotalRewardsEarnings() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARD_EARNINGS,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARD_EARNINGS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    fun callTotalJackpotCards() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE,
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    /*fun callRewardHistory() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.RewardsHistory,
                NetworkUtil.endURL(ApiConstant.RewardsHistory),
                ApiUrl.GET,
                param = QueryPaginationParams(

                    0,
                    3,
                    "createdDate,desc"
                ),
                this, isProgressBar = false
            )
        )
    }*/

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {

            /*ApiConstant.RewardsHistory -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<RewardHistoryResponseNew>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)


            }*/
            ApiConstant.API_FETCH_OFFER_DETAILS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<offerDetailResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                openBottomSheet.postValue(arrayList)


            }/*
            ApiConstant.API_FETCH_ALL_FEEDS -> {
                var feeds = getObject(responseData.toString(), FeedResponseModel::class.java)
                if (feeds is FeedResponseModel) {
                    val response = feeds.getAllFeed?.getAllFeed

                        var notificationList: ArrayList<FeedDetails>? =
                            ArrayList()
                        response?.feedDetails?.forEach() {
                            notificationList?.add(it)
                        }
                        totalCount.set(response?.total)
                        rewardfeedList.postValue(notificationList)

                }

            }*/

//            ApiConstant.API_GET_JACKPOT_CARDS -> {
//
//
//                val json = JsonParser.parseString(responseData.toString()) as JsonObject
//                if (json != null && json.get("data").toString() != "[]") {
//                    val array = Gson().fromJson<TotalJackpotResponse>(
//                        json.get("data").toString(),
//                        TotalJackpotResponse::class.java
//                    )
//
//                    totalJackpotAmount.postValue(array)
//                }
//
//
//            }
            ApiConstant.API_FETCH_FEED_DETAILS -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<FeedDetails>(
                    json.get("data").toString(),
                    FeedDetails::class.java
                )

                feedDetail.postValue(array)


            }


            ApiConstant.API_GET_REWARD_EARNINGS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val array = Gson().fromJson<totalRewardsResponse>(
                    json.get("data").toString(),
                    com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse::class.java
                )

                totalRewardsResponse.postValue(array)


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


            ApiConstant.API_REWARD_SUMMARY -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )

                rewardSummaryStatus.postValue(array)


            }

            ApiConstant.API_GET_ALL_JACKPOTS_PRODUCTWISE -> {

                if (responseData is MultipleJackpotNetworkResponse) {
                    _state.value = RewardsTicket.Success(responseData.data?.totalTickets)
                }
            }

        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        loading.postValue(false)
        when (purpose) {

        }
    }

    sealed class RewardsTicket {
        object Loading : RewardsTicket()
        data class Success(val totalTickets: Int?) : RewardsTicket()
        object Error : RewardsTicket()
    }


}