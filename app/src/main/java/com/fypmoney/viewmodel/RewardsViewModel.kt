package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.Observable
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
import com.fypmoney.view.fragment.AssignedTaskFragment
import com.fypmoney.view.fragment.YourTasksFragment
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
    var rewardSummaryStatus: MutableLiveData<RewardPointsSummaryResponse> = MutableLiveData()
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
    }

    /*
      * This method is used to refresh on swipe
      *  */
    fun onRefresh() {


    }
    /*
      * This method is used to call get family notification API
      * */


    /*
      * This method is used to call user timeline API
      * */
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



    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        loading.postValue(false)
        when (purpose) {

            ApiConstant.RewardsHistory -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<RewardHistoryResponse>>(
                    json.get("data").toString(),
                    Array<RewardHistoryResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)


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