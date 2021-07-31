package com.fypmoney.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.BaseRequest
import com.fypmoney.model.GetAllProductsResponse
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import java.lang.Exception

/*
* This is used for card ordering
* */
class OrderCardViewModel(application: Application) : BaseViewModel(application) {
    var onOrderCardClicked = MutableLiveData(false)
    var imageUrl = MutableLiveData<String>()
    /*
    * This is used to handle order card
    * */

    init {
        if (Utility.getCustomerDataFromPreference()?.cardProductCode == null) {
            callGetAllProductsApi()
        } else {
            callGetAllProductsByCodeApi()
        }
    }

    fun onOrderCardClicked() {
        onOrderCardClicked.value = true
    }

    /*
    * This method is used to get all cards
    * */
    private fun callGetAllProductsApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ALL_PRODUCTS,
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_PRODUCTS + AppConstants.ORDER_CARD_PHYSICAL_CARD_CODE),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    /*
    * This method is used to get all cards
    * */
    private fun callGetAllProductsByCodeApi() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE,
                NetworkUtil.endURL(ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE + Utility.getCustomerDataFromPreference()?.cardProductCode),
                ApiUrl.GET,
                BaseRequest(),
                this, isProgressBar = false
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_GET_ALL_PRODUCTS, ApiConstant.API_GET_ALL_PRODUCTS_BY_CODE -> {
                if (responseData is GetAllProductsResponse) {
                    if (!responseData.getAllProductsResponseDetails?.isNullOrEmpty()!!) {
                        try {
                            val imgList =
                                responseData.getAllProductsResponseDetails[0].detailImage?.split(",")
                            imageUrl.value = imgList?.get(0)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

            }
        }
    }
}