package com.fypmoney.view.giftCardModule.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.view.giftCardModule.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.moengage.core.internal.model.BaseRequest

/*
* This class is used to handle add money functionality
* */
class GiftHistoryModel(application: Application) : BaseViewModel(application) {

    init {
        callAllGifts()
    }

    var allGiftList: MutableLiveData<List<GiftHistoryResponseModel>> = MutableLiveData()

    fun callAllGifts() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.GET_HISTORY_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.GET_HISTORY_LIST),
                request_type = ApiUrl.GET,
                param = com.fypmoney.model.BaseRequest(), onResponse = this,
                isProgressBar = true
            )

        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.GET_HISTORY_LIST -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                var giftList: ArrayList<GiftHistoryResponseModel> = ArrayList()

                var array =
                    json["data"].asJsonArray
                array.forEach {
                    val obj = Gson().fromJson<GiftHistoryResponseModel>(
                        it,
                        GiftHistoryResponseModel::class.java
                    )
                    giftList.add(obj)
                }

                allGiftList.postValue(giftList)
            }

        }
    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false

        when (purpose) {


        }
    }

}


