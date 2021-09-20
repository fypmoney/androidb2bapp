package com.fypmoney.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.multidex.BuildConfig
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.ContactRepository
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.*

/*
* This is used as a home screen
* */
class HomeViewModel(application: Application) : BaseViewModel(application) {
    var mobile = MutableLiveData<String>()
    var onFeedClicked = MutableLiveData<Boolean>()
    var error: MutableLiveData<String> = MutableLiveData()
    var onSubmitClicked = MutableLiveData<Boolean>()
    var onQrCodeClicked = MutableLiveData<Boolean>()
    var onProfileClicked = MutableLiveData<Boolean>()
    var onNotificationClicked = MutableLiveData<Boolean>()
    var onNotificationListener = MutableLiveData<NotificationModel.NotificationResponseDetails?>()
    var otp = ObservableField<String>()
    var headerText = ObservableField<String>()
    var contactRepository = ContactRepository(mDB = appDatabase)
    var notificationSelectedResponse = NotificationModel.NotificationResponseDetails()
    var amountToBeAdded:String? = ""
    var sendMoneyApiResponse = MutableLiveData<SendMoneyResponseDetails>()

    /*
    * This method is used to handle click of mobile
    * */
    fun onFeedClicked() {
        onFeedClicked.value = true
    }

    /*
  * This method is used to handle click of mobile
  * */


    /*
       * This method is used to handle click of submit
       * */
    fun onSubmitClicked() {
        onSubmitClicked.value = true
    }

    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()


    /*
     * This method is used to handle click of user profile
       * */
    fun onProfileClicked() {
        onProfileClicked.value = true

    }

    /*
        * This method is used to handle click of qr code
          * */
    fun onQrCodeClicked() {
        onQrCodeClicked.value = true
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
        Log.d("chack", aprid.toString())
        if (!aprid.isNullOrEmpty()) {
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

    }
    fun postLatlong(latitude: String, longitude: String,userId:Long) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_USER_DEVICE_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_USER_DEVICE_INFO),
                request_type = ApiUrl.PUT,
                /*UserDeviceInfo(
                    Build.BRAND,
                    Build.MODEL,
                    Build.ID,
                    TimeZone.getDefault().getDisplayName(
                        Locale.ROOT
                    ),
                    PockketApplication.instance.resources.configuration.locale.country,
                    BuildConfig.VERSION_NAME,
                    AppConstants.PLATFORM,
                    Build.VERSION.SDK_INT.toString(),
                    SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_FIREBASE_TOKEN
                    ) ?: ""
                )*/
                param = UserDeviceInfo(
                    latitude = latitude,
                    longitude = longitude,
                    userId =  userId,
                    make = Build.BRAND,
                    model = Build.MODEL,
                    modelVersion = Build.ID,
                    timezone = TimeZone.getDefault().getDisplayName(
                        Locale.ROOT
                    ),
                    locale = PockketApplication.instance.resources.configuration.locale.country,
                    dtoken = SharedPrefUtils.getString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_FIREBASE_TOKEN
                    ) ?: "",
                    isHomeViewed = "YES"

                ), onResponse = this,
                isProgressBar = false
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

    fun callTaskAccept(state: String, entityId: String?, s: String) {


        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_TASK_UPDATE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_TASK_UPDATE),
                request_type = ApiUrl.PUT,
                SendTaskResponse(
                    state = state, taskId = entityId,
                    emojis = "",
                    comments = "ACCEPTED the task"
                ), onResponse = this,
                isProgressBar = false
            )

        )
    }

    fun callUpdateApprovalRequestApi(
        actionAllowed: String
    ) {
        notificationSelectedResponse.actionSelected = actionAllowed
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_UPDATE_APPROVAL_REQUEST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_UPDATE_APPROVAL_REQUEST),
                request_type = ApiUrl.PUT,
                param = notificationSelectedResponse, onResponse = this,
                isProgressBar = true
            )
        )
    }

    /*
   * This method is used to call pay money api
   * */
    fun callPayMoneyApi(
        actionAllowed: String
    ) {
        notificationSelectedResponse.actionSelected = actionAllowed
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_PAY_MONEY,
                endpoint = NetworkUtil.endURL(ApiConstant.API_PAY_MONEY),
                request_type = ApiUrl.POST,
                param = PayMoneyRequest(
                    actionSelected = actionAllowed,
                    txnType = AppConstants.FUND_TRANSFER_TRANSACTION_TYPE,
                    approvalId = notificationSelectedResponse.id,
                    emojis = "",
                    remarks = ""
                ), onResponse = this,
                isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_SNC_CONTACTS -> {
                if (responseData is ContactResponse) {

                    // it update the sync status of the contacts which are synced to server and also update the is app user status based on server response
                    SharedPrefUtils.putString(
                        getApplication(),
                        SharedPrefUtils.SF_KEY_PROFILE_IMAGE,
                        responseData.contactResponseDetails?.profilePicResourceId
                    )
                    contactRepository.updateIsSyncAndIsAppUserStatus(responseData.contactResponseDetails?.userPhoneContact)
                }
            }
            ApiConstant.API_TASK_UPDATE -> {

                val json = JsonParser().parse(responseData.toString()) as JsonObject
                val task = Gson().fromJson(json.get("data"), UpdateTaskGetResponse::class.java)

                bottomSheetStatus.postValue(task)


            }
            ApiConstant.API_GET_NOTIFICATION_LIST -> {


                if (responseData is NotificationModel.NotificationResponse) {
                    notificationSelectedResponse = responseData.notificationResponseDetails[0]
                    onNotificationListener.value = responseData.notificationResponseDetails[0]

                }
            }

            ApiConstant.API_UPDATE_APPROVAL_REQUEST -> {

                if (responseData is UpdateFamilyApprovalResponse) {
                    Utility.showToast("Your action completed successfully")
                }
            }

            ApiConstant.API_PAY_MONEY -> {

                if (responseData is PayMoneyResponse) {

                    sendMoneyApiResponse.value = responseData.sendMoneyResponseDetails

                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_SNC_CONTACTS -> {
            }
            ApiConstant.API_TASK_UPDATE -> {
                error.postValue(errorResponseInfo.errorCode)
                amountToBeAdded = errorResponseInfo.data

            }
            ApiConstant.API_PAY_MONEY -> {
                error.postValue(errorResponseInfo.errorCode)
                amountToBeAdded = errorResponseInfo.data
            }
        }
    }

}