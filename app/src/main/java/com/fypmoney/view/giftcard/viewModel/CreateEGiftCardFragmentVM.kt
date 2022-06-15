package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.giftcard.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used to handle add money functionality
* */
class CreateEGiftCardFragmentVM(application: Application) : BaseViewModel(application) {
    var onAddClicked = MutableLiveData(false)
    var userId = SharedPrefUtils.getLong(
        application,
        SharedPrefUtils.SF_KEY_USER_ID
    )

    var selectedPosition = ObservableField(-1)
    var minValue = ObservableField(-1)
    var maxValue = ObservableField(-1)
    var flexibleAmount = ObservableField(false)


    var selectedContactFromList = ObservableField<ContactEntity>()
    var selectedGiftCard = ObservableField<VoucherProductItem>()


    var giftpurchased = MutableLiveData<PurchaseGiftCardResponse>()


    var onItemClicked = MutableLiveData<ContactEntity>()

    var availableAmount = ObservableField(application.getString(R.string.dummy_amount))
    var amountSelected = ObservableField<String>()
    var isFetchBalanceVisible = ObservableField(true)

    var onIsAppUserClicked = MutableLiveData<Boolean>()
    var onSelectClicked = MutableLiveData<Boolean>()


    fun callBrandGiftCards(orderId: String?) {

        val request = RequestGiftRequest()
//        request.brands!!.add(orderId)
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.GET_GIFTS_LIST,
                endpoint = NetworkUtil.endURL(ApiConstant.GET_GIFTS_LIST),
                request_type = ApiUrl.POST,
                RequestGiftswithPage(
                    request = request,
                    0,
                    10,
                    "id,asc"
                ), onResponse = this,
                isProgressBar = false
            )

        )


    }

    fun purchaseGiftCardRequest(orderId: PurchaseGiftCardRequest) {
        WebApiCaller.getInstance().request(
            ApiRequest(
                ApiConstant.PURCHASE_GIFT_CARD,
                NetworkUtil.endURL(ApiConstant.PURCHASE_GIFT_CARD),
                ApiUrl.POST,
                orderId,
                this, isProgressBar = true
            )
        )


    }


    fun onAddClicked() {
        amountSelected.get()?.toIntOrNull()?.let {
            when {
                TextUtils.isEmpty(amountSelected.get()) -> {
                    Utility.showToast(PockketApplication.instance.getString(R.string.add_money_empty_error))
                }

                flexibleAmount.get() == true && minValue.get() != null && it > minValue.get()!!
                    .toInt() -> {
//
                    Utility.showToast("Amount should be more than ${minValue.get()}")
                }
                flexibleAmount.get() == true && maxValue.get() != null && it < maxValue.get()!!
                    .toInt() -> {
//                    maxLoadLimitReached.value = true
                    Utility.showToast("Amount should be less than ${maxValue.get()}")
                }

                else -> {
                    onAddClicked.value = true
                }
            }
        }

    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

            ApiConstant.PURCHASE_GIFT_CARD -> {

                val json = JsonParser.parseString(responseData.toString()) as JsonObject
                val obj = Gson().fromJson(
                    json.get("data"),
                    PurchaseGiftCardResponse::class.java
                )
                giftpurchased.postValue(obj)


            }
            ApiConstant.GET_GIFTS_LIST -> {


                val json = JsonParser.parseString(responseData.toString()) as JsonObject

//                val list = Gson().fromJson(
//                    json.get("data"),
//                    GiftProductResponse::class.java
//                )
//
//                productList.postValue(list)
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


