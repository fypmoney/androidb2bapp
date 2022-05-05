package com.fypmoney.view.discord.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.*
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.SettingsResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.discord.model.DiscordProfileResponse
import com.fypmoney.view.referandearn.model.ReferMessageResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.moengage.core.internal.MoEConstants

class DiscordProfileVM(application: Application) : BaseViewModel(application) {


    var profileResponse = MutableLiveData<DiscordProfileResponse>()

    init {
        callGetDiscordProfileApi()
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


}