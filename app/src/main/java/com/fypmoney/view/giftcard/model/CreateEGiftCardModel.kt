package com.fypmoney.view.giftcard.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class CreateEGiftCardModel(
    var brandName:String,
    var voucherProductId:String,
    var amount:Long,
    var giftedPerson:String,
    var message:String? = "",
    var destinationMobileNo:String,
    var destinationEmail:String? = null,
    var destinationName:String,
):Parcelable


@Parcelize
@Keep
data class PurchasedGiftCardStatusUiModel(
    var purchaseGiftCardDetailId:String,
    var amount:String,
    var title:String,
    var subTitle:String,
    var myntsEarned:String,
    var myntsVisibility:Boolean,
    var statusAnimRes:Int,
    var status:CreateEGiftCardOrderStatus,
    var actionButtonVisibility:Boolean,
    var actionBtnText:String,
    var actionBtnCTA:GiftCardStatusScreenCTA
):Parcelable

sealed class GiftCardStatusScreenCTA:Parcelable{
    @Parcelize
    object NavigateToHome:GiftCardStatusScreenCTA()
    @Parcelize
    data class NavigateToGiftCardDetails(var voucherDetailsId:String):GiftCardStatusScreenCTA()
}


sealed class CreateEGiftCardOrderStatus :Parcelable{
    @Parcelize
    object Success:CreateEGiftCardOrderStatus()
    @Parcelize
    object Failed:CreateEGiftCardOrderStatus()
    @Parcelize
    object Pending:CreateEGiftCardOrderStatus()
}

