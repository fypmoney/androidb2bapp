package com.fypmoney.view.register.viewModel

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
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.register.model.UserGiftResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.*

class YourgiftVM(application: Application) : BaseViewModel(application) {

    var giftsList: MutableLiveData<ArrayList<UserGiftResponse>> = MutableLiveData()

    init {
        callFetchFeedsApi()
    }

    fun callFetchFeedsApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.Api_Your_Gifts,
                NetworkUtil.endURL(ApiConstant.Api_Your_Gifts),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )

    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.Api_Your_Gifts -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<UserGiftResponse>::class.java
                )
                val arrayList = ArrayList(array.toMutableList())
                giftsList.postValue(arrayList)

            }

        }


    }

    private fun <T> getObject(response: String, instance: Class<T>): Any? {
        return Gson().fromJson(response, instance)
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

    }
}