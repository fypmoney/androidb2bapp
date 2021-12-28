package com.fypmoney.view.referandearn.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.referandearn.model.ReferMessageResponse
import com.fypmoney.view.referandearn.model.TotalRefrralCashbackResponse
import com.fypmoney.view.register.model.SendRelationSiblingParentResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class ReferAndEarnActivityVM(application: Application):BaseViewModel(application) {
    val event: LiveData<ReferAndEarnEvent>
        get() = _event
    private val _event = LiveEvent<ReferAndEarnEvent>()
    var referResponse = MutableLiveData<ReferMessageResponse>()
    val state: LiveData<ReferAndEarnState>
        get() = _state
    private val _state = MutableLiveData<ReferAndEarnState>()

    init {

        callReferScreenMessages()
    }
    fun copyCode(){
        _event.value = ReferAndEarnEvent.CopyReferCode
    }
    fun shareCode(){
        trackr {
            it.services = arrayListOf(
                TrackrServices.FIREBASE,
                TrackrServices.MOENGAGE,
                TrackrServices.FB, TrackrServices.ADJUST
            )
            it.name = TrackrEvent.refferal_shared
        }
        _event.value = ReferAndEarnEvent.ShareReferCode
    }


    private fun callReferScreenMessages() {

        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.REFERRAL_SCREEN_MESSAGES_API,
                NetworkUtil.endURL(ApiConstant.REFERRAL_SCREEN_MESSAGES_API),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.REFERRAL_SCREEN_MESSAGES_API -> {
                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<ReferMessageResponse>(
                    json.get("data").toString(),
                    ReferMessageResponse::class.java
                )


                referResponse.postValue(array)
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when(purpose){
            ApiConstant.TOTAL_REFERRAL_CASHBACK_API->{
                _state.value = ReferAndEarnState.Error
            }

        }
    }
    sealed class ReferAndEarnEvent{
        object CopyReferCode:ReferAndEarnEvent()
        object ShareReferCode:ReferAndEarnEvent()
    }

    sealed class ReferAndEarnState{
        object Loading:ReferAndEarnState()
        object Error:ReferAndEarnState()
        data class PopulateData(val totalAmount:Int):ReferAndEarnState()
    }

}