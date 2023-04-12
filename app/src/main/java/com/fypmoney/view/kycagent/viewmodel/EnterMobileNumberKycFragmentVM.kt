package com.fypmoney.view.kycagent.viewmodel

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
import com.fypmoney.view.kycagent.model.OtpData
import com.fypmoney.view.kycagent.model.SendOtpResponse

class EnterMobileNumberKycFragmentVM(application: Application) : BaseViewModel(application) {

    val state : LiveData<EnterMobileNumberKycState>
        get() = _state
    private val _state = MutableLiveData<EnterMobileNumberKycState>()

    fun sendMobileNumberForOtp(mobileNumber: String){
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_SEND_OTP_KYC,
                endpoint = NetworkUtil.endURL(ApiConstant.API_SEND_OTP_KYC) + mobileNumber,
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        
        when(purpose){
            ApiConstant.API_SEND_OTP_KYC -> {
                if (responseData is SendOtpResponse) {
                    _state.value = responseData.data?.let { EnterMobileNumberKycState.Success(it) }
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when(purpose){
            ApiConstant.API_SEND_OTP_KYC -> {
                _state.value = EnterMobileNumberKycState.Error(errorResponseInfo)
            }
        }
    }

    sealed class EnterMobileNumberKycState{
        object Loading : EnterMobileNumberKycState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : EnterMobileNumberKycState()
        data class Success(var otpData: OtpData) : EnterMobileNumberKycState()
    }

}