package com.fypmoney.view.register.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.IsAppUserResponse
import com.fypmoney.util.Utility

class InviteParentSiblingVM(application: Application) : BaseViewModel(application) {


    var user = MutableLiveData<IsAppUserResponse>()
    var toolbarTitle = MutableLiveData(
        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},"
    )



    fun callIsAppUserApi(phone: String) {

        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_CHECK_IS_APP_USER,
                endpoint = NetworkUtil.endURL(
                    ApiConstant.API_CHECK_IS_APP_USER + phone
                ),
                request_type = ApiUrl.GET,
                onResponse = this,
                isProgressBar = true,
                param = BaseRequest()
            )
        )


    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_CHECK_IS_APP_USER -> {
                if (responseData is IsAppUserResponse) {

                    user.postValue(responseData)
                }
            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }


}