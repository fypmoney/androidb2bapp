package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
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
import com.payu.india.Payu.PayuConstants
import com.payu.india.PostParams.PaymentPostParams
import org.json.JSONObject

class AddMoneyUpiDebitViewModel(application: Application) : BaseViewModel(application),
    AddMoneyUpiAdapter.OnUpiClickListener {
    var amountToAdd = ObservableField<String>()
    var clickedPositionForUpi = ObservableField<Int>()
    var addMoneyUpiAdapter = AddMoneyUpiAdapter(this)
    var savedCardsAdapter = SavedCardsAdapter()
    var onUpiClicked = MutableLiveData<UpiModel>()
    var onAddNewCardClicked = MutableLiveData<Boolean>()
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
                param = AddMoneyStep1Request(
                    remarks = "amount added",
                    amount = Utility.convertToPaise(amountToAdd.get()!!),
                    merchantId = "6616",
                    merchantKey = "gtKFFx",
                    salt = "wia56q6O"
                )
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ADD_MONEY_STEP1 -> {
                if (responseData is AddMoneyStep1Response) {
                    requestData.set(responseData.addMoneyStep1ResponseDetails.pgRequestData)
                   val result= requestData.get()?.replace("transactionId","txnid")
                    requestData.set(result)
                    Log.d("jbr8d",requestData.get()!!)

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
        pgRequestData.txnId = mainObject.getString("txnid")
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

    fun getPaymentParams(addNewCardDetails: AddNewCardDetails): PaymentParams {
        val resultData = parseResponseOfStep1(requestData.get())
        val mPaymentParams = PaymentParams()
        //mPaymentParams.setKey(< Your Key issued by PayU >)
        mPaymentParams.key = resultData.merchantKey
        mPaymentParams.amount = resultData.amount
        mPaymentParams.productInfo = resultData.productName
        mPaymentParams.firstName = resultData.userFirstName
        mPaymentParams.phone = resultData.phone
        mPaymentParams.email = resultData.email
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.surl = resultData.surl
        mPaymentParams.furl = resultData.furl
        mPaymentParams.udf1 = resultData.udf1
        mPaymentParams.udf2 = resultData.udf2
        mPaymentParams.udf3 = resultData.udf3
        mPaymentParams.udf4 = resultData.udf4
        mPaymentParams.udf5 = resultData.udf5
        mPaymentParams.hash = resultData.paymentHash
        mPaymentParams.userCredentials ="default"
        mPaymentParams.cardNumber = addNewCardDetails.cardNumber
        mPaymentParams.cardName = addNewCardDetails.nameOnCard
        mPaymentParams.nameOnCard = addNewCardDetails.cardNumber
        mPaymentParams.expiryMonth = addNewCardDetails.expiryMonth// MM
        mPaymentParams.expiryYear = addNewCardDetails.expiryYear// YYYY
        mPaymentParams.cvv = addNewCardDetails.cvv


       // mPaymentParams.userCredentials = "your_key:"+SharedPrefUtils.SF_KEY_ACCESS_TOKEN
        return mPaymentParams
    }

    /*
    * This method is used to handle on add new card
    * */
    fun onAddNewCardClicked() {
        onAddNewCardClicked.value = true

    }
}

