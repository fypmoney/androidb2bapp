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
import com.fypmoney.model.SetPocketMoneyOtpReminder
import com.fypmoney.model.SetPocketMoneyReminder
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.model.OtpVerifyData
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyOtpVerifyResponse
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderResponse

class OtpReminderVM(application: Application) : BaseViewModel(application) {

    val stateReminderPocketMoney: LiveData<PocketMoneyReminderState>
        get() = _stateReminderPocketMoney
    private val _stateReminderPocketMoney = MutableLiveData<PocketMoneyReminderState>()

    val stateReminderOtpVerify: LiveData<PocketMoneyOtpVerifyState>
        get() = _stateReminderOtpVerify
    private val _stateReminderOtpVerify = MutableLiveData<PocketMoneyOtpVerifyState>()

    fun checkOtpPocketMoneyReminder(setPocketMoneyOtpReminder: SetPocketMoneyOtpReminder) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER,
                NetworkUtil.endURL(ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER),
                ApiUrl.POST,
                setPocketMoneyOtpReminder,
                this, isProgressBar = true
            )
        )
    }

    fun callPocketMoneyResendOtp(setPocketMoneyReminder: SetPocketMoneyReminder) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_ADD_POCKET_MONEY_REMINDER,
                NetworkUtil.endURL(ApiConstant.API_ADD_POCKET_MONEY_REMINDER),
                ApiUrl.POST,
                setPocketMoneyReminder,
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)

        when (purpose) {
            ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER -> {
                if (responseData is PocketMoneyOtpVerifyResponse) {
                    _stateReminderOtpVerify.value = PocketMoneyOtpVerifyState.Success(responseData.data)
                }
            }
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                if (responseData is PocketMoneyReminderResponse) {
                    _stateReminderPocketMoney.value = PocketMoneyReminderState.Success(responseData.data)
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_VERIFY_OTP_POCKET_MONEY_REMINDER -> {
                _stateReminderOtpVerify.value = PocketMoneyOtpVerifyState.Error(errorResponseInfo)
            }
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                _stateReminderPocketMoney.value = PocketMoneyReminderState.Error(errorResponseInfo)
            }
        }
    }

    sealed class PocketMoneyReminderState {
        object Loading : PocketMoneyReminderState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : PocketMoneyReminderState()
        data class Success(var dataItem: Data?) :
            PocketMoneyReminderState()
    }

    sealed class PocketMoneyOtpVerifyState{
        object Loading : PocketMoneyOtpVerifyState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : PocketMoneyOtpVerifyState()
        data class Success(var otpVerifyData: OtpVerifyData?) :
            PocketMoneyOtpVerifyState()
    }

}