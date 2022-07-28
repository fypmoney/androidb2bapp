package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.view.giftcard.model.GiftStatusResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used to handle add money functionality
* */
class GiftDetailsModel(application: Application) : BaseViewModel(application) {

    init {

    }


    var giftDetailsResponse: MutableLiveData<GiftStatusResponse> = MutableLiveData()


    fun callVoucherStatus(id: Int?) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.GET_GIFT_VOUCHER_DETAILS,
                endpoint = NetworkUtil.endURL(ApiConstant.GET_GIFT_VOUCHER_DETAILS) + id,
                request_type = ApiUrl.GET,
                param = com.fypmoney.model.BaseRequest(), onResponse = this,
                isProgressBar = true
            )

        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.GET_GIFT_VOUCHER_DETAILS -> {
                responseData.toString()
                val json = JsonParser.parseString(responseData.toString()) as JsonObject


                val obj = Gson().fromJson(
                    json.get("data"),
                    GiftStatusResponse::class.java
                )


                giftDetailsResponse.postValue(obj)
            }


        }
    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false

        when (purpose) {


        }
    }

}


