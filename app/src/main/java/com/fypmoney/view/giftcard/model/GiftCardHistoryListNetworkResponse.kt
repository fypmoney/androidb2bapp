package com.fypmoney.view.giftcard.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class GiftCardHistoryListNetworkResponse(
	@field:SerializedName("data")
	var data: List<GiftCardHistoryItem>? = null

) : Parcelable

@Keep
@Parcelize
data class GiftCardStatusNetworkResponse(
	@field:SerializedName("data")
	var data: GiftCardHistoryItem? = null

) : Parcelable

@Keep
@Parcelize
data class GiftCardHistoryItem(

	@field:SerializedName("endDate")
	var endDate: String? = null,

	@field:SerializedName("productGuid")
	var productGuid: String? = null,

	@field:SerializedName("destinationEmail")
	var destinationEmail: String? = null,

	@field:SerializedName("description")
	var description: String? = null,

	@field:SerializedName("voucherGcCode")
	var voucherGcCode: String? = null,

	@field:SerializedName("redeemLink")
	var redeemLink: String? = null,

	@field:SerializedName("giftVoucherOrderNo")
	var giftVoucherOrderNo: String? = null,

	@field:SerializedName("productName")
	var productName: String? = null,

	@field:SerializedName("destinationUserName")
	var destinationUserName: String? = null,

	@field:SerializedName("howToRedeem")
	var howToRedeem: String? = null,

	@field:SerializedName("fypOrderNo")
	var fypOrderNo: String? = null,

	@field:SerializedName("id")
	var id: String,

	@field:SerializedName("issueDate")
	var issueDate: String? = null,

	@field:SerializedName("tncLink")
	var tncLink: String? = null,

	@field:SerializedName("sourceMobileNo")
	var sourceMobileNo: String? = null,

	@field:SerializedName("brandLogo")
	var brandLogo: String? = null,

	@field:SerializedName("giftedPerson")
	var giftedPerson: String? = null,

	@field:SerializedName("destinationUserId")
	var destinationUserId: String? = null,

	@field:SerializedName("detailImage")
	var detailImage: String? = null,

	@field:SerializedName("amount")
	var amount: String? = null,

	@field:SerializedName("brandName")
	var brandName: String? = null,

	@field:SerializedName("sourceUserName")
	var sourceUserName: String? = null,

	@field:SerializedName("rfu2")
	var rfu2: String? = null,

	@field:SerializedName("rfu1")
	var rfu1: String? = null,

	@field:SerializedName("rfu3")
	var rfu3: String? = null,

	@field:SerializedName("tnc")
	var tnc: String? = null,

	@field:SerializedName("externalOrderId")
	var externalOrderId: String? = null,

	@field:SerializedName("isVoucherPurchased")
	var isVoucherPurchased: String? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("voucherGuid")
	var voucherGuid: String? = null,

	@field:SerializedName("voucherNo")
	var voucherNo: String? = null,

	@field:SerializedName("destinationMobileNo")
	var destinationMobileNo: String? = null,

	@field:SerializedName("voucherPin")
	var voucherPin: String? = null,

	@field:SerializedName("myntsRewarded")
	var myntsRewarded: String? = null,

	@field:SerializedName("voucherName")
	var voucherName: String? = null,

	@field:SerializedName("voucherStatus")
	var voucherStatus: String? = null,

	@field:SerializedName("sourceUserId")
	var sourceUserId: String? = null,

	@field:SerializedName("activationCode")
	var activationCode: String? = null,

	@field:SerializedName("activationUrl")
	var activationUrl: String? = null,

	@field:SerializedName("isGifted")
	var isGifted: String? = null
) : Parcelable{
	companion object{
		fun updateObject(giftCardHistoryItem: GiftCardHistoryItem):GiftCardHistoryItem{
			return GiftCardHistoryItem(
				endDate = giftCardHistoryItem.endDate,
				productGuid = giftCardHistoryItem.productGuid,
				destinationEmail = giftCardHistoryItem.destinationEmail,
				description = giftCardHistoryItem.description,
				voucherGcCode = giftCardHistoryItem.voucherGcCode,
				redeemLink = giftCardHistoryItem.redeemLink,
				giftVoucherOrderNo = giftCardHistoryItem.giftVoucherOrderNo,
				productName = giftCardHistoryItem.productName,
				destinationUserName = giftCardHistoryItem.destinationUserName,
				howToRedeem = giftCardHistoryItem.howToRedeem,
				fypOrderNo = giftCardHistoryItem.fypOrderNo,
				id = giftCardHistoryItem.id,
				issueDate = giftCardHistoryItem.issueDate,
				tncLink = giftCardHistoryItem.tncLink,
				sourceMobileNo = giftCardHistoryItem.sourceMobileNo,
				brandLogo = giftCardHistoryItem.brandLogo,
				giftedPerson = giftCardHistoryItem.giftedPerson,
				destinationUserId = giftCardHistoryItem.destinationUserId,
				detailImage = giftCardHistoryItem.detailImage,
				amount = giftCardHistoryItem.amount,
				brandName = giftCardHistoryItem.brandName,
				sourceUserName = giftCardHistoryItem.sourceUserName,
				rfu2 = giftCardHistoryItem.rfu2,
				rfu1 = giftCardHistoryItem.rfu1,
				rfu3 = giftCardHistoryItem.rfu3,
				tnc = giftCardHistoryItem.tnc,
				externalOrderId = giftCardHistoryItem.externalOrderId,
				isVoucherPurchased = giftCardHistoryItem.isVoucherPurchased,
				message = giftCardHistoryItem.message,
				voucherGuid = giftCardHistoryItem.voucherGuid,
				voucherNo = giftCardHistoryItem.voucherNo,
				destinationMobileNo = giftCardHistoryItem.destinationMobileNo,
				voucherPin = giftCardHistoryItem.voucherPin,
				myntsRewarded = giftCardHistoryItem.myntsRewarded,
				voucherName = giftCardHistoryItem.voucherName,
				voucherStatus = giftCardHistoryItem.voucherStatus,
				sourceUserId = giftCardHistoryItem.sourceUserId,
				activationCode = giftCardHistoryItem.activationCode,
				activationUrl = giftCardHistoryItem.activationUrl,
				isGifted = giftCardHistoryItem.isGifted
			)
		}
	}
}
