package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This class is used for handling aadhaar verification using otp
* */
class AadhaarAccountActivationViewModel(application: Application) : BaseViewModel(application) {
    var onActivateAccountSuccess = MutableLiveData<KycActivateAccountResponseDetails>()
    var onLogoutSuccess = MutableLiveData<Boolean>()
    var postKycScreenCode = MutableLiveData<String>()

    fun callLogOutApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_LOGOUT,
                endpoint = NetworkUtil.endURL(ApiConstant.API_LOGOUT),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = BaseRequest()
            )
        )
    }
    /*
    * This method is used to call auth login API
    * */
    fun callKycAccountActivationApi() {
        trackr { it.services = arrayListOf(TrackrServices.FIREBASE, TrackrServices.MOENGAGE)
            it.name = TrackrEvent.ACCOUNTACTIVATION
            it.add(
                TrackrField.user_id, SharedPrefUtils.getLong(
                    PockketApplication.instance,
                    SharedPrefUtils.SF_KEY_USER_ID
                ).toString())
        }
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_KYC_ACTIVATE_ACCOUNT,
                NetworkUtil.endURL(ApiConstant.API_KYC_ACTIVATE_ACCOUNT),
                ApiUrl.PUT,
                BaseRequest(),
                this, isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_KYC_ACTIVATE_ACCOUNT -> {
                if (responseData is KycActivateAccountResponse) {

                    postKycScreenCode.value =
                        responseData.kycActivateAccountResponseDetails.postKycScreenCode
                    onActivateAccountSuccess.value = responseData.kycActivateAccountResponseDetails


                }
            }
        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_LOGOUT -> {
                Utility.resetPreferenceAfterLogout()
                onLogoutSuccess.value = true
            }


        }
    }

}