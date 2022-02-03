package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.ProfileImageUploadResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_KYC_TYPE
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
    var kycType = ObservableField<String>("Minimum")
    var upgradeKyc = ObservableField(true)
    var verified = ObservableField(false)

    var buildVersion = ObservableField<String>()
    var profilePic = ObservableField<String>()
    var onLogoutSuccess = MutableLiveData<Boolean>()
    var onProfileSuccess = MutableLiveData<Boolean>()
    var onProfileClicked = MutableLiveData<Boolean>()
    var onUpgradeKycClicked = MutableLiveData<Boolean>()

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
    fun onUpgradeKycClicked() {
        onUpgradeKycClicked.value = true
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
                    trackr {
                        it.name = TrackrEvent.profile_image
                    }
                    onProfileSuccess.value = true

                }
            }
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {


                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)

                    // Save the user id in shared preference

                    // save first name, last name, date of birth

                    SharedPrefUtils.putString(PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.kycType)

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_FIRST_NAME,
                        responseData.customerInfoResponseDetails?.firstName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.customerInfoResponseDetails?.profilePicResourceId
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_LAST_NAME,
                        responseData.customerInfoResponseDetails?.lastName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_DOB,
                        responseData.customerInfoResponseDetails?.userProfile?.dob
                    )

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_EMAIL,
                        responseData.customerInfoResponseDetails?.email
                    )

                    // again update the saved data in preference
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)
                    responseData.customerInfoResponseDetails?.id?.let {
                        SharedPrefUtils.putLong(
                            getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID,
                            value = it
                        )
                    }
                    // Save the user phone in shared preference
                    SharedPrefUtils.putString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE,
                        value = responseData.customerInfoResponseDetails?.mobile
                    )

                    val interestList = ArrayList<String>()
                    if (responseData.customerInfoResponseDetails?.userInterests?.isNullOrEmpty() == false) {
                        responseData.customerInfoResponseDetails?.userInterests!!.forEach {
                            interestList.add(it.name!!)
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    } else {
                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    }

                    responseData.customerInfoResponseDetails?.bankProfile?.kycType?.let {
                        kycType.set(Utility.toTitleCase(it))
                        SharedPrefUtils.putString(PockketApplication.instance,SF_KYC_TYPE,it)
                        if(it=="MINIMUM"){
                            upgradeKyc.set(true)
                        }else{
                            upgradeKyc.set(false)
                        }
                    }
                    if (responseData.customerInfoResponseDetails?.bankProfile?.isAccountActive == "YES") {
                        verified.set(true)
                    } else {
                        verified.set(false)

                    }
                    setInitialData()

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
        dob.set(Utility.parseDateTime(SharedPrefUtils.getString(getApplication(), SharedPrefUtils.SF_KEY_USER_DOB),inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT5))
        phone.set(
            SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_MOBILE
            )
        )



    }


}