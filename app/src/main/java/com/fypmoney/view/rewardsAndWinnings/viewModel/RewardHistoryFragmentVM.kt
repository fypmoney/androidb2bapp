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


class RewardHistoryFragmentVM(application: Application) : BaseViewModel(application) {

    var rewardHistoryList: MutableLiveData<ArrayList<RewardHistoryResponseNew>> = MutableLiveData()

    var latitude = ObservableField<Double>()
    var redeemproductDetails = LiveEvent<aRewardProductResponse>()
    val longitude = ObservableField<Double>()
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var selectedPosition = ObservableField(-1)
    var selectedPositionScratch = ObservableField(-1)
    val isApiLoading = ObservableField(true)
    val detailsCalling = ObservableField(false)
    var clickedType = ObservableField("")
    var totalmyntsClicked = MutableLiveData(false)
    var orderNumber = MutableLiveData("")
    var spinArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()
    val page = ObservableField(0)
    var scratchArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()
    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var totalRewardsResponse: MutableLiveData<totalRewardsResponse> = MutableLiveData()
    var coinsBurned: LiveEvent<CoinsBurnedResponse> = LiveEvent()
    var totalJackpotAmount: MutableLiveData<TotalJackpotResponse> = MutableLiveData()

    var error: MutableLiveData<ErrorResponseInfo> = MutableLiveData()
    var totalCount = ObservableField(0)

    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()
    var rewardfeedList: MutableLiveData<ArrayList<FeedDetails>> =
        MutableLiveData()


    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }


    init {

        callRewardHistory()
        callRewardSummary()

    }


    fun onRefresh() {


    }

    fun callProductsDetailsApi(orderId: String?) {
        detailsCalling.set(true)
        orderNumber.value = orderId
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.REWARD_PRODUCT_DETAILS,
                NetworkUtil.endURL(ApiConstant.REWARD_PRODUCT_DETAILS + orderId),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
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
                this, isProgressBar = false
            )
        )
    }


    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    fun callRewardHistory() {
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
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {

            ApiConstant.RewardsHistory -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<RewardHistoryResponseNew>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)


            }


            ApiConstant.API_REWARD_SUMMARY -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

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
        when (purpose) {
            ApiConstant.API_REDEEM_REWARD -> {
                error.postValue(errorResponseInfo)
            }
        }
    }


}