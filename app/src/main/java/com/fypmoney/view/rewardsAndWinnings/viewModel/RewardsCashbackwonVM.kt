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

    var cashbackHistoryAdapter = CashbackHistoryAdapter(this)
    var rewardHistoryList: MutableLiveData<ArrayList<BankTransactionHistoryResponseDetails>> =
        MutableLiveData()
    var noDataFoundVisibility = ObservableField(false)
    var onItemClicked = MutableLiveData<BankTransactionHistoryResponseDetails>()

    init {
        callRewardHistory(0)

    }

    fun callRewardHistory(page: Int) {
        var progress = false
        if (page == 0) {
            progress = true
        }
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.CashbackHistory,
                NetworkUtil.endURL(ApiConstant.CashbackHistory),
                ApiUrl.GET,
                param = QueryPaginationParams(

                    page,
                    8,
                    "createdDate,desc"
                ),
                this, isProgressBar = progress
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.CashbackHistory -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<BankTransactionHistoryResponseDetails>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                rewardHistoryList.postValue(arrayList)


            }
        }


    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.CashbackHistory -> {

            }
        }

    }


}