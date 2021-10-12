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
import com.fypmoney.model.homemodel.TopTenUsersResponse
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.referandearn.model.TotalRefrralCashbackResponse
import com.google.gson.Gson

class ReferAndEarnActivityVM(application: Application):BaseViewModel(application) {
    val event: LiveData<ReferAndEarnEvent>
        get() = _event
    private val _event = LiveEvent<ReferAndEarnEvent>()

    val state: LiveData<ReferAndEarnState>
        get() = _state
    private val _state = MutableLiveData<ReferAndEarnState>()

    init {
        callTotalReferralAmount()
    }
    fun copyCode(){
        _event.value = ReferAndEarnEvent.CopyReferCode
    }
    fun shareCode(){
        trackr { it.services = arrayListOf(TrackrServices.MOENGAGE)
            it.name = TrackrEvent.REFSUCCESS
        }
        _event.value = ReferAndEarnEvent.ShareReferCode
    }


    private fun callTotalReferralAmount() {
        _state.value = ReferAndEarnState.Loading
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.TOTAL_REFERRAL_CASHBACK_API,
                NetworkUtil.endURL(ApiConstant.TOTAL_REFERRAL_CASHBACK_API),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when(purpose){
            ApiConstant.TOTAL_REFERRAL_CASHBACK_API->{
                val data =  Gson().fromJson(responseData.toString(), TotalRefrralCashbackResponse::class.java)
                _state.value = ReferAndEarnState.PopulateData(data.data.totalAmount)
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