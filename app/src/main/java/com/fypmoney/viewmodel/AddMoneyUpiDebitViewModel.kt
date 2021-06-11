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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.AddMoneyUpiAdapter
import com.fypmoney.view.adapter.SavedCardsAdapter
import com.payu.india.Model.PaymentParams
import org.json.JSONObject

class AddMoneyUpiDebitViewModel(application: Application) : BaseViewModel(application),
    AddMoneyUpiAdapter.OnUpiClickListener {
    var amountToAdd = ObservableField<String>()
    var clickedPositionForUpi = ObservableField<Int>()
    var addMoneyUpiAdapter = AddMoneyUpiAdapter(this)
    var savedCardsAdapter = SavedCardsAdapter()
    var onUpiClicked = MutableLiveData<UpiModel>()
    var requestData = ObservableField<String>()

    init {
        val list = ArrayList<UpiModel>()
        list.add(UpiModel(name = "Google Pay"))
        list.add(UpiModel(name = "Phone Pay"))
        addMoneyUpiAdapter.setList(list)

    }


    /*
      *This method is used to call payment parameters while receiving the payment
      * */
    fun callAddMoneyStep1Api() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_ADD_MONEY_STEP1,
                endpoint = NetworkUtil.endURL(ApiConstant.API_ADD_MONEY_STEP1),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = AddMoneyStep1Request(remarks = "amount added", amount = amountToAdd.get())
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ADD_MONEY_STEP1 -> {
                if (responseData is AddMoneyStep1Response) {
                    requestData.set(responseData.addMoneyStep1ResponseDetails.pgRequestData)

                }
            }


        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
    }

    override fun onUpiItemClicked(position: Int, upiModel: UpiModel?) {
        clickedPositionForUpi.set(position)
        onUpiClicked.value = upiModel

    }

    /*
    *  used to parse the response
    * */
    fun parseResponseOfStep1(requestData: String?): PgRequestData {
        val pgRequestData = PgRequestData()
        val mainObject = JSONObject(requestData)
        pgRequestData.transactionId = mainObject.getString("transactionId")
        pgRequestData.email = mainObject.getString("email")
        pgRequestData.amount = mainObject.getString("amount")
        pgRequestData.merchantId = mainObject.getString("merchantId")
        pgRequestData.merchantKey = mainObject.getString("merchantKey")
        pgRequestData.productName = mainObject.getString("productName")
        pgRequestData.paymentHash = mainObject.getString("paymentHash")
        pgRequestData.userFirstName = mainObject.getString("userFirstName")
        pgRequestData.surl = mainObject.getString("surl")
        pgRequestData.furl = mainObject.getString("furl")
        pgRequestData.pgUrl = mainObject.getString("pgUrl")
        pgRequestData.udf1 = mainObject.getString("udf1")
        pgRequestData.udf2 = mainObject.getString("udf2")
        pgRequestData.udf3 = mainObject.getString("udf3")
        pgRequestData.udf4 = mainObject.getString("udf4")
        pgRequestData.udf5 = mainObject.getString("udf5")
        pgRequestData.udf6 = mainObject.getString("udf6")
        pgRequestData.udf7 = mainObject.getString("udf7")
        pgRequestData.udf8 = mainObject.getString("udf8")
        return pgRequestData


    }

    fun getPaymentParams(): PaymentParams {
        val resultData = parseResponseOfStep1(requestData.get())
        val mPaymentParams = PaymentParams()
        // mPaymentParams.setKey(< Your Key issued by PayU >)
        mPaymentParams.setKey(resultData.merchantKey)
        mPaymentParams.amount = resultData.amount
        mPaymentParams.productInfo = resultData.productName
        mPaymentParams.firstName = resultData.userFirstName
        mPaymentParams.email = resultData.email
        mPaymentParams.txnId = resultData.transactionId
        mPaymentParams.surl = resultData.surl
        mPaymentParams.furl = resultData.furl
        mPaymentParams.udf1 = resultData.udf1
        mPaymentParams.udf2 = resultData.udf2
        mPaymentParams.udf3 = resultData.udf3
        mPaymentParams.udf4 = resultData.udf4
        mPaymentParams.udf5 = resultData.udf5

        return mPaymentParams
    }
}

