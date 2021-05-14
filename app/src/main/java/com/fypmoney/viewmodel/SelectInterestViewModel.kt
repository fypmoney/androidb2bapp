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
import com.fypmoney.database.InterestRepository
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.InterestEntity
import com.fypmoney.model.InterestResponse
import com.fypmoney.model.UpdateProfileRequest
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.ChooseInterestAdapter

/*
* This is used to handle contacts
* */
class SelectInterestViewModel(application: Application) : BaseViewModel(application) {
    var chooseInterestAdapter = ChooseInterestAdapter(this)
    var noDataFoundVisibility = ObservableField(false)
    var onUpdateProfileSuccess = MutableLiveData<Boolean>()
    var dobForServer = MutableLiveData<String>()
    var selectedInterestList = ArrayList<InterestEntity>()
    var interestRepository = InterestRepository(mDB = appDatabase)

    init {
        callGetInterestApi()

    }


    /*
       * This method is used to call get interest API
       * */

    private fun callGetInterestApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_INTEREST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_INTEREST),
                request_type = ApiUrl.GET,
                param = "", onResponse = this,
                isProgressBar = true
            )
        )


    }

    /*
    * This method is used to handle click of submit
    * */
    fun onSubmitClicked() {
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
                    firstName = SharedPrefUtils.getString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_FIRST_NAME
                    ),
                    lastName = SharedPrefUtils.getString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_LAST_NAME
                    ),
                    mobile = SharedPrefUtils.getString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_MOBILE
                    ),
                    dob = SharedPrefUtils.getString(
                        getApplication(), key = SharedPrefUtils.SF_KEY_USER_DOB
                    ),
                )
            )
        )

    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.API_GET_INTEREST -> {
                if (responseData is InterestResponse) {
                    chooseInterestAdapter.setList(responseData.interestDetails)

                }
            }
            ApiConstant.API_UPDATE_PROFILE -> {
                if (responseData is CustomerInfoResponse) {
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

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}