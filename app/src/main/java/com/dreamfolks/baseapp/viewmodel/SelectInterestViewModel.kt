package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.application.PockketApplication
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.InterestRepository
import com.dreamfolks.baseapp.model.CustomerInfoResponse
import com.dreamfolks.baseapp.model.InterestEntity
import com.dreamfolks.baseapp.model.UpdateProfileRequest
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.view.adapter.ChooseInterestAdapter

/*
* This is used to handle contacts
* */
class SelectInterestViewModel(application: Application) : BaseViewModel(application) {
    var chooseInterestAdapter = ChooseInterestAdapter(this)
    var noDataFoundVisibility = ObservableField(false)
    var onUpdateProfileSuccess = MutableLiveData<Boolean>()
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var dob = MutableLiveData<String>()
    var dobForServer = MutableLiveData<String>()
    var onDobClicked = MutableLiveData(false)
    var majorMinorText = ObservableField<String>()
    var isMajorMinorVisible = ObservableField(false)
    var selectedInterestList = ArrayList<InterestEntity>()
    var interestRepository = InterestRepository(mDB = appDatabase)

    init {
        chooseInterestAdapter.setList(interestRepository.getAllInterestFromDatabase())

    }

    /*
    * This method is used to set data
    * */
    fun setData(customerInfoResponse: CustomerInfoResponse) {
        firstName.value = customerInfoResponse.firstName
        lastName.value = customerInfoResponse.lastName
    }

    /*
    * This method is used to handle click of submit
    * */
    fun onSubmitClicked() {
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
                            interest = selectedInterestList,
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


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_UPDATE_PROFILE -> {
                if (responseData is CustomerInfoResponse) {
                    val interestList=ArrayList<String>()
                    if(selectedInterestList.isNullOrEmpty()==false) {
                       selectedInterestList.forEach {
                            interestList.add(it.name!!)
                        }

                        SharedPrefUtils.putArrayList(
                            getApplication(),
                            SharedPrefUtils.SF_KEY_USER_INTEREST,interestList

                        )

                    }

                    onUpdateProfileSuccess . value =
                    true                    // set the button text to continue

                }
            }


        }
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}