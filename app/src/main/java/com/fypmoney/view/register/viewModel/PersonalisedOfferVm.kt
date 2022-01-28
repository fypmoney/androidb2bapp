package com.fypmoney.view.register.viewModel

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
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.register.model.SelectRelationModel

import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class PersonalisedOfferVm(application: Application) : BaseViewModel(application) {
    var selectedRelation = ObservableField(SelectRelationModel())

    var user = MutableLiveData<CustomerInfoResponseDetails>()
    var offerList: MutableLiveData<ArrayList<offerDetailResponse>> = MutableLiveData()
    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},"
    )



    init {
        callgetOffer()
    }


    private fun callgetOffer() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.Api_LIGHTENING_DEALS,
                NetworkUtil.endURL(ApiConstant.Api_LIGHTENING_DEALS),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.Api_LIGHTENING_DEALS -> {
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