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
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.rewardsAndWinnings.model.TotalJackpotResponse
import com.fypmoney.view.rewardsAndWinnings.model.totalRewardsResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class RewardsJackpotVM(application: Application) : BaseViewModel(application) {


    var latitude = ObservableField<Double>()

    val longitude = ObservableField<Double>()
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var selectedPosition = ObservableField(-1)

    val isApiLoading = ObservableField(true)

    var orderNumber = MutableLiveData("")

    val pagejackpot = ObservableField(0)
    var totalJackpotAmount: MutableLiveData<TotalJackpotResponse> = MutableLiveData()

    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()

    var totalCountJackpot = ObservableField(0)

    var jackpotfeedList: LiveEvent<ArrayList<FeedDetails>> =
        LiveEvent()

    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }

    fun callTotalJackpotCards() {
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
        callFetchFeedsJackpotApi()


//callFetchFeedsJackpotApi()

    }


    fun onRefresh() {


    }


    private fun makeFetchFeedJackpotRequest(
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
        val feedRequestModel = FeedRequestModel()

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

        if (userInterest.isNullOrEmpty()) {
            feedRequestModel.query =
                "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + AppConstants.JACKPOT_SCREEN_NAME + "\",screenSection:null,tags :[\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"

        } else {
            feedRequestModel.query =
                "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + AppConstants.JACKPOT_SCREEN_NAME + "\",screenSection:null,tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"

        }




        return feedRequestModel

    }


    fun callFetchFeedsJackpotApi(
        isProgressBarVisible: Boolean? = false,
        latitude: Double? = 0.0,
        longitude: Double? = 0.0
    ) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_ALL_FEEDS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_ALL_FEEDS),
                ApiUrl.POST,
                makeFetchFeedJackpotRequest(
                    latitude = null, longitude = null,
                    pageValue = pagejackpot.get()
                ),
                this, isProgressBar = false
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
                    val response = feeds.getAllFeed?.getAllFeed
                    var notificationList: ArrayList<FeedDetails>? =
                        ArrayList()
                    response?.feedDetails?.forEach() {
                        notificationList?.add(it)
                    }
                    totalCountJackpot.set(response?.total)
                    jackpotfeedList.postValue(notificationList)

                }

            }

            ApiConstant.API_GET_JACKPOT_CARDS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                if (json != null && json.get("data").toString() != "[]") {
                    val array = Gson().fromJson<TotalJackpotResponse>(
                        json.get("data").toString(),
                        TotalJackpotResponse::class.java
                    )

                    totalJackpotAmount.postValue(array)
                }

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