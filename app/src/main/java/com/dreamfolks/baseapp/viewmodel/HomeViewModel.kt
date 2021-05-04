package com.dreamfolks.baseapp.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseViewModel
import com.dreamfolks.baseapp.connectivity.ApiConstant
import com.dreamfolks.baseapp.connectivity.ApiUrl
import com.dreamfolks.baseapp.connectivity.ErrorResponseInfo
import com.dreamfolks.baseapp.connectivity.network.NetworkUtil
import com.dreamfolks.baseapp.connectivity.retrofit.ApiRequest
import com.dreamfolks.baseapp.connectivity.retrofit.WebApiCaller
import com.dreamfolks.baseapp.database.ContactRepository
import com.dreamfolks.baseapp.database.entity.ContactEntity
import com.dreamfolks.baseapp.model.ContactRequest
import com.dreamfolks.baseapp.model.ContactRequestDetails
import com.dreamfolks.baseapp.model.ContactResponse
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.util.Utility

/*
* This is used as a home screen
* */
class HomeViewModel(application: Application) : BaseViewModel(application) {
    var mobile = MutableLiveData<String>()
    var onFeedClicked = MutableLiveData<Boolean>()
    var onLocationClicked = MutableLiveData<Boolean>()
    var onSubmitClicked = MutableLiveData<Boolean>()
    var onProfileClicked = MutableLiveData<Boolean>()
    var balanceAmount = ObservableField(application.getString(R.string.balance_default_value))
    var otp = ObservableField<String>()
    var contactRepository = ContactRepository(mDB = appDatabase)


    /*
    * This method is used to handle click of mobile
    * */
    fun onFeedClicked() {
        onFeedClicked.value = true
    }

    /*
  * This method is used to handle click of mobile
  * */
    fun onLocationClicked() {
        onLocationClicked.value = true
    }

    /*
       * This method is used to handle click of submit
       * */
    fun onSubmitClicked() {
        onSubmitClicked.value = true
    }

    /*
          * This method is used to handle click of user profile
          * */
    fun onProfileClicked() {
        onProfileClicked.value = true

    }

    /*
* This method is used to get all the contacts
* */
    fun getAllContacts(contentResolver: ContentResolver) {



    }

    /*
* This method is used to call contact sync API
* */ fun callContactSyncApi() {
        val contactRequestDetailsList = mutableListOf<ContactRequestDetails>()
        contactRepository.getAllNotSyncedContacts().forEachIndexed { index, s ->
            if (s.contactNumber?.length!! > 9) {
                contactRequestDetailsList.add(s)
            }

        }
       // Log.d("contacts","step5_callContactSyncApi")

        if (!contactRequestDetailsList.isNullOrEmpty()) {
            WebApiCaller.getInstance().request(
                ApiRequest(
                    ApiConstant.API_SNC_CONTACTS,
                    NetworkUtil.endURL(ApiConstant.API_SNC_CONTACTS),
                    ApiUrl.POST,
                    ContactRequest(
                        contactRequestDetails = contactRequestDetailsList
                    ),
                    this,
                    isProgressBar = false
                )
            )
        }


    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_SNC_CONTACTS -> {
                if (responseData is ContactResponse) {
                    // it update the sync status of the contacts which are synced to server and also update the is app user status based on server response
                    contactRepository.updateIsSyncAndIsAppUserStatus(responseData.contactResponseDetails?.userPhoneContact)
                }
            }


        }

    }

    override fun onError(purpose: String,errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose,errorResponseInfo)
    }

}