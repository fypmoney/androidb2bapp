package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.NotificationAdapter
import com.fypmoney.view.adapter.UserTimeLineAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used for handling notification
* */
class NotificationViewModel(application: Application) : BaseViewModel(application),
    NotificationAdapter.OnNotificationClickListener {
    var notificationAdapter = NotificationAdapter(this, this)
    var userTimeLineAdapter = UserTimeLineAdapter(this)
    var noDataFoundVisibility = ObservableField(false)
    var isPreviousVisible = ObservableField(false)
    var isTimeLineNoDataVisible = ObservableField(false)
    var error: MutableLiveData<String> = MutableLiveData()
    var isGetNotificationsRecyclerVisible = ObservableField(true)
    var positionSelected = ObservableField<Int>()
    var onNotificationClicked = MutableLiveData<Boolean>()
    var onPaySuccess = MutableLiveData<Boolean>()
    var notificationSelectedResponse = NotificationModel.NotificationResponseDetails()
    val isLoading = ObservableBoolean()

    init {
        callGetFamilyNotificationApi()
        callUserTimeLineApi()
    }

    var bottomSheetStatus: MutableLiveData<UpdateTaskGetResponse> = MutableLiveData()


    /*
      * This method is used to refresh on swipe
      *  */
    fun onRefresh() {
        isLoading.set(true)
        callGetFamilyNotificationApi()
        callUserTimeLineApi()
    }
    /*
      * This method is used to call get family notification API
      * */

    private fun callGetFamilyNotificationApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_NOTIFICATION_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_NOTIFICATION_LIST),
                request_type = ApiUrl.POST,
                param = NotificationModel.NotificationRequest(), onResponse = this,
                isProgressBar = true
            )
        )
    }

    fun callTaskAccept(state: String, entityId: String?, msg: String) {


        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_TASK_UPDATE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_TASK_UPDATE),
                request_type = ApiUrl.PUT,
                SendTaskResponse(
                    state = state, taskId = entityId,
                    emojis = "\u1F600",
                    comments = msg
                ), onResponse = this,
                isProgressBar = false
            )

        )
    }
    /*
      * This method is used to call user timeline API
      * */

    private fun callUserTimeLineApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_USER_TIMELINE,
                endpoint = NetworkUtil.endURL(ApiConstant.API_USER_TIMELINE),
                request_type = ApiUrl.GET,
                param = BaseRequest(), onResponse = this,
                isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        isLoading.set(false)

        when (purpose) {
            ApiConstant.API_GET_NOTIFICATION_LIST -> {
                if (responseData is NotificationModel.NotificationResponse) {
                    if (responseData.notificationResponseDetails.isNullOrEmpty()) {
                        isGetNotificationsRecyclerVisible.set(false)
                    } else {
                        isGetNotificationsRecyclerVisible.set(true)
                        notificationAdapter.setList(responseData.notificationResponseDetails)
                    }
                }
            }
            ApiConstant.API_USER_TIMELINE -> {
                if (responseData is NotificationModel.UserTimelineResponse) {
                    isPreviousVisible.set(true)
                    if (responseData.notificationResponseDetails.isNullOrEmpty()) {
                        isTimeLineNoDataVisible.set(true)
                    } else {

                        userTimeLineAdapter.setList(responseData.notificationResponseDetails)
                    }
                }
            }
            ApiConstant.API_UPDATE_APPROVAL_REQUEST -> {
                if (responseData is UpdateFamilyApprovalResponse) {
                    Utility.showToast(responseData.msg)
                    responseData.notificationResponseDetails.let {
                        notificationAdapter.updateList(
                            notification = responseData.notificationResponseDetails,
                            position = positionSelected.get()!!
                        )
                        if (notificationAdapter.itemCount == 0) {

                            isTimeLineNoDataVisible.set(true)
                        }
                    }


                }
            }
            ApiConstant.API_TASK_UPDATE -> {
                Log.d("chackupdate", responseData.toString())

                val json = JsonParser().parse(responseData.toString()) as JsonObject
                val task = Gson().fromJson(json.get("data"), UpdateTaskGetResponse::class.java)

                bottomSheetStatus.postValue(task)


            }
            ApiConstant.API_PAY_MONEY -> {
                if (responseData is PayMoneyResponse) {
                    Utility.showToast(responseData.msg)

                    onPaySuccess.value = true


                }
            }


        }

    }

    /*
    * This method is used to call get approval request api
    * */
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

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        isLoading.set(false)
        when (purpose) {
            ApiConstant.API_TASK_UPDATE -> {
                error.postValue(errorResponseInfo.errorCode)
            }
        }


    }

    override fun onNotificationClick(
        notification: NotificationModel.NotificationResponseDetails?,
        position: Int
    ) {
        positionSelected.set(position)
        notificationSelectedResponse = notification!!
        onNotificationClicked.value = true


    }

}