package com.fypmoney.view.ordercard
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


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
    val basePrice: Int,
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
    val id: Int,
    @SerializedName("igst")
    val igst: Int,
    @SerializedName("listImage")
    val listImage: String,
    @SerializedName("mrp")
    val mrp: Int,
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