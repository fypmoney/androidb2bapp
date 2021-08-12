package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.model.homemodel.TopTenUsersResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsSectionAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.lang.Exception

class HomeScreenViewModel(application: Application) : BaseViewModel(application),
    FeedsAdapter.OnFeedItemClickListener {
    var availableAmount =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    var onAddMoneyClicked = MutableLiveData(false)
    var onPayClicked = MutableLiveData(false)
    var onChoreClicked = MutableLiveData(false)
    var isFetchBalanceVisible = ObservableField(true)
    var fetchBalanceLoading = MutableLiveData<Boolean>()
    var isFeedVisible = ObservableField(false)
    var feedsAdapter = FeedsSectionAdapter(this)
    var onFeedButtonClick = MutableLiveData<FeedDetails>()
    val selectedPosition = ObservableField<Int>()
    val isApiLoading = ObservableField(true)
    var latitude = ObservableField<Double>()
    val longitude = ObservableField<Double>()
    val fromWhichScreen = ObservableField(0)
    var onReferalAndCodeClicked = MutableLiveData<Boolean>()

    val topTenUsersResponse:LiveData<TopTenUsersResponse>
        get() = _topTenUserResponse
    private val _topTenUserResponse = MutableLiveData<TopTenUsersResponse>()

    init {
        callGetWalletBalanceApi()
        callTopTenUsersApi()
    }

    /*
    * This is used to handle add money
    * */
    fun onAddMoneyClicked() {
        onAddMoneyClicked.value = true

    }

    /*
    * This is used to handle pay button click
    * */
    fun onPayClicked() {
        onPayClicked.value = true

    }



    fun onReferralAndCodeClicked() {
        onReferalAndCodeClicked.value = true
    }

    /*
     * This method is used to get the balance of wallet
     * */
    private fun callGetWalletBalanceApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_WALLET_BALANCE,
                NetworkUtil.endURL(ApiConstant.API_GET_WALLET_BALANCE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }
    private fun callTopTenUsersApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.TOP_TEN_USER_API,
                NetworkUtil.endURL(ApiConstant.TOP_TEN_USER_API),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
  * This method is used to call the get feeds API
  * */
    /*
   * This method is used to call the get feeds API
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
                    latitude = null, longitude = null
                ),
                this, isProgressBar = false
            )
        )

    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_WALLET_BALANCE -> {
                if (responseData is GetWalletBalanceResponse) {
                    isFetchBalanceVisible.set(false)
                    fetchBalanceLoading.value = true
                    availableAmount.set(Utility.getFormatedAmount(Utility.convertToRs(responseData.getWalletBalanceResponseDetails.accountBalance)!!))

                }
            }
            ApiConstant.TOP_TEN_USER_API -> {
                val data =  Gson().fromJson(responseData.toString(), TopTenUsersResponse::class.java)
                if (data is TopTenUsersResponse) {
                    _topTenUserResponse.value = data
                }
            }
            ApiConstant.API_FETCH_ALL_FEEDS -> {
                if (responseData is FeedResponseModel) {
                    // Save the access token in shared preference
                    val response = responseData.getAllFeed?.getAllFeed
                    // check total count and if greater than 0 set list else set no data found

                    response?.feedDetails.let {
                        isFeedVisible.set(true)
                        feedsAdapter.setList(response?.feedDetails)
                    }


                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        isFeedVisible.set(true)
    }


    /*
   * This is used to handle Open Chore
   * */
    /* fun onChoreClicked() {
         onChoreClicked.value = true

     }*/
    /*
   * This method is used to make fetch feeds request
   * */
    private fun makeFetchFeedRequest(
        size: Int? = 3,
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
                    userInterestValue = userInterestValue.append(",")
                }

            }
        }

        val feedRequestModel = FeedRequestModel()
        feedRequestModel.query =
            "{getAllFeed(page:0,size:null, id : null, screenName:\"" + AppConstants.FEED_SCREEN_NAME_HOME + "\",screenSection:null,tags :null,displayCard: [\"STATICIMAGE\",\"STATICIMAGE1X1\",\"DEEPLINK1X1\",\"INAPPWEB1X1\",\"EXTWEBVIEW1X1\",\"BLOG\", \"DEEPLINK\", \"INAPPWEB\", \"EXTWEBVIEW\", \"VIDEO\"]) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId title subTitle content backgroundColor action{ type url buttonText }}}}"






        return feedRequestModel

    }

    override fun onFeedClick(position: Int, feedDetails: FeedDetails) {
        selectedPosition.set(position)
        onFeedButtonClick.value = feedDetails
    }
}