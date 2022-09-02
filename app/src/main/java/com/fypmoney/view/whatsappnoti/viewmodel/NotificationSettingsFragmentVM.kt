package com.fypmoney.view.whatsappnoti.viewmodel

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
import com.fypmoney.model.BaseRequest
import com.fypmoney.view.whatsappnoti.model.WhatsAppNotificationResponse
import com.fypmoney.view.whatsappnoti.model.WhatsAppOptData

class NotificationSettingsFragmentVM(application: Application) : BaseViewModel(application) {

    val stateOptStatusChange: LiveData<WhatsAppNotificationOptState>
        get() = _stateOptStatusChange
    private val _stateOptStatusChange = MutableLiveData<WhatsAppNotificationOptState>()

    init {
        getOptStatusApi()
    }

    private fun getOptStatusApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_POST_OPT_STATUS_DATA,
                NetworkUtil.endURL(ApiConstant.API_POST_OPT_STATUS_DATA) + "status",
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    fun callChangeOptStatusApi(code: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_POST_OPT_STATUS_DATA,
                NetworkUtil.endURL(ApiConstant.API_POST_OPT_STATUS_DATA) + code,
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.API_POST_OPT_STATUS_DATA -> {
                if (responseData is WhatsAppNotificationResponse) {
                    _stateOptStatusChange.value =
                        responseData.data?.let { WhatsAppNotificationOptState.Success(it) }
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {
            ApiConstant.API_POST_OPT_STATUS_DATA -> {
                _stateOptStatusChange.value = WhatsAppNotificationOptState.Error(errorResponseInfo)
            }
        }
    }

    sealed class WhatsAppNotificationOptState {
        object Loading : WhatsAppNotificationOptState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : WhatsAppNotificationOptState()
        data class Success(var whatsAppOptData: WhatsAppOptData) : WhatsAppNotificationOptState()
    }
}