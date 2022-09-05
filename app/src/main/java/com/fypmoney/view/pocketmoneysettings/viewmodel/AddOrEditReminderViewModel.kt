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
import com.fypmoney.model.SetPocketMoneyReminder
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.pocketmoneysettings.model.Data
import com.fypmoney.view.pocketmoneysettings.model.PocketMoneyReminderResponse

class AddOrEditReminderViewModel(application: Application) : BaseViewModel(application) {

    val stateReminderPocketMoney: LiveData<PocketMoneyReminderState>
        get() = _stateReminderPocketMoney
    private val _stateReminderPocketMoney =
        MutableLiveData<PocketMoneyReminderState>()

    val stateAmountPocketMoney: LiveData<ReminderEditTextState>
        get() = _stateAmountPocketMoney
    private val _stateAmountPocketMoney = MutableLiveData<ReminderEditTextState>()

    val phoneBookEvent: LiveData<AddReminderEvents>
        get() = _phoneBookEvent
    private val _phoneBookEvent = LiveEvent<AddReminderEvents>()

    fun callPocketMoneySendOtp(setPocketMoneyReminder: SetPocketMoneyReminder) {
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
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                if (responseData is PocketMoneyReminderResponse) {
                    _stateReminderPocketMoney.value =
                        PocketMoneyReminderState.Success(responseData.data)
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_ADD_POCKET_MONEY_REMINDER -> {
                _stateReminderPocketMoney.value = PocketMoneyReminderState.Error(errorResponseInfo)
            }
        }
    }

    fun onSelectContactFromPhonebook() {
        _phoneBookEvent.value = AddReminderEvents.PickContactFromContactBookEvent
    }

    fun realtimeTextChanged(text: CharSequence) {
        if (text.isNotEmpty() && Integer.parseInt(text.toString()) < 10)
            _stateAmountPocketMoney.value = ReminderEditTextState.LessThanTen

        if (text.isNotEmpty() && Integer.parseInt(text.toString()) >= 10 && Integer.parseInt(text.toString()) <= 5000)
            _stateAmountPocketMoney.value = ReminderEditTextState.GreaterThanTenLessThanFiveThousand

        if (text.isNotEmpty() && Integer.parseInt(text.toString()) > 5000)
            _stateAmountPocketMoney.value = ReminderEditTextState.GreaterThanFiveThousand
    }

    fun mobileNumberValidation(text: CharSequence) {
        if (text.toString() == "0") {
            _stateAmountPocketMoney.value = ReminderEditTextState.MobileNumberZeroNotAllowed
        }

        if (text.isNotEmpty() && text.length < 10) {
            _stateAmountPocketMoney.value = ReminderEditTextState.MobileNumberIsInvalid
        } else {
            _stateAmountPocketMoney.value = ReminderEditTextState.MobileNumberIsValid
        }
    }

    sealed class PocketMoneyReminderState {
        object Loading : PocketMoneyReminderState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : PocketMoneyReminderState()
        data class Success(var dataItem: Data?) :
            PocketMoneyReminderState()
    }

    sealed class ReminderEditTextState {
        object LessThanTen : ReminderEditTextState()
        object GreaterThanFiveThousand : ReminderEditTextState()
        object GreaterThanTenLessThanFiveThousand : ReminderEditTextState()
        object MobileNumberIsInvalid : ReminderEditTextState()
        object MobileNumberIsValid : ReminderEditTextState()
        object MobileNumberZeroNotAllowed : ReminderEditTextState()
    }

    sealed class AddReminderEvents {
        object PickContactFromContactBookEvent : AddReminderEvents()
    }

}