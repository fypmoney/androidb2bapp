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
import com.payu.india.Extras.PayUChecksum
import com.payu.india.Model.PaymentParams
import com.payu.india.Payu.PayuConstants
import com.payu.paymentparamhelper.PostData
import org.json.JSONObject
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

class AddMoneyUpiDebitViewModel(application: Application) : BaseViewModel(application),
    AddMoneyUpiAdapter.OnUpiClickListener {
    var amountToAdd = ObservableField<String>()
    var amountToAdd1 = ObservableField<String>()
    var clickedPositionForUpi = ObservableField<Int>()
    var addMoneyUpiAdapter = AddMoneyUpiAdapter(this)
    var savedCardsAdapter = SavedCardsAdapter()
    var onUpiClicked = MutableLiveData<UpiModel>()
    var onAddNewCardClicked = MutableLiveData<Boolean>()
    var requestData = ObservableField<String>()
    var hash = ObservableField<String>()
    var merchantKey = ObservableField("smsplus")
    var merchantSalt = "1b1b0"
    var pgTxnNo = ObservableField<String>()
    var accountTxnNo = ObservableField<String>()
    var upiEntered = ObservableField<String>()
    var payUResponse = ObservableField<String>()
    var onStep2Response = MutableLiveData<String>()
    var step2ApiResponse = AddMoneyStep2ResponseDetails()

    init {


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
                    merchantKey = merchantKey.get(),
                    merchantSalt = merchantSalt
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

                }
            }
            ApiConstant.API_ADD_MONEY_STEP2 -> {
                if (responseData is AddMoneyStep2Response) {
                    step2ApiResponse = responseData.addMoneyStep2ResponseDetails
                    onStep2Response.value = AppConstants.API_SUCCESS

                }

            }

            ApiConstant.GET_USER_CARDS -> {
                if (responseData is SavedCardResponseDetails) {
                    Log.d("cmckcmfci", responseData.toString())

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
    private fun parseResponseOfStep1(requestData: String?): PgRequestData {
        if (!requestData.isNullOrEmpty()) {
            val pgRequestData = PgRequestData()
            val mainObject = JSONObject(requestData)
            pgRequestData.txnId = mainObject.getString("txnid")
            pgRequestData.email = mainObject.getString("email")
            pgRequestData.amount = mainObject.getString("amount")
            pgRequestData.merchantId = mainObject.getString("merchantId")
            //  pgRequestData.merchantKey = mainObject.getString("merchantKey")
            pgRequestData.merchantKey = merchantKey.get()
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
            // merchantKey.set(mainObject.getString("merchantKey"))

            getAllSavedCardsApi()
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
        addNewCardDetails: AddNewCardDetails? = null
    ): PaymentParams {
        val resultData = parseResponseOfStep1(requestData.get())
        val mPaymentParams = PaymentParams()
        mPaymentParams.key = resultData.merchantKey
        mPaymentParams.txnId = resultData.txnId
        mPaymentParams.amount = resultData.amount
        mPaymentParams.productInfo = resultData.productName
        mPaymentParams.firstName = resultData.userFirstName
        mPaymentParams.phone = resultData.phone
        mPaymentParams.email = "poojamalik810@gmail.com"
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

        mPaymentParams.hash = generateHashFromSDK(mPaymentParams, merchantSalt)
        hash.set(mPaymentParams.hash)
        Log.d("hashhhhhhh", mPaymentParams.hash)


        when (type) {
            AppConstants.TYPE_DC -> {
                mPaymentParams.cardNumber = addNewCardDetails?.cardNumber
                mPaymentParams.nameOnCard = addNewCardDetails?.nameOnCard
                mPaymentParams.expiryMonth = addNewCardDetails?.expiryMonth// MM
                mPaymentParams.expiryYear = addNewCardDetails?.expiryYear// YYYY
                mPaymentParams.cvv = addNewCardDetails?.cvv

                if (addNewCardDetails?.isCardSaved == true) {
                    mPaymentParams.storeCard = 1
                } else {
                    mPaymentParams.storeCard = 0
                }

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
    * This method is used to handle on add new card
    * */
    fun onAddNewCardClicked() {
        onAddNewCardClicked.value = true

    }

/*
* This method is used to get all the user saved cards
* */

    private fun getAllSavedCardsApi() {
        val var1 = merchantKey.get() + ":" + SharedPrefUtils.getLong(
            getApplication(),
            SharedPrefUtils.SF_KEY_USER_ID
        )
        WebApiCaller.getInstance().request(
            ApiRequest(
                purpose = ApiConstant.PAYU_TEST_URL,
                endpoint = NetworkUtil.endURL(ApiConstant.PAYU_TEST_URL),
                request_type = ApiUrl.POST,
                onResponse = this, isProgressBar = false,
                param = GetSavedCardRequest(
                    hash = calculateHash(
                        merchantKey.get()!!,
                        command = ApiConstant.GET_USER_CARDS,
                        var1 = var1,
                        salt = merchantSalt
                    )!!,
                    var1 = var1,
                    command = ApiConstant.GET_USER_CARDS,
                    key = merchantKey.get()

                )
            ), whichServer = AppConstants.PAYU_SERVER
        )
    }

    private fun calculateHash(hashString: String): String {
        return return try {
            val hash = StringBuilder()
            val messageDigest = MessageDigest.getInstance("SHA-512")
            messageDigest.update(hashString.toByteArray())
            val mdbytes = messageDigest.digest()
            for (hashByte in mdbytes) {
                hash.append(
                    Integer.toString((hashByte and 0xff.toByte()) + 0x100, 16).substring(1)
                )
            }
            hash.toString()
            // getReturnData(PayuErrors.NO_ERROR, PayuConstants.SUCCESS, hash.toString())
        } catch (e: NoSuchAlgorithmException) {
            ""
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

    fun generateHashFromSDK(mPaymentParams: PaymentParams, salt: String): String? {

        var postData = PostData();

//        if(mPaymentParams.getBeneficiaryAccountNumber()== null){

        // payment Hash;
        val checksum = PayUChecksum()
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        return postData.getResult()

    }

    private fun calculateHash(key: String, command: String, var1: String, salt: String): String? {
        val checksum = PayUChecksum()
        checksum.setKey(key)
        checksum.setCommand(command)
        checksum.setVar1(var1)
        checksum.setSalt(salt)
        return checksum.getHash().result
    }

    /*
    * This method is used to set the list of upi
    * */
    fun setUpiList() {

    }

}

