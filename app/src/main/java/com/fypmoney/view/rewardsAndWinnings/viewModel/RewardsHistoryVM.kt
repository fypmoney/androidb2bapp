package com.fypmoney.view.rewardsAndWinnings.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.view.rewardsAndWinnings.model.RewardHistoryResponse2
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class RewardsHistoryVM(application: Application) : BaseViewModel(application) {
    var rewardHistoryList2: MutableLiveData<ArrayList<RewardHistoryResponse2>> = MutableLiveData()

    init {

    }

    fun callRewardHistory() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.RewardsHistory,
                NetworkUtil.endURL(ApiConstant.RewardsHistory),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.RewardsHistory -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<RewardHistoryResponse2>>(
                    json.get("data").toString(),
                    Array<RewardHistoryResponse2>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList2.postValue(arrayList)


            }
        }


    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)


    }

}