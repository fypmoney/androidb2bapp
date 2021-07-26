package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.InterestEntity
import com.fypmoney.model.UpdateProfileRequest
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility

/*
* This class is used for handling community
* */
class CommunityViewModel(application: Application) : BaseViewModel(application) {
    var schoolName = MutableLiveData<String>()
    var cityName = MutableLiveData<String>()
    var onContinueClicked = MutableLiveData<Boolean>()
    var selectedInterestList = ArrayList<InterestEntity>()

    /*
* This method is used to handle click of continue
* */
    fun onContinueClicked() {
        when {
            TextUtils.isEmpty(schoolName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.school_name_empty_error))
            }
            TextUtils.isEmpty(cityName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.city_name_empty_error))
            }

            else -> {
                WebApiCaller.getInstance().request(
                    ApiRequest(
                        purpose = ApiConstant.API_UPDATE_PROFILE,
                        endpoint = NetworkUtil.endURL(ApiConstant.API_UPDATE_PROFILE),
                        request_type = ApiUrl.PUT,
                        onResponse = this, isProgressBar = true,
                        param = UpdateProfileRequest(
                            userId = SharedPrefUtils.getLong(
                                getApplication(), key = SharedPrefUtils.SF_KEY_USER_ID
                            ),
                            cityName = cityName.value?.trim(),
                            schoolName = cityName.value?.trim()
                        )
                    )
                )

            }
        }
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_UPDATE_PROFILE -> {
                if (responseData is CustomerInfoResponse) {

                    // save first name, last name, date of birth

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_FIRST_NAME,
                        responseData.customerInfoResponseDetails?.firstName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_LAST_NAME,
                        responseData.customerInfoResponseDetails?.lastName
                    )

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.customerInfoResponseDetails?.profilePicResourceId
                    )



                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_EMAIL,
                        responseData.customerInfoResponseDetails?.email
                    )

                    // again update the saved data in preference
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)


                    val interestList = ArrayList<String>()
                    if (!selectedInterestList.isNullOrEmpty()) {
                        selectedInterestList.forEach {
                            interestList.add(it.name!!)
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST, interestList

                        )

                    }

                    onContinueClicked.value =
                        true

                }
            }


        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}