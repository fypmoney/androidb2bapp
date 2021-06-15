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
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.adapter.AddMoneyUpiAdapter
import com.fypmoney.view.adapter.SavedCardsAdapter
import com.payu.india.Model.PaymentParams
import com.payu.india.Payu.PayuConstants
import com.payu.india.Payu.PayuErrors
import com.payu.paymentparamhelper.PostData
import com.payu.upisdk.generatepostdata.PaymentParamsUpiSdk
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

class AddMoneyUpiDebitViewModel(application: Application) : BaseViewModel(application),
    AddMoneyUpiAdapter.OnUpiClickListener {
    var amountToAdd = ObservableField<String>()
    var clickedPositionForUpi = ObservableField<Int>()
    var addMoneyUpiAdapter = AddMoneyUpiAdapter(this)
    var savedCardsAdapter = SavedCardsAdapter()
    var onUpiClicked = MutableLiveData<UpiModel>()
    var onAddNewCardClicked = MutableLiveData<Boolean>()
    var requestData = ObservableField<String>()
    var hash = ObservableField<String>()
    var merchantKey = ObservableField<String>()
    var pgTxnNo = ObservableField<String>()
    var accountTxnNo = ObservableField<String>()
    var payUResponse = ObservableField<String>()
    var onStep2Response = MutableLiveData<String>()
    var step2ApiResponse = AddMoneyStep2ResponseDetails()

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
                    merchantId = "",
                    merchantKey = "",
                    salt = ""
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

                    //   requestData.set("{\"transactionId\":\"211654905710001\",\"amount\":\"20.00\",\"phone\":\"9873752590\",\"pgUrl\":\"https://test.payu.in/_payment\",\"userFirstName\":\"yogesh  dayma\",\"productName\":\"Pockket Product\",\"email\":null,\"merchantKey\":\"MEqEEF\",\"merchantId\":\"5004627\",\"paymentHash\":\"1ffcc5a54e8b4706c8ed85b2b37f493cc44a70da33bea4d0654e5fdf6239cf4b2c94976fed766369310c94a5b17fa2a7f57aa4c275b50529ae228528a884de90\",\"udf1\":\"FLM-211654905710001\",\"udf2\":\"\",\"udf3\":\"\",\"udf4\":\"\",\"udf5\":\"\",\"udf6\":\"\",\"udf7\":\"\",\"udf8\":\"\",\"udf9\":\"\",\"udf10\":\"\",\"surl\":\"http://10.0.1.76:9898/services/PockketService/api/pg/callback\",\"furl\":\"http://10.0.1.76:9898/services/PockketService/api/pg/callback\"}")
                    val result = requestData.get()?.replace("transactionId", "txnid")
                    requestData.set(result)

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

        // set hash

        hash.set(mainObject.getString("paymentHash"))
        merchantKey.set(mainObject.getString("merchantKey"))

        return pgRequestData


    }

    /*
    * return params for core sdk for debit card
    * */
    fun getPaymentParams(addNewCardDetails: AddNewCardDetails): PaymentParams {
        val resultData = parseResponseOfStep1(requestData.get())
        val mPaymentParams = PaymentParams()
        //mPaymentParams.setKey(< Your Key issued by PayU >)
        mPaymentParams.key = resultData.merchantKey
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.amount = resultData.amount
        mPaymentParams.productInfo = resultData.productName
        mPaymentParams.firstName = resultData.userFirstName
        mPaymentParams.phone = resultData.phone
        mPaymentParams.email = resultData.email
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.surl = " https://payuresponse.firebaseapp.com/success"
        mPaymentParams.furl = "https://payuresponse.firebaseapp.com/failure"

        mPaymentParams.udf1 = resultData.udf1
        mPaymentParams.udf2 = resultData.udf2
        mPaymentParams.udf3 = resultData.udf3
        mPaymentParams.udf4 = resultData.udf4
        mPaymentParams.udf5 = resultData.udf5
        mPaymentParams.hash = resultData.paymentHash
        mPaymentParams.userCredentials = resultData.merchantKey + ":" + SharedPrefUtils.getString(
            getApplication(),
            SharedPrefUtils.SF_KEY_USER_ID
        )
        mPaymentParams.cardNumber = addNewCardDetails.cardNumber
        mPaymentParams.nameOnCard = addNewCardDetails.nameOnCard
        mPaymentParams.expiryMonth = addNewCardDetails.expiryMonth// MM
        mPaymentParams.expiryYear = addNewCardDetails.expiryYear// YYYY
        mPaymentParams.cvv = addNewCardDetails.cvv

        if (addNewCardDetails.isCardSaved == true) {
            mPaymentParams.storeCard = 1
        } else {
            mPaymentParams.storeCard = 0
        }

        // mPaymentParams.userCredentials = "your_key:"+SharedPrefUtils.SF_KEY_ACCESS_TOKEN
        return mPaymentParams
    }

    /*
      * return params for upi
      * */
    fun getPaymentParamsForUpi(upiId: String, isUpiSaved: Boolean): PaymentParamsUpiSdk {
        val resultData = parseResponseOfStep1(requestData.get())
        val mPaymentParams = PaymentParamsUpiSdk()
        //mPaymentParams.setKey(< Your Key issued by PayU >)
        mPaymentParams.key = resultData.merchantKey
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.amount = resultData.amount
        mPaymentParams.productInfo = resultData.productName
        mPaymentParams.firstName = resultData.userFirstName
        mPaymentParams.phone = resultData.phone
        mPaymentParams.email = resultData.email
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.surl = " https://payuresponse.firebaseapp.com/success"
        mPaymentParams.furl = "https://payuresponse.firebaseapp.com/failure"

        mPaymentParams.udf1 = resultData.udf1
        mPaymentParams.udf2 = resultData.udf2
        mPaymentParams.udf3 = resultData.udf3
        mPaymentParams.udf4 = resultData.udf4
        mPaymentParams.udf5 = resultData.udf5
        mPaymentParams.hash = resultData.paymentHash
        mPaymentParams.vpa = upiId

        mPaymentParams.userCredentials = resultData.merchantKey + ":" + SharedPrefUtils.getString(
            getApplication(),
            SharedPrefUtils.SF_KEY_USER_ID
        )


        if (isUpiSaved) {
            mPaymentParams.storeCard = 1
        } else {
            mPaymentParams.storeCard = 0
        }

        // mPaymentParams.userCredentials = "your_key:"+SharedPrefUtils.SF_KEY_ACCESS_TOKEN
        return mPaymentParams
    }

    /*
    * This method is used to handle on add new card
    * */
    fun onAddNewCardClicked() {
        onAddNewCardClicked.value = true

    }

    /*
    * This method is used to get all the user saved cards
    * */

    fun getAllSavedCardsApi() {
        val var1 = merchantKey.get() + ":" + SharedPrefUtils.getString(
            getApplication(),
            SharedPrefUtils.SF_KEY_USER_ID
        )
        val calculatedHash =
            calculateHash(merchantKey.get() + "|" + ApiConstant.GET_USER_CARDS + "|" + var1 + "|" + "salt")
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.PAYU_TEST_URL,
                endpoint = NetworkUtil.endURL(ApiConstant.PAYU_TEST_URL),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = GetSavedCardRequest(
                    hash = calculatedHash,
                    var1 = var1,
                    command = ApiConstant.GET_USER_CARDS,
                    key = merchantKey.get()

                )
            ), whichServer = AppConstants.PAYU_SERVER
        )
    }

    private fun calculateHash(hashString: String): String {
        return try {
            val hash = StringBuilder()
            val messageDigest = MessageDigest.getInstance("SHA-512")
            messageDigest.update(hashString.toByteArray())
            val mdbytes = messageDigest.digest()
            for (hashByte in mdbytes) {
                hash.append(
                    Integer.toString((hashByte and 0xff.toByte()) + 0x100, 16).substring(1)
                )
            }
            return hash.toString()
            // getReturnData(PayuErrors.NO_ERROR, PayuConstants.SUCCESS, hash.toString())
        } catch (e: NoSuchAlgorithmException) {
            return ""
        }
    }

    protected fun getReturnData(code: Int, status: String?, result: String?): PostData {
        val postData = PostData()
        postData.code = code
        postData.status = status
        postData.result = result
        return postData
    }

    protected fun getReturnData(code: Int, result: String?): PostData {
        return getReturnData(code, PayuConstants.ERROR, result)
    }


}

