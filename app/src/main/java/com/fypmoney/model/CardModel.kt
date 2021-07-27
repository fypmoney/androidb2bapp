package com.fypmoney.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class VirtualCardRequestResponse(
    @SerializedName("data") val virtualCardRequestResponseDetails: VirtualCardRequestResponseDetails
) : Serializable


data class VirtualCardRequestResponseDetails(
    @SerializedName("requestData") val requestData: String,
    @SerializedName("serviceUrl") val serviceUrl: String
) : Serializable

data class FetchVirtualCardRequest(
    @SerializedName("action_name") var action_name: String? = null,
    @SerializedName("wlap_code") var wlap_code: String? = null,
    @SerializedName("wlap_secret_key") var wlap_secret_key: String? = null,
    @SerializedName("checksum") var checksum: String? = null,
    @SerializedName("p1") var p1: String? = null,
    @SerializedName("p2") var p2: String? = null
) : BaseRequest()

data class FetchVirtualCardResponse(
    @SerializedName("data") val fetchVirtualCardResponseDetails: FetchVirtualCardResponseDetails
) : Serializable


data class FetchVirtualCardResponseDetails(
    @SerializedName("message") val message: String,
    @SerializedName("card_number") val card_number: String,
    @SerializedName("expiry_month") val expiry_month: String,
    @SerializedName("expiry_year") val expiry_year: String,
    @SerializedName("cvv") val cvv: String,
    @SerializedName("transaction_type") val transactionTypeDetails: TransactionTypeDetails
) : Serializable

data class TransactionTypeDetails(
    @SerializedName("ECOM") val ECOM: String,
    @SerializedName("POS") val POS: String,
    @SerializedName("ATM") val ATM: String,
    @SerializedName("CL") val CL: String
) : Serializable

data class BankTransactionHistoryRequest(
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null
) : BaseRequest()


data class BankTransactionHistoryResponse(
    @SerializedName("data") val transactions: Transactions
) : Serializable


data class Transactions(
    @SerializedName("transactions") val bankTransactionHistoryResponseDetails: List<BankTransactionHistoryResponseDetails>,
    @SerializedName("pagination") val bankTransactionHistoryPagination: BankTransactionHistoryPagination
) : Serializable


data class BankTransactionHistoryResponseDetails(
    @SerializedName("transactionType") val transactionType: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("paymentMode") val paymentMode: String,
    @SerializedName("mrn") val mrn: String,
    @SerializedName("bankReferenceNumber") val bankReferenceNumber: String,
    @SerializedName("message") val message: String,
    @SerializedName("transactionDate") val transactionDate: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("mobileNo") val mobileNo: String,
    @SerializedName("accReferenceNumber") val accReferenceNumber: String
) : Serializable

data class BankTransactionHistoryPagination(
    @SerializedName("total_record") val total_record: Int,
    @SerializedName("per_page") val per_page: Int,
    @SerializedName("current_page") val current_page: Int,
    @SerializedName("total_pages") val total_pages: Int
) : Serializable


data class UpDateCardSettingsRequest(
    @SerializedName("action") val action: String? = null,
    @SerializedName("isEnable") val isEnable: Int? = null,
    @SerializedName("channelType") val channelType: String? = null,
    @SerializedName("kitNumber") val kitNumber: String? = null,
    @SerializedName("cardType") val cardType: Int? = null,
) : BaseRequest()

data class UpdateCardLimitRequest(
    @SerializedName("action") val action: String? = null,
    @SerializedName("atmLimit") val atmLimit: String? = null,
    @SerializedName("posLimit") val posLimit: String? = null,
    @SerializedName("ecomLimit") val ecomLimit: String? = null
) : BaseRequest()

data class UpdateCardLimitResponse(
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("data") val updateCardLimitResponseDetails: UpdateCardLimitResponseDetails? = null
) : Serializable


data class UpdateCardLimitResponseDetails(
    @SerializedName("walletStatus") val walletStatus: String? = null,
    @SerializedName("atmLimit") val atmLimit: String? = null,
    @SerializedName("posLimit") val posLimit: String? = null,
    @SerializedName("ecomLimit") val ecomLimit: String? = null,
    @SerializedName("action") val action: String? = null,
    @SerializedName("clLimit") val clLimit: String? = null
) : Serializable

