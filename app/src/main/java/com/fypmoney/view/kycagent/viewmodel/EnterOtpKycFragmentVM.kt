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
import com.fypmoney.model.VerifyOtp
import com.fypmoney.view.kycagent.model.OtpData
import com.fypmoney.view.kycagent.model.OtpVerifyData
import com.fypmoney.view.kycagent.model.OtpVerifyResponse
import com.fypmoney.view.kycagent.model.SendOtpResponse

class EnterOtpKycFragmentVM(application: Application) : BaseViewModel(application) {

    val state: LiveData<OtpVerifyState>
        get() = _state
    private val _state = MutableLiveData<OtpVerifyState>()

    val otpVerifyState: LiveData<ResendOtpState>
        get() = _otpVerifyState
    private val _otpVerifyState = MutableLiveData<ResendOtpState>()

    var mobileNumber : String ?= null

    fun verifyOtp(otpIdentifier: String, otp: String){
        val verifyOtp = VerifyOtp(
            otpIdentifier = otpIdentifier,
            otp = otp
        )
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_VERIFY_OTP_KYC,
                NetworkUtil.endURL(ApiConstant.API_VERIFY_OTP_KYC),
                ApiUrl.POST,
                verifyOtp,
                this, isProgressBar = true
            )
        )
    }

    fun resendOtp(){
        //ResendOtpState
    }

    fun resendMobileNumberForOtp(mobileNumber: String){
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
            ApiConstant.API_VERIFY_OTP_KYC -> {
                if (responseData is OtpVerifyResponse){
                    _state.value = responseData.data?.let { OtpVerifyState.Success(it) }
                }
            }

            ApiConstant.API_SEND_OTP_KYC ->{
                if (responseData is SendOtpResponse) {
                    _otpVerifyState.value = responseData.data?.let { ResendOtpState.Success(it) }
                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when(purpose){
            ApiConstant.API_VERIFY_OTP_KYC -> {
                _state.value = OtpVerifyState.Error(errorResponseInfo)
            }
            ApiConstant.API_SEND_OTP_KYC ->{
                    _otpVerifyState.value = ResendOtpState.Error(errorResponseInfo)
            }
        }
    }

    sealed class OtpVerifyState{
        object Loading : OtpVerifyState()
        data class Error(val errorResponseInfo: ErrorResponseInfo) : OtpVerifyState()
        data class Success(val otpVerifyData: OtpVerifyData) : OtpVerifyState()
    }

    sealed class ResendOtpState{
        object Loading : ResendOtpState()
        data class Error(var errorResponseInfo: ErrorResponseInfo) : ResendOtpState()
        data class Success(var otpData: OtpData) : ResendOtpState()
    }

}