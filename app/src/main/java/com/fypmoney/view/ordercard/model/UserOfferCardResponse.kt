package com.fypmoney.view.ordercard.model
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Keep
data class UserDeliveryAddress(
    var pincode:String,
    var houseAddress:String,
    var areaDetail:String,
    var landmark:String
)


@Keep
@Parcelize
data class UserOfferCardResponse(
    @SerializedName("data")
    val `data`: List<UserOfferCard>
):Parcelable

@Keep
@Parcelize
data class UserOfferCard(
    @SerializedName("basePrice")
    val basePrice: String,
    @SerializedName("cgst")
    val cgst: Int,
    @SerializedName("code")
    val code: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("detailImage")
    val detailImage: String,
    @SerializedName("discount")
    var discount: Int = 0,
    @SerializedName("flexiblePrice")
    val flexiblePrice: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("igst")
    val igst: Int,
    @SerializedName("listImage")
    val listImage: String,
    @SerializedName("mrp")
    val mrp: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("productType")
    val productType: String,
    @SerializedName("sgst")
    val sgst: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("taxMasterCode")
    val taxMasterCode: String,
    @SerializedName("totalTax")
    val totalTax: Int,
    @SerializedName("voucherAllowed")
    val voucherAllowed: String
):Parcelable