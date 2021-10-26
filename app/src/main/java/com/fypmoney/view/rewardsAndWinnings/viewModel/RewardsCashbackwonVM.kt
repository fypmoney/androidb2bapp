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
import com.fypmoney.view.adapter.CashbackHistoryAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class RewardsCashbackwonVM(application: Application) : BaseViewModel(application) {

    var bankTransactionHistoryAdapter = CashbackHistoryAdapter(this)
    var rewardHistoryList: MutableLiveData<ArrayList<CashbackWonResponse>> = MutableLiveData()
    var noDataFoundVisibility = ObservableField(false)
    var onItemClicked = MutableLiveData<CashbackWonResponse>()

    init {
        callRewardHistory(0)

    }

    fun callRewardHistory(page: Int) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.CashbackHistory,
                NetworkUtil.endURL(ApiConstant.CashbackHistory),
                ApiUrl.GET,
                param = QueryPaginationParams(

                    page,
                    3,
                    "createdDate,desc"
                ),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.CashbackHistory -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<CashbackWonResponse>>(
                    json.get("data").toString(),
                    Array<CashbackWonResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)


            }
        }


    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)


    }


}