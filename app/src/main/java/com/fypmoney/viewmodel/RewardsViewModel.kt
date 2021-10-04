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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling chores
* */
class RewardsViewModel(application: Application) : BaseViewModel(application) {
    var onAddMoneyClicked = MutableLiveData(false)
    var loading = MutableLiveData(false)
    var yourtask = ObservableField(false)
    var rewardHistoryList: MutableLiveData<ArrayList<RewardHistoryResponse>> = MutableLiveData()
    var spinArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()

    var scratchArrayList: MutableLiveData<ArrayList<aRewardProductResponse>> = MutableLiveData()
    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var rewardsproductsSpinner: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var rewardsproducts: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
    var totalRewardsResponse: MutableLiveData<totalRewardsResponse> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()

    var AssignedByYouTask: MutableLiveData<ArrayList<AssignedTaskResponse>> = MutableLiveData()
    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()
    var bottomtaskwithdrawn: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()
    var selectedPosition = MutableLiveData(-1)
    var amountToBeAdded: String? = ""

    var TaskDetailResponse: MutableLiveData<TaskDetailResponse> = MutableLiveData()

    fun onSelectClicked() {
        onAddMoneyClicked.value = true
    }


    init {

        callRewardHistory()
        callRewardSummary()
        callRewardProductList()
        callTotalRewardsEarnings()
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


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {


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

                val array = Gson().fromJson<Array<RewardHistoryResponse>>(
                    json.get("data").toString(),
                    Array<RewardHistoryResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)


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