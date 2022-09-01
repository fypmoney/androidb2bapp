package com.fypmoney.view.pocketmoneysettings.viewmodel

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
import com.fypmoney.view.pocketmoneysettings.model.DataItem
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderListResponse

class PocketMoneySettingsFragmentVM(application: Application) : BaseViewModel(application) {

    val stateReminderCoupon: LiveData<PocketMoneyReminderState>
        get() = _stateReminderCoupon
    private val _stateReminderCoupon = MutableLiveData<PocketMoneyReminderState>()

    fun callPocketMoneyReminderData() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_POCKET_MONEY_REMINDER_DATA,
                NetworkUtil.endURL(ApiConstant.API_GET_POCKET_MONEY_REMINDER_DATA),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.API_GET_POCKET_MONEY_REMINDER_DATA -> {
                if (responseData is PocketMoneyReminderListResponse) {
                    _stateReminderCoupon.value = PocketMoneyReminderState.Success(responseData.data)
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {
            ApiConstant.API_GET_POCKET_MONEY_REMINDER_DATA -> {
                _stateReminderCoupon.value = PocketMoneyReminderState.Error(errorResponseInfo)
            }
        }
    }

    sealed class PocketMoneyReminderState {
        object Loading : PocketMoneyReminderState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : PocketMoneyReminderState()
        data class Success(var dataItem: List<DataItem?>?) :
            PocketMoneyReminderState()
    }

}