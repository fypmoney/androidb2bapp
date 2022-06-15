package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.KycInitRequest
import com.fypmoney.model.KycInitResponse
import com.fypmoney.model.KycInitResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This class is used for handling aadhaar verification
* */
class AadhaarVerificationViewModel(application: Application) : BaseViewModel(application) {
    var aadhaarNumber = MutableLiveData<String>()
    var clickHere = MutableLiveData<Boolean>()
    var kycUpgraded = MutableLiveData<Boolean>()
    var onKycInitSuccess = MutableLiveData<KycInitResponseDetails>()
    var onVerificationFailed = MutableLiveData<String>()

    init {
        /*trackr {
            it.name = TrackrEvent.bank_verification
        }*/
    }
    /*
    * This method is used to handle click of get otp
    * */
    fun onGetOtpClicked() {
        when {
            TextUtils.isEmpty(aadhaarNumber.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.aadhaar_number_empty_error))
            }
            aadhaarNumber.value?.length!! < 12-> {
                Utility.showToast(PockketApplication.instance.getString(R.string.aadhaar_number_valid_error))
            }

            else -> {
                trackr {
                    it.name = TrackrEvent.get_otp_on_aadhar_clicked
                }
                callKycInitApi()
            }
        }

    }

    fun clickHere(){
        clickHere.value = true
    }
    /*
      * This method is used to call kyc init api
      * */
    private fun callKycInitApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_UPGRADE_KYC_ACCOUNT,
                NetworkUtil.endURL(ApiConstant.API_UPGRADE_KYC_ACCOUNT),
                ApiUrl.PUT,
                KycInitRequest(
                    kycMode = AppConstants.KYC_MODE,
                    kycType = AppConstants.KYC_TYPE,
                    documentIdentifier = aadhaarNumber.value!!.replace(" ",""),
                    documentType = AppConstants.KYC_DOCUMENT_TYPE,
                    action = AppConstants.KYC_ACTION_ADHAR_AUTH
                ), this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_UPGRADE_KYC_ACCOUNT -> {
                if (responseData is KycInitResponse) {
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_AADHAAR_NUMBER,
                        responseData.kycInitResponseDetails.documentIdentifier
                    )


                    if(responseData.kycInitResponseDetails.showAdharOtpVerificationScreen==AppConstants.YES){
                        onKycInitSuccess.value=responseData.kycInitResponseDetails

                    }else if(responseData.kycInitResponseDetails.showAdharOtpVerificationScreen==AppConstants.NO
                        && responseData.kycInitResponseDetails.kycType==AppConstants.SEMI){
                        SharedPrefUtils.putString(PockketApplication.instance,
                            SharedPrefUtils.SF_KYC_TYPE,responseData.kycInitResponseDetails.kycType)
                        kycUpgraded.value  = true
                    }


                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        if(errorResponseInfo.errorCode.equals(AppConstants.AADHAR_VERIFICATION_ERROR_CODE)){
            onVerificationFailed.value = AppConstants.API_FAIL
        }
    }

}