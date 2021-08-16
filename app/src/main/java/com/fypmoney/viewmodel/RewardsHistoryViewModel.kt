package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetRewardsHistoryResponse
import com.fypmoney.model.GetRewardsHistoryResponseDetails
import com.fypmoney.view.adapter.RewardsHistoryAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class RewardsHistoryViewModel(application: Application) : BaseViewModel(application) {
    var rewardsHistoryAdapter = RewardsHistoryAdapter()
    var noDataFoundVisibility = ObservableField(false)


    init {
        callGetRewardsHistoryApi()
    }

    /*
* This method is used to call get rewards history api
* */
    private fun callGetRewardsHistoryApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARDS_HISTORY,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARDS_HISTORY),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_REWARDS_HISTORY -> {
                Log.d("chackspin", responseData.toString())
                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<GetRewardsHistoryResponseDetails>>(
                    json.get("data").toString(),
                    Array<GetRewardsHistoryResponseDetails>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardsHistoryAdapter.setList(arrayList)


            }

        }


    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        Log.d("chackspin", errorResponseInfo.msg)

    }

}