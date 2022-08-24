package com.fypmoney.view.whatsappnoti.viewmodel

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.R
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
import kotlinx.android.synthetic.main.dialog_settings_confirm.*

class NotificationSettingsFragmentVM(application: Application) : BaseViewModel(application) {

    var optStatus: Boolean = true

    val stateOptStatus: LiveData<WhatsAppOptStatusState>
        get() = _stateOptStatus
    private val _stateOptStatus = MutableLiveData<WhatsAppOptStatusState>()

    val stateOptStatusChange: LiveData<WhatsAppNotificationOptState>
        get() = _stateOptStatusChange
    private val _stateOptStatusChange = MutableLiveData<WhatsAppNotificationOptState>()


    fun callChangeOptStatusApi(code: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_POST_OPT_STATUS_DATA,
                NetworkUtil.endURL(ApiConstant.API_POST_OPT_STATUS_DATA) + code,
                ApiUrl.POST,
                BaseRequest(),
                this, isProgressBar = false
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

    sealed class WhatsAppOptStatusState {
        object Loading : WhatsAppOptStatusState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : WhatsAppOptStatusState()
//        data class Success(var ) : WhatsAppOptStatusState()
    }

    sealed class WhatsAppNotificationOptState {
        object Loading : WhatsAppNotificationOptState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : WhatsAppNotificationOptState()
        data class Success(var whatsAppOptData: WhatsAppOptData) : WhatsAppNotificationOptState()
    }
}