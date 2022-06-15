package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.UserTrackr
import com.fyp.trackr.models.push
import com.fyp.trackr.models.setDateOfBirthDate
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.InterestEntity
import com.fypmoney.model.UpdateProfileRequest
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.moengage.core.internal.MoEConstants
import java.util.regex.Matcher
import java.util.regex.Pattern

/*
* This is used to handle account creation related functionality
* */
class CreateAccountViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)
    var onUpdateProfileSuccess = MutableLiveData<Boolean>()
    var onLoginClicked = MutableLiveData<Boolean>()
    var isEnabled = MutableLiveData(false)
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var emailId = MutableLiveData<String>()
    var dob = MutableLiveData<String>()
    var dobForServer = MutableLiveData<String>()
    var onDobClicked = MutableLiveData(false)
    var teenager = MutableLiveData(-1)
    var selectedInterestList = ArrayList<InterestEntity>()

    // this method check is email is valid or not
     fun isEmailValid(email: String?): Boolean {
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern: Pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    /*
    * This method is used to handle click of continue
    * */
    fun onContinueClicked() {
        when {
            TextUtils.isEmpty(firstName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.first_name_empty_error))
            }
            TextUtils.isEmpty(lastName.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.last_name_empty_error))
            }
            TextUtils.isEmpty(emailId.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.email_empty_error))
            }
            !isEmailValid(emailId.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.email_valid_error))
            }

            else -> {
                isEnabled.value = true
                var age_type = ""
                if (teenager.value == 2) {
                    age_type = "PARENT"
                } else if (teenager.value == 1) {
                    age_type = "CHILD"
                }
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

                            firstName = firstName.value?.trim(),
                            lastName = lastName.value?.trim(),
                            email = emailId.value?.trim(),
                            mobile = SharedPrefUtils.getString(
                                getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE
                            ),

                            cityName = age_type
                        )
                    )
                )

            }
        }
    }

    /*
 * This method is used to handle click of date of birth
 * */
    fun onDobClicked() {
        onDobClicked.value = true
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
                        SharedPrefUtils.SF_KEY_USER_DOB, dobForServer.value?.trim()
                    )

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_EMAIL,
                        responseData.customerInfoResponseDetails?.email
                    )

                    // again update the saved data in preference
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)

                    val map = hashMapOf<String, Any>()

                    map[MoEConstants.USER_ATTRIBUTE_USER_FIRST_NAME] =
                        responseData.customerInfoResponseDetails?.firstName.toString()
                    map[MoEConstants.USER_ATTRIBUTE_USER_LAST_NAME] =
                        responseData.customerInfoResponseDetails?.lastName.toString()
                    map[MoEConstants.USER_ATTRIBUTE_USER_EMAIL] = responseData.customerInfoResponseDetails!!.email.toString()

                    UserTrackr.push(map)
                    responseData.customerInfoResponseDetails?.userProfile?.dob?.let {
                        UserTrackr.setDateOfBirthDate(
                            it
                        )
                    }

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

                    onUpdateProfileSuccess.value =
                        true                    // set the button text to continue

                }
            }


        }
    }


}