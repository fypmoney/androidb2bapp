package com.fypmoney.view.home.main.explore.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.QueryPaginationParams
import com.fypmoney.model.RewardHistoryResponseNew
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.ExploreResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class ExploreFragmentVM(application: Application) : BaseViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    var rewardHistoryList: MutableLiveData<ArrayList<ExploreContentResponse>> = MutableLiveData()

    init {
        callExplporeContent(0)
    }

    fun callExplporeContent(page: Int) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_Explore,
                NetworkUtil.endURL(ApiConstant.API_Explore) + "HOME_SCREEN",
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_Explore -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson(
                    json.get("data").toString(),
                    Array<ExploreContentResponse>::class.java
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