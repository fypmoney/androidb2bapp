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
import com.fypmoney.model.*
import com.fypmoney.view.adapter.NotificationAdapter
import com.fypmoney.view.adapter.UserTimeLineAdapter

/*
* This class is used for handling notification
* */
class NotificationViewModel(application: Application) : BaseViewModel(application),
    NotificationAdapter.OnNotificationClickListener {
    var notificationAdapter = NotificationAdapter(this, this)
    var userTimeLineAdapter = UserTimeLineAdapter(this)
    var noDataFoundVisibility = ObservableField(false)
    var isTimeLineRecyclerVisible = ObservableField(false)
    var positionSelected = ObservableField<Int>()
    var onNotificationClicked = MutableLiveData<Boolean>()
    var notificationSelectedResponse =
        ObservableField<NotificationModel.NotificationResponseDetails>()

    init {
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
        when (purpose) {
            ApiConstant.API_GET_NOTIFICATION_LIST -> {
                if (responseData is NotificationModel.NotificationResponse) {
                    if (responseData.notificationResponseDetails.isNullOrEmpty()) {
                        // noDataFoundVisibility.set(true)
                    } else {
                        notificationAdapter.setList(responseData.notificationResponseDetails)
                    }
                }
            }
            ApiConstant.API_USER_TIMELINE -> {
                if (responseData is NotificationModel.NotificationResponse) {
                    if (responseData.notificationResponseDetails.isNullOrEmpty()) {
                        isTimeLineRecyclerVisible.set(false)
                    } else {
                        isTimeLineRecyclerVisible.set(true)
                        userTimeLineAdapter.setList(responseData.notificationResponseDetails)
                    }
                }
            }
            ApiConstant.API_UPDATE_APPROVAL_REQUEST -> {
                if (responseData is UpdateFamilyApprovalResponse) {
                    responseData.notificationResponseDetails.let {
                        notificationAdapter.updateList(
                            notification = responseData.notificationResponseDetails,
                            position = positionSelected.get()!!
                        )
                        if (notificationAdapter.itemCount == 0) {
                            noDataFoundVisibility.set(true)
                        }
                    }


                }
            }


        }

    }

    fun callUpdateApprovalRequestApi(
        actionAllowed: String
    ) {
        notificationSelectedResponse.get()?.actionSelected = actionAllowed
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

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    override fun onNotificationClick(
        notification: NotificationModel.NotificationResponseDetails?,
        position: Int
    ) {
        positionSelected.set(position)
        notificationSelectedResponse.set(notification)
        onNotificationClicked.value = true


    }

}