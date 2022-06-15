package com.fypmoney.view.discord.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.discord.model.DiscordProfileResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class DiscordActivityVM(application: Application) : BaseViewModel(application) {

    val event: LiveData<DiscordEvent>
        get() = _event
    private val _event = LiveEvent<DiscordEvent>()
    var profileResponse = MutableLiveData<DiscordProfileResponse>()

    init {
        callGetDiscordProfileApi()
    }



    fun connectToDiscord() {
        _event.value = DiscordEvent.ConnectNow
    }

    private fun callGetDiscordProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.Api_GET_DISCORD_PROFILE,
                endpoint = NetworkUtil.endURL(ApiConstant.Api_GET_DISCORD_PROFILE),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = ""
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.Api_GET_DISCORD_PROFILE -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<DiscordProfileResponse>(
                    json.get("data").toString(),
                    DiscordProfileResponse::class.java
                )


                profileResponse.postValue(array)

            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    sealed class DiscordEvent {
        object ConnectNow : DiscordEvent()
    }
}