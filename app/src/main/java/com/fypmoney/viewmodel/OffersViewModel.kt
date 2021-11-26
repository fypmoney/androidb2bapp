package com.fypmoney.viewmodel

import android.app.Application
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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.*

import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class OffersViewModel(application: Application) : BaseViewModel(application) {

    init {
        callgetOffer()
    }

    var offerList: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()


    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    private fun callgetOffer() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.Api_OfferList,
                NetworkUtil.endURL(ApiConstant.Api_OfferList),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.Api_OfferList -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject
//                var feeds = getObject(responseData.toString(),Array<offerDetailResponse>::class.java)

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<offerDetailResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                offerList.postValue(arrayList)
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

    }


}