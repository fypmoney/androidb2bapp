package com.fypmoney.view.register.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility


class PendingRequestVm(application: Application) : BaseViewModel(application) {


    var user = MutableLiveData<CustomerInfoResponseDetails>()

    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},"
    )

    fun onRefresh() {


        callGetCustomerProfileApi()
    }

    fun callGetCustomerProfileApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_GET_CUSTOMER_INFO,
                endpoint = NetworkUtil.endURL(ApiConstant.API_GET_CUSTOMER_INFO),
                request_type = ApiUrl.GET,
                onResponse = this, isProgressBar = false,
                param = ""
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_CUSTOMER_INFO -> {
                if (responseData is CustomerInfoResponse) {
                    Utility.saveCustomerDataInPreference(responseData.customerInfoResponseDetails)
                    SharedPrefUtils.putString(
                        PockketApplication.instance,
                        SharedPrefUtils.SF_KYC_TYPE,responseData.customerInfoResponseDetails?.bankProfile?.kycType)
                    user.postValue(responseData.customerInfoResponseDetails!!)
                }
            }

        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}