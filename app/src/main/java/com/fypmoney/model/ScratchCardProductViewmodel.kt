package com.fypmoney.model

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.livedata.LiveEvent
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class ScratchCardProductViewmodel(application: Application) : BaseViewModel(application) {
    var scratchResponseList = MutableLiveData<SpinWheelRotateResponseDetails>()
    var redeemCallBackResponse = MutableLiveData<aRewardProductResponse>()
    var onButtomClicked = MutableLiveData(false)
    var scratchCalled = MutableLiveData(false)
    val played = ObservableField(false)


    sealed class CardOfferEvent {
        object Continue : CardOfferEvent()
    }

    fun callProductsDetailsApi(orderId: String?) {
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

    fun callScratchWheelApi(orderId: String?, b: Boolean) {
        if (scratchCalled.value == false) {
            scratchCalled.value = true
            WebApiCaller.getInstance().request(
                ApiRequest(
                    ApiConstant.PLAY_ORDER_API,
                    NetworkUtil.endURL(ApiConstant.PLAY_ORDER_API + orderId),
                    ApiUrl.POST,
                    BaseRequest(),
                    this, isProgressBar = b
                )
            )
        }


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

                redeemCallBackResponse.value = spinDetails


            }
            ApiConstant.PLAY_ORDER_API -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val spinDetails = Gson().fromJson(
                    json.get("data"),
                    com.fypmoney.model.SpinWheelRotateResponseDetails::class.java
                )


                scratchResponseList.value = spinDetails
                played.set(true)

//                if (!spinDetails.cashbackWon.isNullOrEmpty())
//                    totalRewards.set(Utility.convertToRs(spinDetails.cashbackWon))
//                else {
//                    totalRewards.set("0")
//                }

            }
        }
    }


}