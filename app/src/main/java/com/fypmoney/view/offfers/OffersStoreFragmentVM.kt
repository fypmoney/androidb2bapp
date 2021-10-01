package com.fypmoney.view.offfers

import android.app.Application
import androidx.lifecycle.MutableLiveData
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
import com.google.gson.Gson

class OffersStoreFragmentVM(application: Application): BaseViewModel(application) {

    var offerTopList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()
    var offerBottomList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()

    init {
        callFetchFeedsApiBottom()
        callFetchFeedsApi()
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
                    var notificationList: ArrayList<FeedDetails>? =
                        ArrayList()
                    val response = feeds.getAllFeed?.getAllFeed
                    if (response?.feedDetails?.isNotEmpty() == true) {
                        if (response.feedDetails!![0].screenSection == "top") {

                            // check total count and if greater than 0 set list else set no data found

                            response?.feedDetails?.forEach() {
                                notificationList?.add(it)
                            }
                            offerTopList.postValue(notificationList)
                        } else {
                            response?.feedDetails?.forEach() {
                                notificationList?.add(it)
                            }
                            offerBottomList.postValue(notificationList)
                        }

                    }
                    // Save the access token in shared preference


                }

            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
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
                makeFetchFeedRequesttop(
                    latitude = null, longitude = null,
                    pageValue = 0
                ),
                this, isProgressBar = false
            )
        )

    }

    fun callFetchFeedsApiBottom(

        page: Int? = 0
    ) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_FETCH_ALL_FEEDS,
                NetworkUtil.endURL(ApiConstant.API_FETCH_ALL_FEEDS),
                ApiUrl.POST,
                makeFetchFeedRequestBottom(
                    latitude = null, longitude = null,
                    pageValue = page
                ),
                this, isProgressBar = false
            )
        )

    }

    private fun makeFetchFeedRequesttop(
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

        if (Utility.getCustomerDataFromPreference()?.userProfile?.gender == "MEN") {
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
            "{getAllFeed(page:0, size:null, id : null, screenName:\"OFFER\",screenSection:\"top\",tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: []){ total feedData  { id name description screenName screenSection sortOrder displayCard scope tags resourceId title subTitle }}}"
        return feedRequestModel

    }

    private fun makeFetchFeedRequestBottom(
        size: Int? = 50,
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

        if (Utility.getCustomerDataFromPreference()?.userProfile?.gender == "MEN") {
            gender = 0
        } else {
            gender = 1
        }
        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null) {
            feedtype =
                gender.toString() + "_" + Utility.getCustomerDataFromPreference()?.postKycScreenCode
        }

        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null) {
            feedtype =
                gender.toString() + "_" + Utility.getCustomerDataFromPreference()?.postKycScreenCode
        }

        val feedRequestModel = FeedRequestModel()
        feedRequestModel.query =
            "{getAllFeed(page:$pageValue, size:50, id : null, screenName:\"OFFER\",screenSection:\"bottom\",tags :[\"" + userInterestValue.toString() + ",\"" + feedtype + "\"],displayCard: []) { total feedData  { id name description screenName screenSection sortOrder displayCard scope tags resourceId title subTitle }}}"



        return feedRequestModel

    }
}