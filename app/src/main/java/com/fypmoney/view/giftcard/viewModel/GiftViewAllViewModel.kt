package com.fypmoney.view.giftcard.viewModel

import android.app.Application
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ErrorResponseInfo

/*
* This class is used to handle add money functionality
* */
class GiftViewAllViewModel(application: Application) : BaseViewModel(application) {

    init {

    }


    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {


        }
    }


    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)

        when (purpose) {


        }
    }

}