data class UpdateCardSettingsResponse(
    @SerializedName("msg") val msg: String? = null,
    @SerializedName("data") val updateCardSettingsResponseDetails: UpdateCardSettingsResponseDetails? = null
) : Serializable


data class UpdateCardSettingsResponseDetails(
    @SerializedName("requestData") val requestData: String? = null,
    @SerializedName("serviceUrl") val serviceUrl: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("accountInfoId") val accountInfoId: String? = null,
    @SerializedName("cardType") val cardType: String? = null,
    @SerializedName("action") val action: String? = null,
    @SerializedName("kitNumber") val kitNumber: String? = null,
    @SerializedName("posEnabled") val posEnabled: String? = null,
    @SerializedName("ecomEnabled") val ecomEnabled: String? = null,
    @SerializedName("atmEnabled") val atmEnabled: String? = null,
    @SerializedName("clEnabled") val clEnabled: String? = null,
    @SerializedName("isCardBlocked") val isCardBlocked: String? = null
) : Serializable


data class ActivateCardInitResponse(
    @SerializedName("msg") val msg: String? = null
) : Serializable


data class BankProfileResponse(
    @SerializedName("data") val bankProfileResponseDetails: BankProfileResponseDetails? = null
) : Serializable

data class BankProfileResponseDetails(
    @SerializedName("isAccountActive") val isAccountActive: String? = null,
    @SerializedName("isVirtualCardIssued") val isVirtualCardIssued: String? = null,
    @SerializedName("isPhysicardIssued") val isPhysicardIssued: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("accountIdentifier") val accountIdentifier: String? = null,
    @SerializedName("isBlocked") val isBlocked: String? = null,
    @SerializedName("initialAmount") val initialAmount: String? = null,
    @SerializedName("isPhysicalCardIssued") val isPhysicalCardIssued: String? = null,
    @SerializedName("mobileVerificationState") val mobileVerificationState: String? = null,
    @SerializedName("kycVerificationState") val kycVerificationState: String? = null,
    @SerializedName("virtualCardIssuedState") val virtualCardIssuedState: String? = null,
    @SerializedName("physicalCardIssuedState") val physicalCardIssuedState: String? = null,
    @SerializedName("mobileVerificationStatus") val mobileVerificationStatus: String? = null,
    @SerializedName("kycVerificationStatus") val kycVerificationStatus: String? = null,
    @SerializedName("virtualCardIssuedStatus") val virtualCardIssuedStatus: String? = null,
    @SerializedName("physicalCardIssuedStatus") val physicalCardIssuedStatus: String? = null,
    @SerializedName("posLimit") val posLimit: String? = null,
    @SerializedName("ecomLimit") val ecomLimit: String? = null,
    @SerializedName("atmLimit") val atmLimit: String? = null,
    @SerializedName("clLimit") val clLimit: String? = null,
    @SerializedName("cardInfos") val cardInfos: List<CardInfoDetails>? = null,
) : Serializable


data class CardInfoDetails(
    @SerializedName("id") val id: String? = null,
    @SerializedName("cardNo") val cardNo: String? = null,
    @SerializedName("cardType") val cardType: String? = null,
    @SerializedName("nameOnCard") val nameOnCard: String? = null,
    @SerializedName("isCardBlocked") val isCardBlocked: String? = null,
    @SerializedName("kitNumber") val kitNumber: String? = null,
    @SerializedName("posEnabled") val posEnabled: String? = null,
    @SerializedName("ecomEnabled") val ecomEnabled: String? = null,
    @SerializedName("atmEnabled") val atmEnabled: String? = null,
    @SerializedName("clEnabled") val clEnabled: String? = null,
    @SerializedName("status") val status: String? = null
) : Serializable


data class OrderCardRequest(
    @SerializedName("nameOnCard") val nameOnCard: String? = null,
    @SerializedName("quote") val quote: String? = null,
    @SerializedName("paymentMode") val paymentMode: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("houseAddress") val houseAddress: String? = null,
    @SerializedName("areaDetail") val areaDetail: String? = null,
    @SerializedName("landmark") val landmark: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("stateCode") val stateCode: String? = null,
    @SerializedName("taxMasterCode") val taxMasterCode: String? = null,
    @SerializedName("productMasterCode") val productMasterCode: String? = null,
    @SerializedName("totalTax") val totalTax: String? = null,
    @SerializedName("discount") val discount: String? = null,

) : BaseRequest()

