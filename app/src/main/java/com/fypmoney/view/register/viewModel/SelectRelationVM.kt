package com.fypmoney.view.register.viewModel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.register.model.SelectRelationModel
import com.fypmoney.view.register.model.SendRelationSiblingParentResponse

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class SelectRelationVM(application: Application) : BaseViewModel(application) {
    var selectedRelation = ObservableField(SelectRelationModel())

    var user = MutableLiveData<SendRelationSiblingParentResponse>()

    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},"
    )

    val event: LiveData<HomeActivityEvent>
        get() = _event
    private var _event = LiveEvent<HomeActivityEvent>()

    fun onProfileClicked() {
        _event.value = HomeActivityEvent.ProfileClicked
    }

    fun onNotificationClicked() {
        _event.value = HomeActivityEvent.NotificationClicked
    }


    fun callIsAppUserApi(relationSibling: SendRelationSiblingParentResponse, userType: String?) {
      var postKyc = Utility.getCustomerDataFromPreference()?.postKycScreenCode
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.Api_Request_Siblin_parent,
                NetworkUtil.endURL(ApiConstant.Api_Request_Siblin_parent) + postKyc,
                ApiUrl.POST,
                relationSibling,
                this, isProgressBar = true
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.Api_Request_Siblin_parent -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

                val array = Gson().fromJson<SendRelationSiblingParentResponse>(
                    json.get("data").toString(),
                    SendRelationSiblingParentResponse::class.java
                )




                user.postValue(array)

            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


    sealed class HomeActivityEvent {
        object ProfileClicked : HomeActivityEvent()
        object NotificationClicked : HomeActivityEvent()
    }
}