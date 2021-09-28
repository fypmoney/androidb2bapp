package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.*

class StoreScreenViewModel(application: Application) : BaseViewModel(application),
   StoreItemAdapter.OnStoreItemClick,RechargeItemAdapter.OnRechargeItemClick {
    var latitude = ObservableField<Double>()
    var totalCount = ObservableField(0)
    val longitude = ObservableField<Double>()

    var storefeedList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()
    var offerstopList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()

    var offersbottomList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()
    var availableAmount =
        ObservableField(PockketApplication.instance.getString(R.string.dummy_amount))
    val isApiLoading = ObservableField(true)

    val page = ObservableField(0)
    var onAddMoneyClicked = MutableLiveData(false)
    var onPayClicked = MutableLiveData(false)
    var onChoreClicked = MutableLiveData(false)
    var isFetchBalanceVisible = ObservableField(true)
    var isFeedVisible = ObservableField(false)
    var onUpiClicked = MutableLiveData<StoreDataModel>()
    var onRechargeClicked = MutableLiveData<StoreDataModel>()

    var onFeedButtonClick = MutableLiveData<FeedDetails>()
    var storeAdapter = StoreItemAdapter(this, application.applicationContext)
    var rechargeItemAdapter = RechargeItemAdapter(this)
    init {
        callFetchFeedsApi()

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
                    userInterestValue = userInterestValue.append(",")
                }

            }
        }

        val feedRequestModel = FeedRequestModel()
        feedRequestModel.query =
            "{getAllFeed(page:" + pageValue + ", size:" + size + ", id : null, screenName:\"" + "SHOP" + "\",screenSection:null,tags :[\"" + userInterestValue.toString() + "\"],displayCard: []) { total feedData { id name description screenName screenSection sortOrder displayCard readTime author createdDate scope responsiveContent category{name code description } location {latitude longitude } tags resourceId resourceArr title subTitle content backgroundColor action{ type url buttonText }}}}"






        return feedRequestModel

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_FETCH_ALL_FEEDS -> {
                if (responseData is FeedResponseModel) {
                    // Save the access token in shared preference
                    val response = responseData.getAllFeed?.getAllFeed
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

    var clickedPositionForUpi = ObservableField<Int>()


    override fun onStoreItemClicked(position: Int, upiModel: StoreDataModel?) {
        clickedPositionForUpi.set(position)
        onUpiClicked.value = upiModel!!
    }


    override fun onRechargeItemClicked(position: Int, upiModel: StoreDataModel?) {
        clickedPositionForUpi.set(position)
        onRechargeClicked.value = upiModel!!
    }


}