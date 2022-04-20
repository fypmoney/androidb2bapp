package com.fypmoney.view.recharge.viewmodel

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
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.adapter.*
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.recharge.adapter.RechargeItemAdapter
import com.fypmoney.view.recharge.adapter.RechargePostpaidAdapter
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson

class RechargeViewModel(application: Application) : BaseViewModel(application),
    StoreItemAdapter.OnStoreItemClick {
    var latitude = ObservableField<Double>()
    var totalCount = ObservableField(0)
    val longitude = ObservableField<Double>()

    var storefeedList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()
    val isApiLoading = ObservableField(true)

    val page = ObservableField(0)
    var onUpiClicked = MutableLiveData<StoreDataModel>()

    var onFeedButtonClick = MutableLiveData<FeedDetails>()
    var storeAdapter = StoreItemAdapter(this, application.applicationContext)

    var openBottomSheet: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()

    var feedDetail: MutableLiveData<FeedDetails> = LiveEvent()

    init {
        callFetchFeedsApi()
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

    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()
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

    /*
     * This method is used to get the balance of wallet
     * */

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
        if (userInterest.isNullOrEmpty()) {
            feedRequestModel.query =
                "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + "SHOP" + "\",screenSection:null,tags :[\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"
        } else {
            feedRequestModel.query =
                "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + "SHOP" + "\",screenSection:null,tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"
        }




        return feedRequestModel

    }

    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
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
                    storefeedList.postValue(notificationList)

                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

    }


    override fun onStoreItemClicked(position: Int, upiModel: StoreDataModel?) {
        onUpiClicked.value = upiModel!!
    }




}