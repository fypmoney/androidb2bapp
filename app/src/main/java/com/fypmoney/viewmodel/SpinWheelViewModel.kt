package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat
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

import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.*

/*
* This class is used for spin the wheel
* */
class SpinWheelViewModel(application: Application) : BaseViewModel(application) {
    var luckyItemList: ArrayList<LuckyItem> = ArrayList()
    var getRewardsResponseList = ObservableField<GetRewardsResponseDetails>()
    var spinWheelResponseList = MutableLiveData<SpinWheelResponseDetails>()
    var redeemCallBackResponse = MutableLiveData<RedeemRequestedResponse>()
    var redeemDetailsResponse = MutableLiveData<RedeemDetailsResponse>()
    val onGetRewardsSuccess = MutableLiveData<Boolean>()
    val spinAllowed = MutableLiveData<String>()
    val onSpinDone = MutableLiveData<Boolean>()
    val enableSpin = MutableLiveData<Boolean>()
    val coinVisibilty = ObservableField(true)
    val screenopen = ObservableField(false)
    val spinnerClickable = ObservableField(true)
    val onRewardsHistoryClicked = MutableLiveData<Boolean>()
    val onPlayClicked = MutableLiveData<Boolean>()
    val onError = MutableLiveData<ErrorResponseInfo>()
    private val fromWhich = ObservableField<String>()
    val totalRewards =
        ObservableField(PockketApplication.instance.getString(R.string.fetching_reward))

    init {
        setDataInSpinWheel()
    }

    /*
    * This is used to handle rewards history
    * */
    fun onRewardsHistoryClicked() {
        onRewardsHistoryClicked.value = true
    }

    /*
    * This method is used to set data in spin the wheel
    * */
    private fun setDataInSpinWheel() {
        val luckyItem1 = LuckyItem()
        luckyItem1.topText = PockketApplication.instance.getString(R.string.better_luck_emoji)
        luckyItem1.icon = R.drawable.cash
        luckyItem1.color = ContextCompat.getColor(getApplication(), R.color.text_color_dark)
        luckyItemList.add(luckyItem1)

        val luckyItem2 = LuckyItem()
        luckyItem2.topText = "5"
        luckyItem2.icon = R.drawable.cash
        luckyItem2.color = ContextCompat.getColor(getApplication(), R.color.color_dark_red)
        luckyItemList.add(luckyItem2)

        val luckyItem3 = LuckyItem()
        luckyItem3.topText = "10"
        luckyItem3.icon = R.drawable.cash
        luckyItem3.color = ContextCompat.getColor(getApplication(), R.color.color_dark_yellow)
        luckyItemList.add(luckyItem3)

        val luckyItem4 = LuckyItem()
        luckyItem4.topText = "20"
        luckyItem4.icon = R.drawable.cash
        luckyItem4.color = ContextCompat.getColor(getApplication(), R.color.color_dark_red)
        luckyItemList.add(luckyItem4)

        val luckyItem5 = LuckyItem()
        luckyItem5.topText = "50"
        luckyItem5.icon = R.drawable.cash
        luckyItem5.color = ContextCompat.getColor(getApplication(), R.color.color_dark_yellow)
        luckyItemList.add(luckyItem5)

        val luckyItem6 = LuckyItem()
        luckyItem6.topText = "100"
        luckyItem6.icon = R.drawable.cash
        luckyItem6.color = ContextCompat.getColor(getApplication(), R.color.color_dark_black)
        luckyItemList.add(luckyItem6)

        val luckyItem7 = LuckyItem()
        luckyItem7.topText = "500"
        luckyItem7.icon = R.drawable.cash
        luckyItem7.color = ContextCompat.getColor(getApplication(), R.color.color_dark_yellow)
        luckyItemList.add(luckyItem7)
    }

    /*
* This method is used to call get rewards api
* */
    fun callGetRewardsApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REWARDS_API,
                NetworkUtil.endURL(ApiConstant.API_GET_REWARDS_API),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )


    }

    fun callRedeemCoins() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_REDEEM_COINS_API,
                NetworkUtil.endURL(ApiConstant.API_REDEEM_COINS_API),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )


    }

    fun callGetCoinsToRedeem() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_REDEEM_DETAILS_API,
                NetworkUtil.endURL(ApiConstant.API_GET_REDEEM_DETAILS_API),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )


    }

    /*
    * This method is used to play wheel
    * */
    fun onPlayClicked() {
        spinnerClickable.set(false)
        onPlayClicked.value = true
        coinVisibilty.set(false)
    }

    /*
   * This method is used to call spin wheel api
   * */
    fun callSpinWheelApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_SPIN_WHEEL,
                NetworkUtil.endURL(ApiConstant.API_SPIN_WHEEL),
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = false
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.API_SPIN_WHEEL -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.SpinWheelResponseDetails::class.java
                )

                spinWheelResponseList.value = spinDetails
                fromWhich.set(AppConstants.ERROR_TYPE_AFTER_SPIN)
                callGetRewardsApi()

            }
            ApiConstant.API_REDEEM_COINS_API -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val redeemDetails = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.RedeemDetailsResponse::class.java
                )

                redeemDetailsResponse.postValue(redeemDetails)
                callGetRewardsApi()
            }

            ApiConstant.API_GET_REDEEM_DETAILS_API -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val redeemDetails = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.RedeemDetailsResponse::class.java
                )

                redeemDetailsResponse.postValue(redeemDetails)

            }
            ApiConstant.API_GET_REWARDS_API -> {


                val json = JsonParser().parse(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.GetRewardsResponseDetails::class.java
                )

                getRewardsResponseList.set(spinDetails)

                if (fromWhich.get() != AppConstants.ERROR_TYPE_AFTER_SPIN) {
                    spinAllowed.value = spinDetails.spinAllowedToday!!
                }
                when {
                    fromWhich.get() != AppConstants.ERROR_TYPE_AFTER_SPIN -> {
                        if (enableSpin.value == true) {
                            onGetRewardsSuccess.value = true
                        }
                    }
                }
                if (!spinDetails.pointsToRedeem.isNullOrEmpty())
                    totalRewards.set(((spinDetails.pointsToRedeem)!!.toInt() / 100).toString())
                else {
                    totalRewards.set("0")
                }


            }
        }


    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {
            ApiConstant.API_SPIN_WHEEL -> {
                onError.value = errorResponseInfo
            }
        }


    }

}



