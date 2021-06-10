package com.fypmoney.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
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
import com.fypmoney.database.InterestRepository
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.InterestEntity
import com.fypmoney.model.UpdateProfileRequest
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.ChooseInterestAdapter

/*
* This is used to handle account creation related functionality
* */
class CreateAccountViewModel(application: Application) : BaseViewModel(application) {
    var noDataFoundVisibility = ObservableField(false)
    var onUpdateProfileSuccess = MutableLiveData<Boolean>()
    var onLoginClicked = MutableLiveData<Boolean>()
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var dob = MutableLiveData<String>()
    var dobForServer = MutableLiveData<String>()
    var onDobClicked = MutableLiveData(false)
    var majorMinorText = ObservableField<String>()
    var isMajorMinorVisible = ObservableField(false)
    var buttonColor = ObservableField(false)
    var selectedInterestList = ArrayList<InterestEntity>()
    var interestRepository = InterestRepository(mDB = appDatabase)


    /*
    * This method is used to set data
    * */
    fun setData(customerInfoResponse: CustomerInfoResponse) {
        firstName.value = customerInfoResponse.firstName
        lastName.value = customerInfoResponse.lastName
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
            TextUtils.isEmpty(dob.value) -> {
                Utility.showToast(PockketApplication.instance.getString(R.string.dob_empty_error))
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
                            firstName = firstName.value?.trim(),
                            lastName = lastName.value?.trim(),
                            mobile = SharedPrefUtils.getString(
                                getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE
                            ),
                            dob = dobForServer.value?.trim()
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

    /*
    * This method is used to handle click of login
    * */
    fun onLoginClicked() {
        onLoginClicked.value = true
    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_UPDATE_PROFILE -> {
                if (responseData is CustomerInfoResponse) {

                    // save first name, last name, date of birth

                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_FIRST_NAME, responseData.firstName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_LAST_NAME, responseData.lastName
                    )
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_USER_DOB, dobForServer.value?.trim()
                    )

                    // again update the saved data in preference
                    Utility.saveCustomerDataInPreference(responseData)


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

                    onUpdateProfileSuccess.value = true                    // set the button text to continue

                }
            }


        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}