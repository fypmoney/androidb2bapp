package com.fypmoney.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fypmoney.base.BaseViewModel
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiUrl
import com.fypmoney.connectivity.ErrorResponseInfo
import com.fypmoney.connectivity.network.NetworkUtil
import com.fypmoney.connectivity.retrofit.ApiRequest
import com.fypmoney.connectivity.retrofit.WebApiCaller
import com.fypmoney.model.*
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.livedata.LiveEvent
import com.fypmoney.view.adapter.AddMoneyUpiAdapter
import com.fypmoney.view.adapter.SavedCardsAdapter
import com.payu.india.Extras.PayUChecksum
import com.payu.india.Model.PaymentParams
import com.payu.india.Payu.PayuConstants
import com.payu.paymentparamhelper.PostData
import org.json.JSONObject

class AddMoneyUpiDebitViewModel(application: Application) : BaseViewModel(application),
    AddMoneyUpiAdapter.OnUpiClickListener {
    var amountToAdd = ObservableField<String>()
    var amountToAdd1 = ObservableField<String>()
    var clickedPositionForUpi = ObservableField<Int>()
    var addMoneyUpiAdapter = AddMoneyUpiAdapter(this)
    var onUpiClicked = MutableLiveData(false)
    var onAddNewCardClicked = MutableLiveData(false)
    var requestData = ObservableField<String>()
    var hash = ObservableField<String>()
    var merchantKey = ObservableField<String>()
    var pgTxnNo = ObservableField<String>()
    var accountTxnNo = ObservableField<String>()
    var upiEntered = ObservableField<String>()
    var payUResponse = ObservableField<String>()
    var onStep2Response = MutableLiveData<String>()
    var step2ApiResponse = AddMoneyStep2ResponseDetails()
    var onAddInSaveCardClicked = MutableLiveData<AddNewCardDetails>()
    var isPaymentFail = ObservableField(false)
    var clickedAppPackageName = ObservableField<String>()
    var modeOfPayment = ObservableField<Int>()

    val event: LiveData<AddMoneyEvent>
        get() = _event
    private val _event = LiveEvent<AddMoneyEvent>()

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
                    merchantId = "",
                    merchantKey = ""
                )
            )
        )
    }




    /*
    *This method is used to verify the payment
    * */
    fun callAddMoneyStep2Api() {
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.API_ADD_MONEY_STEP2,
                endpoint = NetworkUtil.endURL(ApiConstant.API_ADD_MONEY_STEP2),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = true,
                param = AddMoneyStep2Request(
                    pgTxnNo = pgTxnNo.get(),
                    accountTxnNo = accountTxnNo.get(),
                    pgResponseData = payUResponse.get()
                )
            )
        )
    }

    override fun onSuccess(purpose: String, responseData: Any) {
        super.onSuccess(purpose, responseData)
        when (purpose) {
            ApiConstant.API_ADD_MONEY_STEP1 -> {
                if (responseData is AddMoneyStep1Response) {
                    pgTxnNo.set(responseData.addMoneyStep1ResponseDetails.pgTxnNo)
                    accountTxnNo.set(responseData.addMoneyStep1ResponseDetails.accountTxnNo)
                    requestData.set(responseData.addMoneyStep1ResponseDetails.pgRequestData)
                    val result = requestData.get()?.replace("transactionId", "txnid")
                    requestData.set(result)
                    parseResponseOfStep1(requestData.get())
                    if(onUpiClicked.value!!){
                        _event.value = AddMoneyEvent.OnUPIClickedEvent
                        onUpiClicked.value = false
                    }else if(onAddNewCardClicked.value!!){
                        _event.value = AddMoneyEvent.OnADDNewCardClickedEvent
                        onAddNewCardClicked.value = false
                    }
                }
            }
            ApiConstant.API_ADD_MONEY_STEP2 -> {
                if (responseData is AddMoneyStep2Response) {
                    step2ApiResponse = responseData.addMoneyStep2ResponseDetails
                    onStep2Response.value = AppConstants.API_SUCCESS

                }

            }
        }

    }

    override fun onError(purpose: String, errorResponseInfo: ErrorResponseInfo) {
        super.onError(purpose, errorResponseInfo)
        when (purpose) {
            ApiConstant.API_ADD_MONEY_STEP2 -> {
                onStep2Response.value = AppConstants.API_FAIL
            }
        }

    }

    override fun onUpiItemClicked(position: Int, upiModel: UpiModel?) {
        //clickedPositionForUpi.set(position)
        clickedAppPackageName.set(upiModel!!.packageName)
        onUpiClicked.value = true
        callAddMoneyStep1Api()

    }

    /*
   * This method is used to handle on add new card
   * */
    fun onAddNewCardClicked() {
        onAddNewCardClicked.value = true
        callAddMoneyStep1Api()

    }
    /*
    *  used to parse the response
    * */
    private fun parseResponseOfStep1(requestData: String?): PgRequestData {
        if (!requestData.isNullOrEmpty()) {
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

            // set hash and merchant key
            hash.set(mainObject.getString("paymentHash"))
            merchantKey.set(mainObject.getString("merchantKey"))

            return pgRequestData
        }
        return PgRequestData()
    }

    /*
    * return params for upi, phone pe , google pe, debit card
    * */
    fun getPaymentParams(
        type: String,
        upiId: String? = null,
        isUpiSaved: Boolean? = false,
        addNewCardDetails: AddNewCardDetails? = null, fromWhichType: Int? = 0
    ): PaymentParams {
        val resultData = parseResponseOfStep1(requestData.get())
        val mPaymentParams = PaymentParams()
        mPaymentParams.key = resultData.merchantKey
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.amount = resultData.amount
        mPaymentParams.productInfo = resultData.productName
        mPaymentParams.firstName = resultData.userFirstName
        mPaymentParams.phone = resultData.phone
        if (SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_EMAIL
            ) == null
        ) {
            mPaymentParams.email = ""
        } else {
            mPaymentParams.email = SharedPrefUtils.getString(
                getApplication(),
                SharedPrefUtils.SF_KEY_USER_EMAIL
            )
        }
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.surl = " https://payuresponse.firebaseapp.com/success"
        mPaymentParams.furl = "https://payuresponse.firebaseapp.com/failure"
        mPaymentParams.udf1 = resultData.udf1
        mPaymentParams.udf2 = resultData.udf2
        mPaymentParams.udf3 = resultData.udf3
        mPaymentParams.udf4 = resultData.udf4
        mPaymentParams.udf5 = resultData.udf5
        mPaymentParams.userCredentials = resultData.merchantKey + ":" + SharedPrefUtils.getLong(
            getApplication(),
            SharedPrefUtils.SF_KEY_USER_ID
        )

        mPaymentParams.hash = resultData.paymentHash
        hash.set(mPaymentParams.hash)


        when (type) {
            AppConstants.TYPE_DC -> {
                if (fromWhichType == 0) {
                    mPaymentParams.cardNumber = addNewCardDetails?.cardNumber
                } else {
                    mPaymentParams.cardToken = addNewCardDetails?.card_token
                }
                mPaymentParams.nameOnCard = addNewCardDetails?.nameOnCard
                mPaymentParams.expiryMonth = addNewCardDetails?.expiryMonth// MM
                mPaymentParams.expiryYear = addNewCardDetails?.expiryYear// YYYY
                mPaymentParams.cvv = addNewCardDetails?.cvv

                mPaymentParams.storeCard = 0


            }

            AppConstants.TYPE_UPI -> {
                mPaymentParams.vpa = upiId
                if (isUpiSaved == true) {
                    mPaymentParams.storeCard = 1
                } else {
                    mPaymentParams.storeCard = 0
                }

            }
            AppConstants.TYPE_GOOGLE_PAY -> {
            }
            AppConstants.TYPE_PHONEPE -> {
            }
            AppConstants.TYPE_GENERIC -> {
            }
        }


        return mPaymentParams
    }






    /*
    * This method is used to make request of hash
    * */
    fun makeGetHashRequest(command: String, var1: String): GetHashRequest {
        val getHashRequest = GetHashRequest()
        getHashRequest.merchantKey = merchantKey.get()
        val list = ArrayList<HashData>()
        val hashData = HashData()
        hashData.command = command
        hashData.var1 = var1
        list.add(hashData)
        getHashRequest.hashData = list
        return getHashRequest
    }

    sealed class AddMoneyEvent{
        object OnUPIClickedEvent:AddMoneyEvent()
        object OnADDNewCardClickedEvent:AddMoneyEvent()
    }

}

