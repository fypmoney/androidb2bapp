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
import com.fypmoney.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.*
import kotlin.collections.ArrayList

class RewardsHistoryVM(application: Application) : BaseViewModel(application) {
    var rewardHistoryList2: MutableLiveData<ArrayList<RewardHistoryResponseNew>> = MutableLiveData()
    var redeemproductDetails = MutableLiveData<aRewardProductResponse>()
    var orderNumber = MutableLiveData("")

    init {

    }

    fun callRewardHistory(page: Int) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.RewardsHistory,
                NetworkUtil.endURL(ApiConstant.RewardsHistory),
                ApiUrl.GET,
                param = QueryPaginationParams(

                    page,
                    10,
                    "createdDate,desc"
                ),
                this, isProgressBar = false
            )
        )
    }

    fun callProductsDetailsApi(orderId: String?) {
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

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.REWARD_PRODUCT_DETAILS -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.aRewardProductResponse::class.java
                )


                redeemproductDetails.value = spinDetails


            }
            ApiConstant.RewardsHistory -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<Array<RewardHistoryResponseNew>>(
                    json.get("data").toString(),
                    Array<RewardHistoryResponseNew>::class.java
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