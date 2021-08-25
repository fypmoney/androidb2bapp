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
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.ProfileImageUploadResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import okhttp3.MultipartBody

/*
* This is used to handle user profile
* */
class UserProfileViewModel(application: Application) : BaseViewModel(application) {
    var userNameValue = ObservableField<String>()
    var lastName = ObservableField<String>()
    var dob = ObservableField<String>()
    var phone = ObservableField<String>()
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

    /*

 *This method is used to call profile pic upload api
 * */
    fun callProfilePicUploadApi(image: MultipartBody.Part) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_UPLOAD_PROFILE_PIC,
                endpoint = NetworkUtil.endURL(ApiConstant.API_UPLOAD_PROFILE_PIC),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = BaseRequest()
            ), image = image
        )
    }

    /*
      *This method is used to call profile click
      * */
    fun onProfileClicked() {
        onProfileClicked.value = true
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_UPLOAD_PROFILE_PIC -> {
                if (responseData is ProfileImageUploadResponse) {
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.profileImageUploadResponseDetails?.accessUrl
                    )
                    onProfileSuccess.value=true

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
        dob.set(Utility.parseDateTime(SharedPrefUtils.getString(getApplication(), SharedPrefUtils.SF_KEY_USER_DOB),inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT5))
        phone.set(
            SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_MOBILE
            )
        )

    }


}