package com.fypmoney.view.register.viewModel

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
import com.fypmoney.model.BaseRequest
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This is used to handle user profile
* */
class TimelineViewModel(application: Application) : BaseViewModel(application) {
    var userNameValue = ObservableField<String>()
    var lastName = ObservableField<String>()
    var dob = ObservableField<String>()
    var phone = ObservableField<String>()
    var verified = ObservableField(false)

    var buildVersion = ObservableField<String>()
    var profilePic = ObservableField<String>()
    var onLogoutSuccess = MutableLiveData<Boolean>()
    var onProfileSuccess = MutableLiveData<Boolean>()
    var onProfileClicked = MutableLiveData<Boolean>()

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
        when (purpose) {


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

    fun callGetCustomerProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CUSTOMER_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CUSTOMER_INFO),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = true,
                param = ""
            )
        )
    }

    fun setInitialData() {
        lastName.set(
            SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_LAST_NAME
            )
        )
        if (lastName.get().isNullOrEmpty()) {
            userNameValue.set(
                SharedPrefUtils.getString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_USER_FIRST_NAME
                )
            )
        } else {
            userNameValue.set(
                SharedPrefUtils.getString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_USER_FIRST_NAME
                ) + " " + lastName.get()
            )

        }
        dob.set(
            Utility.parseDateTime(
                SharedPrefUtils.getString(
                    getApplication(),
                    SharedPrefUtils.SF_KEY_USER_DOB
                ),
                inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT5
            )
        )
        phone.set(
            SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_MOBILE
            )
        )

    }


}