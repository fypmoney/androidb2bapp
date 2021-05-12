package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.FamilyNotificationAdapter
import com.fypmoney.view.adapter.FeedsAdapter

/*
* This class is used for handling notification for family
* */
class FamilyNotificationViewModel(application: Application) : BaseViewModel(application) {
    var notificationAdapter = FamilyNotificationAdapter(this)
    var noDataFoundVisibility = ObservableField(false)
    var positionSelected = ObservableField<Int>()

    init {
        callGetFamilyNotificationApi()
    }

    /*
      * This method is used to call get family notification API
      * */

    private fun callGetFamilyNotificationApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_FAMILY_NOTIFICATION_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_FAMILY_NOTIFICATION_LIST),
                request_type = ApiUrl.POST,
                param = FamilyNotificationRequest(), onResponse = this,
                isProgressBar = true
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_FAMILY_NOTIFICATION_LIST -> {
                if (responseData is FamilyNotificationResponse) {
                    if (responseData.familyNotificationResponseDetails.isNullOrEmpty()) {
                        noDataFoundVisibility.set(true)
                    } else {
                        notificationAdapter.setList(responseData.familyNotificationResponseDetails)
                    }
                }
            }

            ApiConstant.API_UPDATE_APPROVAL_REQUEST -> {
                if (responseData is UpdateFamilyApprovalResponse) {
                    responseData.familyNotificationResponseDetails.let {
                        notificationAdapter.updateList(
                            notification = responseData.familyNotificationResponseDetails,
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
        position: Int,
        familyList: FamilyNotificationResponseDetails,
        actionAllowed: String
    ) {
        positionSelected.set(position)
        familyList.actionSelected = actionAllowed
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_UPDATE_APPROVAL_REQUEST,
                endpoint = NetworkUtil.endURL(ApiConstant.API_UPDATE_APPROVAL_REQUEST),
                request_type = ApiUrl.PUT,
                param = familyList, onResponse = this,
                isProgressBar = true
            )
        )
    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

}