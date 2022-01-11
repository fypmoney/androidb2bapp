package com.fypmoney.view.giftCardModule.viewModel

import android.app.Application
import androidx.databinding.ObservableArrayList
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
import com.fypmoney.database.ContactRepository
import com.fypmoney.database.entity.ContactEntity
import com.fypmoney.model.*
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.ContactsGiftCardAdapter
import com.fypmoney.view.giftCardModule.model.GiftProductResponse
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardRequest
import com.fypmoney.view.giftCardModule.model.PurchaseGiftCardResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/*
* This class is used to handle add money functionality
* */
class GiftCardViewModel(application: Application) : BaseViewModel(application) {

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {

        }
    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        progressDialog.value = false

        when (purpose) {

        }
    }

}


