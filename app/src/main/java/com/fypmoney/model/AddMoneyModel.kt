package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import com.payu.paymentparamhelper.PostData
import java.io.Serializable

data class AddMoneyModel(var name: String? = null, var imageUrl: String? = null) {
}

data class UpiModel(var name: String? = null, var imageUrl: String? = null) {
}

data class SavedCardResponseDetails(
    @SerializedName("name_on_card") val name_on_card: String,
    @SerializedName("card_type") val card_type: String,
    @SerializedName("card_token") val card_token: String,
    @SerializedName("is_expired") val is_expired: String,
    @SerializedName("card_no") val card_no: String,
    @SerializedName("card_brand") val card_brand: String,
    @SerializedName("expiry_year") val expiry_year: String,
    @SerializedName("expiry_month") val expiry_month: String,
) : Serializable


data class AddMoneyStep1Request(
    @SerializedName("remarks") val remarks: String?,
    @SerializedName("amount") val amount: String?,
    @SerializedName("merchantSalt") val merchantSalt: String?,
    @SerializedName("merchantKey") val merchantKey: String?,
    @SerializedName("merchantId") val merchantId: String?
) : BaseRequest()

data class AddMoneyStep1Response(
    @SerializedName("data") val addMoneyStep1ResponseDetails: AddMoneyStep1ResponseDetails
) : Serializable

data class AddMoneyStep1ResponseDetails(
    @SerializedName("pgTxnNo") val pgTxnNo: String,
    @SerializedName("pgRequestData") val pgRequestData: String,
    @SerializedName("accountTxnNo") val accountTxnNo: String?
    ) : Serializable
data class AddMoneyStep2Response(
    @SerializedName("data") val addMoneyStep2ResponseDetails: AddMoneyStep2ResponseDetails
) : Serializable


data class AddMoneyStep2ResponseDetails(
    @SerializedName("pgTxnNo") val pgTxnNo: String?=null,
    @SerializedName("pgRequestData") val pgRequestData: String?=null,
    @SerializedName("pgExternalId") val pgExternalId: String?=null,
    @SerializedName("bankExternalId") val bankExternalId: String?=null,
    @SerializedName("txnTime") val txnTime: String?=null,
    @SerializedName("accountTxnTime") val accountTxnTime: String?=null,
    @SerializedName("tagBalance") val tagBalance: String?=null,
    @SerializedName("remainingLoadLimit") val remainingLoadLimit: String?=null,
    @SerializedName("balance") val balance: String?=null,
    @SerializedName("amount") val amount: String?=null,
    @SerializedName("accountTxnNo") val accountTxnNo: String?=null
) : Serializable


data class PgRequestData(
    @SerializedName("txnId") var txnId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("pgUrl") var pgUrl: String? = null,
    @SerializedName("userFirstName") var userFirstName: String? = null,
    @SerializedName("productName") var productName: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("merchantKey") var merchantKey: String? = null,
    @SerializedName("merchantId") var merchantId: String? = null,
    @SerializedName("paymentHash") var paymentHash: String? = null,
    @SerializedName("furl") var furl: String? = null,
    @SerializedName("surl") var surl: String? = null,
    @SerializedName("udf10") var udf10: String? = null,
    @SerializedName("udf8") var udf8: String? = null,
    @SerializedName("udf9") var udf9: String? = null,
    @SerializedName("udf7") var udf7: String? = null,
    @SerializedName("udf6") var udf6: String? = null,
    @SerializedName("udf5") var udf5: String? = null,
    @SerializedName("udf4") var udf4: String? = null,
    @SerializedName("udf3") var udf3: String? = null,
    @SerializedName("udf2") var udf2: String? = null,
    @SerializedName("udf1") var udf1: String? = null
) : Serializable


data class PayMoneyRequest(
    @SerializedName("txnType") val txnType: String?,
    @SerializedName("approvalId") val approvalId: String?,
    @SerializedName("actionSelected") val actionSelected: String?,
    @SerializedName("emojis") val emojis: String?,
    @SerializedName("remarks") val remarks: String?
) : BaseRequest()


data class PayMoneyResponse(
    @SerializedName("msg") val msg: String? = null
) : Serializable


data class AddNewCardDetails(
    var cardNumber: String,
    var nameOnCard: String,
    var expiryMonth: String,
    var expiryYear: String,
    var cvv: String,
    var isCardSaved: Boolean? = false
)

data class AddMoneyStep2Request(
    @SerializedName("pgTxnNo") val pgTxnNo: String?,
    @SerializedName("accountTxnNo") val accountTxnNo: String?,
    @SerializedName("pgResponseData") val pgResponseData: String?
) : BaseRequest()


data class GetSavedCardRequest(
    @SerializedName("hash") val hash: String,
    @SerializedName("command") val command: String?,
    @SerializedName("key") val key: String?,
    @SerializedName("var1") val var1: String?
) : BaseRequest()