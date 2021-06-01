package com.fypmoney.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.ContactRepository
import com.fypmoney.model.ContactRequest
import com.fypmoney.model.ContactRequestDetails
import com.fypmoney.model.ContactResponse
import com.fypmoney.model.NotificationModel

/*
* This is used as a home screen
* */
class HomeViewModel(application: Application) : BaseViewModel(application) {
    var mobile = MutableLiveData<String>()
    var onFeedClicked = MutableLiveData<Boolean>()
    var onLocationClicked = MutableLiveData<Boolean>()
    var onSubmitClicked = MutableLiveData<Boolean>()
    var onProfileClicked = MutableLiveData<Boolean>()
    var onNotificationClicked = MutableLiveData<Boolean>()
    var onNotificationListener = MutableLiveData<NotificationModel.NotificationResponseDetails?>()
    var balanceAmount = ObservableField(application.getString(R.string.balance_default_value))
    var otp = ObservableField<String>()
    var headerText = ObservableField<String>()
    var isScanVisible = ObservableField(true)
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
      * This method is used to handle click of notifications for the family
     * */
    fun onNotificationClicked() {
        onNotificationClicked.value = true


    }

    /*
  * This method is used to call get family notification API
  * */

     fun callGetFamilyNotificationApi(aprid: String?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_NOTIFICATION_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_NOTIFICATION_LIST),
                request_type = ApiUrl.POST,
                param = NotificationModel.NotificationRequest(id = aprid), onResponse = this,
                isProgressBar = true
            )
        )
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
            ApiConstant.API_GET_NOTIFICATION_LIST -> {
                if (responseData is NotificationModel.NotificationResponse) {
                    onNotificationListener.value=responseData.notificationResponseDetails[0]

                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

}