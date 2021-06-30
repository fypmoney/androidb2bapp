package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.Utility

/*
* This is used to handle user profile
* */
class UserProfileViewModel(application: Application) : BaseViewModel(application) {
    var userName = ObservableField<String>()
    var dob = ObservableField<String>()
    var phone = ObservableField<String>()
    var buildVersion = ObservableField<String>()
    var onLogoutSuccess = MutableLiveData<Boolean>()


    /*
   *This method is used to call log out Api
   * */
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


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)


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