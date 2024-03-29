package com.fypmoney.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.FeedDetails
import com.fypmoney.model.FeedRequestModel
import com.fypmoney.model.FeedResponseModel
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.FeedsAdapter
import com.google.gson.Gson

/*
* This is used to show list of feeds
* */
class FeedsViewModel(application: Application) : BaseViewModel(application),
    FeedsAdapter.OnFeedItemClickListener {
    var feedsAdapter = FeedsAdapter(this, this)
    var noDataFoundVisibility = ObservableField(false)
    var noDataText = ObservableField(application.getString(R.string.no_data_found))
    var isRecyclerviewVisible = ObservableField(false)
    private var mLastClickTime: Long = 0
    var username = ObservableField<String>()
    var totalCount = ObservableField(0)
    var onFeedButtonClick = MutableLiveData<FeedDetails>()
    var onFeedsApiFail = MutableLiveData<Boolean>()
    val isLoading = ObservableBoolean()
    val pageValue = ObservableField(0)
    val isApiLoading = ObservableField(true)
    var latitude = ObservableField<Double>()
    val longitude = ObservableField<Double>()
    val isDenied = ObservableField(false)
    val selectedPosition = ObservableField<Int>()
    val fromWhichScreen = ObservableField(0)
    val onFeedsSuccess = MutableLiveData<ArrayList<String?>>()

    init {
        username.set(
            "Hey, " + Utility.getCustomerDataFromPreference()?.firstName + " 👋"
        )

    }


    /*
    * This method is used to call the get feeds API
    * */
    fun callFetchFeedsApi(
        isProgressBarVisible: Boolean? = false,
        latitude: Double? = 0.0,
        longitude: Double? = 0.0
    ) {
        if (isApiLoading.get()!!) {
            WebApiCaller.getInstance().request(
                ApiRequest(
                    ApiConstant.API_FETCH_ALL_FEEDS,
                    NetworkUtil.endURL(ApiConstant.API_FETCH_ALL_FEEDS),
                    ApiUrl.POST,
                    makeFetchFeedRequest(
                        pageValue = pageValue.get(),
                        latitude = latitude.toString(),
                        longitude = longitude.toString()
                    ),
                    this, isProgressBar = isProgressBarVisible
                )
            )
        }
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        isLoading.set(false)
        when (purpose) {
            ApiConstant.API_FETCH_ALL_FEEDS -> {
                var feeds = getObject(responseData.toString(), FeedResponseModel::class.java)
                if (feeds is FeedResponseModel) {
                    isRecyclerviewVisible.set(true)
                    // Save the access token in shared preference
                    val response = feeds.getAllFeed?.getAllFeed
                    isApiLoading.set(false)
                    totalCount.set(response?.total)
                    // check total count and if greater than 0 set list else set no data found
                    try {
                        when {
                            totalCount.get()!! > 0 && fromWhichScreen.get() == 0 -> {
                                if (response != null) {
                                    feedsAdapter.setList(response.feedDetails)
                                }

                            }
                            fromWhichScreen.get() != 0 -> {
                                val resultList = ArrayList<String?>()
                                response?.feedDetails?.forEach { resultList.add(it.resourceId) }
                                onFeedsSuccess.value = resultList

                            }
                            else -> {
                                noDataFoundVisibility.set(true)
                            }
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }

    }

    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    override fun onFeedClick(position: Int, feedDetails: FeedDetails) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        selectedPosition.set(position)
        onFeedButtonClick.value = feedDetails
    }

    /*
    * This method is used to refresh on swipe
    *  */
    fun onRefresh() {
        isLoading.set(true)
        isApiLoading.set(true)
        pageValue.set(0)
        feedsAdapter.feedList?.clear()

        callFetchFeedsApi(
            latitude = latitude.get(),
            longitude = longitude.get(),
            isProgressBarVisible = false
        )
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        isRecyclerviewVisible.set(true)
        isLoading.set(false)
        onFeedsApiFail.value = true
    }

    /*
      * This method is used to make fetch feeds request
      * */
    private fun makeFetchFeedRequest(
        size: Int? = 3,
        pageValue: Int?,
        latitude: String?,
        longitude: String?
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

        gender = if ( Utility.getCustomerDataFromPreference()?.userProfile?.gender.isNullOrEmpty() || Utility.getCustomerDataFromPreference()?.userProfile?.gender == "MALE") {
            0
        } else {
            1
        }
        feedtype = if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null) {
            gender.toString() + "_" + Utility.getCustomerDataFromPreference()?.postKycScreenCode
        }else{
            gender.toString() + "_" + "0"
        }



        when (fromWhichScreen.get()) {
            0 -> {
                if(userInterest.isNullOrEmpty()){
                    feedRequestModel.query =
                        "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + AppConstants.FEED_SCREEN_NAME + "\",screenSection:null,tags :[\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"

                }else{
                    feedRequestModel.query =
                        "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + AppConstants.FEED_SCREEN_NAME + "\",screenSection:null,tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"

                }

            }
            else -> {
                if(userInterest.isNullOrEmpty()){
                    feedRequestModel.query =
                        "{getAllFeed(page:" + pageValue + ", size:null, id : null, screenName:\"" + AppConstants.FEED_SCREEN_NAME + "\",screenSection:null,tags :[\"" + feedtype + "\"],displayCard: [\"DIDYOUKNOW\"]) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId title subTitle content backgroundColor action{ type url buttonText }}}}"
                }else{
                    feedRequestModel.query =
                        "{getAllFeed(page:" + pageValue + ", size:null, id : null, screenName:\"" + AppConstants.FEED_SCREEN_NAME + "\",screenSection:null,tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: [\"DIDYOUKNOW\"]) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId title subTitle content backgroundColor action{ type url buttonText }}}}"
                }

            }
        }
        return feedRequestModel

    }


}