data class OrderCardResponse(
    @SerializedName("data") val orderCardResponseDetails: OrderCardResponseDetails? = null
) : Serializable

data class OrderCardResponseDetails(
    @SerializedName("nameOnCard") val nameOnCard: String? = null,
    @SerializedName("quote") val quote: String? = null,
    @SerializedName("paymentMode") val paymentMode: String? = null,
    @SerializedName("pincode") val pincode: String? = null,
    @SerializedName("houseAddress") val houseAddress: String? = null,
    @SerializedName("areaDetail") val areaDetail: String? = null,
    @SerializedName("landmark") val landmark: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("kitNumber") val kitNumber: String? = null
) : Serializable

data class GetOrderCardStatusResponse(
    @SerializedName("data") val GetOrderCardStatusResponseDetails:GetOrderCardStatusResponseDetails
) : Serializable

data class GetOrderCardStatusResponseDetails(
    @SerializedName("id") val id: String? = null,
    @SerializedName("nameOnCard") val nameOnCard: String? = null,
    @SerializedName("customerName") val customerName: String? = null,
    @SerializedName("productId") val productId: String? = null,
    @SerializedName("kitNo") val kitNo: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("amount") val amount: String? = null,
    @SerializedName("loyaltyPoints") val loyaltyPoints: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("packageStatusList") val packageStatusList: List<PackageStatusList>? = null
) : Serializable

data class PackageStatusList(
    @SerializedName("id") val id: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("isDone") var isDone: String? = null
) : Serializable

data class GetAllProductsResponse(
    @SerializedName("data") val getAllProductsResponseDetails: List<GetAllProductsResponseDetails>? = null
) : Serializable

data class GetAllProductsResponseDetails(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("basePrice") val basePrice: String? = null,
    @SerializedName("taxMasterCode") val taxMasterCode: String? = null,
    @SerializedName("igst") val igst: String? = null,
    @SerializedName("cgst") val cgst: String? = null,
    @SerializedName("sgst") val sgst: String? = null,
    @SerializedName("mrp") val mrp: String? = null,
    @SerializedName("flexiblePrice") val flexiblePrice: String? = null,
    @SerializedName("voucherAllowed") val voucherAllowed: String? = null,
    @SerializedName("totalTax") val totalTax: String? = null,
    @SerializedName("discount") val discount: String? = null,
    @SerializedName("status") val status: String? = null
) : Serializable

data class ActivateCardRequest(
    @SerializedName("additionalInfo") val additionalInfo: String? = null,
    @SerializedName("validationNo") val validationNo: String? = null,
    @SerializedName("cardIdentifier") val cardIdentifier: String? = null
) : BaseRequest()


data class ActivateCardResponse(
    @SerializedName("data") val getAllProductsResponseDetails: List<GetAllProductsResponseDetails>? = null
) : Serializable


data class PhysicalCardInitResponse(
    @SerializedName("msg") val msg: String? = null
) : Serializable

data class SetPinResponse(
    @SerializedName("cardIdentifier") val cardIdentifier: String? = null,
    @SerializedName("url") val url: String? = null
) : Serializable

data class GetStatesResponse(
    @SerializedName("data") val getStatesResponseDetails: List<GetStatesResponseDetails>? = null
) : Serializable

data class GetStatesResponseDetails(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("countryId") val countryId: String? = null,
    @SerializedName("gstCode") val gstCode: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("sortOrder") val sortOrder: String? = null
) : Serializable {
    override fun toString(): String {
        return name!!
    }
}


data class GetCityResponse(
    @SerializedName("data") val getCityResponseDetails: List<GetCityResponseDetails>? = null
) : Serializable

data class GetCityResponseDetails(
    @SerializedName("id") val id: String? = null,
    @SerializedName("cityName") val cityName: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("stateId") val stateId: String? = null,
    @SerializedName("latitude") val latitude: String? = null,
    @SerializedName("longitude") val longitude: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("sortOrder") val sortOrder: String? = null
) : Serializable {
    override fun toString(): String {
        return cityName!!
    }
}
