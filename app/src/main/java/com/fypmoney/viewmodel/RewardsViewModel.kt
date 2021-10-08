package com.fypmoney.viewmodel

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
import com.fypmoney.model.*
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling chores
* */
class RewardsViewModel(application: Application) : BaseViewModel(application) {


    var latitude = ObservableField<Double>()

    val longitude = ObservableField<Double>()
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var selectedPosition = ObservableField(-1)
    val isApiLoading = ObservableField(true)
    var clickedType = ObservableField("")
    var rewardHistoryList2: MutableLiveData<ArrayList<RewardHistoryResponse2>> = MutableLiveData()
    var spinArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()
    val page = ObservableField(0)
    var scratchArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()
    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var totalRewardsResponse: MutableLiveData<totalRewardsResponse> = MutableLiveData()
    var coinsBurned: MutableLiveData<CoinsBurnedResponse> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()
    var totalCount = ObservableField(0)
    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()
    var rewardfeedList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()
    var isRecyclerviewVisible = ObservableField(false)

    var TaskDetailResponse: MutableLiveData<TaskDetailResponse> = MutableLiveData()

    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }


    init {

        callRewardHistory()
        callRewardSummary()
        callRewardProductList()
        callTotalRewardsEarnings()
        callFetchFeedsApi()
    }


    fun onRefresh() {


    }

    fun callRewardHistory() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.RewardsHistory,
                NetworkUtil.endURL(ApiConstant.RewardsHistory),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    private fun makeFetchFeedRequest(
        size: Int? = 5,
        pageValue: Int? = 0,
        latitude: String? = "0.0",
        longitude: String? = "0.0"
    ): FeedRequestModel {
        val userInterest =
            SharedPrefUtils.getArrayList(getApplication(), SharedPrefUtils.SF_KEY_USER_INTEREST)
        var userInterestValue = StringBuilder()
        if (!userInterest.isNullOrEmpty()) {
            for (i in 0 until userInterest.size) {
                userInterestValue = userInterestValue.append(userInterest.get(i))
                if (i != userInterest.size - 1) {
                    userInterestValue = userInterestValue.append("\",\"")
                } else {
                    userInterestValue.append("\"")
                }

            }
        }


        var gender = 1
        var feedtype = ""

        if (Utility.getCustomerDataFromPreference()?.userProfile?.gender == "MALE") {
            gender = 0
        } else {
            gender = 1
        }
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null) {
            feedtype =
                gender.toString() + "_" + Utility.getCustomerDataFromPreference()?.postKycScreenCode
        }


        val feedRequestModel = FeedRequestModel()
        feedRequestModel.query =
            "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + "SHOP" + "\",screenSection:null,tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"






        return feedRequestModel

    }

    fun callFetchFeedsApi(
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

    }

    fun callRewardSummary() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REWARD_SUMMARY,
                NetworkUtil.endURL(ApiConstant.API_REWARD_SUMMARY),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    fun callRewardsRedeem(code: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REDEEM_REWARD,
                NetworkUtil.endURL(ApiConstant.API_REDEEM_REWARD) + code,
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    fun callRewardProductList() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARD_PRODUCTS,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARD_PRODUCTS),
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
                this, isProgressBar = true
            )
        )
    }

    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {
            ApiConstant.API_FETCH_ALL_FEEDS -> {
                var feeds = getObject(responseData.toString(), FeedResponseModel::class.java)
                if (feeds is FeedResponseModel) {

                    // Save the access token in shared preference
                    val response = feeds.getAllFeed?.getAllFeed
                    // check total count and if greater than 0 set list else set no data found
                    var notificationList: ArrayList<FeedDetails>? =
                        ArrayList()
                    response?.feedDetails?.forEach() {
                        notificationList?.add(it)
                    }
                    rewardfeedList.postValue(notificationList)

                }

            }
            ApiConstant.API_REDEEM_REWARD -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject
                val array = Gson().fromJson<CoinsBurnedResponse>(
                    json.get("data").toString(),
                    com.fypmoney.model.CoinsBurnedResponse::class.java
                )

                coinsBurned.postValue(array)


            }

            ApiConstant.API_GET_REWARD_EARNINGS -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject
                val array = Gson().fromJson<totalRewardsResponse>(
                    json.get("data").toString(),
                    com.fypmoney.model.totalRewardsResponse::class.java
                )

                totalRewardsResponse.postValue(array)


            }

            ApiConstant.RewardsHistory -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<RewardHistoryResponse2>>(
                    json.get("data").toString(),
                    Array<RewardHistoryResponse2>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList2.postValue(arrayList)


            }
            ApiConstant.API_GET_REWARD_PRODUCTS -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject
                val dataObject = json.get("data")?.asJsonObject

                val array = Gson().fromJson<Array<aRewardProductResponse>>(
                    dataObject?.get("SPIN_WHEEL").toString(),
                    Array<aRewardProductResponse>::class.java
                )


                val arrayList = ArrayList(array.toMutableList())

                val spinarray = Gson().fromJson<Array<aRewardProductResponse>>(
                    dataObject?.get("SCRATCH_CARD").toString(),
                    Array<aRewardProductResponse>::class.java
                )


                if (spinarray != null) {
                    val scratchArray = ArrayList(spinarray.toMutableList())
                    scratchArrayList.postValue(scratchArray)
                }


//                 var itemsArrayList: ArrayList<aRewardProductResponse> = ArrayList()
//
//                spinObject.forEachIndexed{pos,item->
//                    val images = Gson().fromJson(item, aRewardProductResponse::class.java)
////
//                    itemsArrayList.add(images)
//                }

                spinArrayList.postValue(arrayList)


            }
            ApiConstant.API_REWARD_SUMMARY -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<RewardPointsSummaryResponse>(
                    json.get("data").toString(),
                    RewardPointsSummaryResponse::class.java
                )

                rewardSummaryStatus.postValue(array)


            }

        }

    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        loading.postValue(false)


    }


}