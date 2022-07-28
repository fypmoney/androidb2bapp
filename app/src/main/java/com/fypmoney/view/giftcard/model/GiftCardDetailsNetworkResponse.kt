package com.fypmoney.view.giftcard.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class GiftCardDetailsNetworkResponse(

	@field:SerializedName("data")
	val giftCardDetail: GiftCardDetail
) : Parcelable

@Keep
@Parcelize
data class GiftCardDetail(

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("productGuid")
	val productGuid: String? = null,

	@field:SerializedName("destinationEmail")
	val destinationEmail: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("voucherGcCode")
	val voucherGcCode: String? = null,

	@field:SerializedName("redeemLink")
	val redeemLink: String? = null,

	@field:SerializedName("giftVoucherOrderNo")
	val giftVoucherOrderNo: String? = null,

	@field:SerializedName("productName")
	val productName: String? = null,

	@field:SerializedName("destinationUserName")
	val destinationUserName: String? = null,

	@field:SerializedName("howToRedeem")
	val howToRedeem: String? = null,

	@field:SerializedName("fypOrderNo")
	val fypOrderNo: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("issueDate")
	val issueDate: String? = null,

	@field:SerializedName("tncLink")
	val tncLink: String? = null,

	@field:SerializedName("sourceMobileNo")
	val sourceMobileNo: String? = null,

	@field:SerializedName("brandLogo")
	val brandLogo: String? = null,

	@field:SerializedName("giftedPerson")
	val giftedPerson: String? = null,

	@field:SerializedName("destinationUserId")
	val destinationUserId: String? = null,

	@field:SerializedName("detailImage")
	val detailImage: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("brandName")
	val brandName: String? = null,

	@field:SerializedName("sourceUserName")
	val sourceUserName: String? = null,

	@field:SerializedName("rfu2")
	val rfu2: String? = null,

	@field:SerializedName("rfu1")
	val rfu1: String? = null,

	@field:SerializedName("rfu3")
	val rfu3: String? = null,

	@field:SerializedName("tnc")
	val tnc: String? = null,

	@field:SerializedName("externalOrderId")
	val externalOrderId: String? = null,

	@field:SerializedName("isVoucherPurchased")
	val isVoucherPurchased: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("voucherGuid")
	val voucherGuid: String? = null,

	@field:SerializedName("voucherNo")
	val voucherNo: String? = null,

	@field:SerializedName("destinationMobileNo")
	val destinationMobileNo: String? = null,

	@field:SerializedName("voucherPin")
	val voucherPin: String? = null,

	@field:SerializedName("myntsRewarded")
	val myntsRewarded: String? = null,

	@field:SerializedName("voucherName")
	val voucherName: String? = null,

	@field:SerializedName("voucherStatus")
	val voucherStatus: String? = null,

	@field:SerializedName("sourceUserId")
	val sourceUserId: Int? = null,

	@field:SerializedName("activationCode")
	val activationCode: String? = null,

	@field:SerializedName("activationUrl")
	val activationUrl: String? = null,

	@field:SerializedName("isGifted")
	val isGifted: String? = null
) : Parcelable
