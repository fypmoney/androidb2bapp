package com.fypmoney.view.giftcard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.ItemPurchasedGiftCardBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.GIFT_VOUCHER_STATUS_NOT_VALID
import com.fypmoney.util.Utility
import com.fypmoney.view.giftcard.model.GiftCardHistoryItem
import com.google.firebase.crashlytics.FirebaseCrashlytics

class PurchasedGiftCardListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onGiftCardClick: (giftVoucherId: String) -> Unit,
    private val onRefreshClick: (giftVoucherId: String) -> Unit
) : ListAdapter<PurchasedGiftCardItemUiModel, PurchasedGiftCardListAdapter.PurchasedGiftCardListVH>(
    PurchasedGiftCardListDiffUtils
) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedGiftCardListVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPurchasedGiftCardBinding.inflate(inflater, parent, false)
        return PurchasedGiftCardListVH(
            binding,
            lifecycleOwner,
            onGiftCardClick,
            onRefreshClick
        )
    }


    override fun onBindViewHolder(holder: PurchasedGiftCardListVH, position: Int) {
        holder.bind(getItem(position))
    }


    class PurchasedGiftCardListVH(
        private val binding: ItemPurchasedGiftCardBinding,
        private val lifecycleOwner: LifecycleOwner,
        val onGiftCardClick: (giftVoucherId: String) -> Unit,
        val onRefreshClick: (giftVoucherId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PurchasedGiftCardItemUiModel) {
            with(binding){
                executeAfter {
                    lifecycleOwner = this@PurchasedGiftCardListVH.lifecycleOwner
                }
                Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = brandLogoIv, url = item.brandLogo)
                item.giftCardReceivedPurchasedType.run {
                    when(this){
                        is GiftVoucherReceivedStatus.Gifted -> {
                            giftReceivedPurchasedTv.setBackgroundResource(backgroundRes)
                            giftReceivedPurchasedTv.text = text
                        }
                        is GiftVoucherReceivedStatus.Purchased -> {
                            giftReceivedPurchasedTv.setBackgroundResource(backgroundRes)
                            giftReceivedPurchasedTv.text = text
                        }
                        is GiftVoucherReceivedStatus.Received -> {
                            giftReceivedPurchasedTv.setBackgroundResource(backgroundRes)
                            giftReceivedPurchasedTv.text = text
                        }
                    }
                }
                purchasedDateAndTimeTv.text = item.validityDate
                giftCardAmountTv.text = item.amount
                item.status.run {
                    when(this){
                        is Status.Failed -> {
                            giftCardStatusTv.text = status
                            giftCardStatusTv.setTextColor(textColor)
                            binding.giftCardStatusTv.setCompoundDrawablesWithIntrinsicBounds(drawableStart,0,0,0)

                        }
                        is Status.Pending -> {
                            giftCardStatusTv.text = status
                            giftCardStatusTv.setTextColor(textColor)
                            binding.giftCardStatusTv.setCompoundDrawablesWithIntrinsicBounds(drawableStart,0,0,0)

                        }
                        is Status.Success -> {
                            giftCardStatusTv.text = status
                            giftCardStatusTv.setTextColor(textColor)
                            binding.giftCardStatusTv.setCompoundDrawablesWithIntrinsicBounds(drawableStart,0,0,0)

                        }
                    }
                }
                if(item.refreshIsVisible){
                    binding.refreshTv.toVisible()
                }else{
                    binding.refreshTv.toGone()
                }
            }
            binding.refreshTv.setOnClickListener {
                onRefreshClick(item.giftCardId)
            }
            binding.voucherItemMcv.setOnClickListener {
                onGiftCardClick(item.giftCardId)
            }
        }

    }
}

object PurchasedGiftCardListDiffUtils : DiffUtil.ItemCallback<PurchasedGiftCardItemUiModel>() {

    override fun areItemsTheSame(
        oldItem: PurchasedGiftCardItemUiModel,
        newItem: PurchasedGiftCardItemUiModel
    ): Boolean {
        return (oldItem.giftCardId == newItem.giftCardId)
    }

    override fun areContentsTheSame(
        oldItem: PurchasedGiftCardItemUiModel,
        newItem: PurchasedGiftCardItemUiModel
    ): Boolean {
        return ((oldItem.brandLogo == newItem.brandLogo) && (oldItem.giftCardReceivedPurchasedType == newItem.giftCardReceivedPurchasedType)
                && (oldItem.validityDate == newItem.validityDate) &&
                (oldItem.amount == newItem.amount) &&
                (oldItem.status == newItem.status) &&
                (oldItem.refreshIsVisible == newItem.refreshIsVisible))
    }
}

@Keep
data class PurchasedGiftCardItemUiModel(
    var brandLogo: String? = null,
    var giftCardId: String,
    var giftCardReceivedPurchasedType: GiftVoucherReceivedStatus,
    var validityDate: String,
    var amount: String,
    var status: Status,
    var refreshIsVisible: Boolean
){
    companion object{
        fun convertGiftCardHistoryItemToPurchasedGiftCardItemUiModel(context: Context, giftCardHistoryItem: GiftCardHistoryItem):PurchasedGiftCardItemUiModel{
            return PurchasedGiftCardItemUiModel(
                brandLogo = giftCardHistoryItem.brandLogo,
                giftCardId = giftCardHistoryItem.id!!,
                giftCardReceivedPurchasedType = getGiftCardReceivedPurchaseType(context,giftCardHistoryItem),
                validityDate = Utility.parseDateTime(
                    giftCardHistoryItem.endDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT9
                ),
                amount = Utility.convertToRs(giftCardHistoryItem.amount)!!,
                status = getGiftCardStatus(context,giftCardHistoryItem.voucherStatus)!!,
                refreshIsVisible = giftCardHistoryItem.voucherStatus.equals("PENDING",true)
            )
        }

        private fun getGiftCardStatus(
            context: Context,
            voucherStatus: String?
        ): Status? {
            when(voucherStatus){
                "PENDING"->{
                    return Status.Pending(status = context.getString(R.string.pending),
                        drawableStart = R.drawable.ic_pending_status, textColor =ContextCompat.getColor(context,R.color.color_orange))
                }
                "COMPLETE"->{
                    return Status.Success(status = context.getString(R.string.success),
                        drawableStart = R.drawable.ic_success_status, textColor = ContextCompat.getColor(context,R.color.reward_continue_button)
                    )

                }
                "CANCELED"->{
                    return Status.Failed(status = context.getString(R.string.failed), drawableStart = R.drawable.ic_failed_status, textColor = ContextCompat.getColor(context,R.color.text_color_red))
                }
                else->{
                    FirebaseCrashlytics.getInstance().recordException(Throwable(GIFT_VOUCHER_STATUS_NOT_VALID))
                    return null
                }
            }
        }

        private fun getGiftCardReceivedPurchaseType(context: Context,giftCardHistoryItem: GiftCardHistoryItem): GiftVoucherReceivedStatus {
            val loggedInUserMobileNo = Utility.getCustomerDataFromPreference()?.mobile
            return if(loggedInUserMobileNo.equals(giftCardHistoryItem.destinationMobileNo)){
                GiftVoucherReceivedStatus.Purchased(text = context.getString(R.string.purchased), backgroundRes = R.drawable.ic_purchased)
            }else{
                if(giftCardHistoryItem.isGifted.equals(YES)){
                    GiftVoucherReceivedStatus.Gifted(text = context.getString(R.string.gifted), backgroundRes = R.drawable.ic_gifted)
                }else{
                    GiftVoucherReceivedStatus.Received(text = context.getString(R.string.received), backgroundRes = R.drawable.ic_reccived)

                }
            }
        }
    }
}

sealed class GiftVoucherReceivedStatus {
    data class Gifted(var text: String, var backgroundRes: Int):GiftVoucherReceivedStatus()
    data class Purchased(var text: String, var backgroundRes: Int):GiftVoucherReceivedStatus()
    data class Received(var text: String, var backgroundRes: Int):GiftVoucherReceivedStatus()
}

sealed class Status {
    data class Success(var status: String, var drawableStart: Int, var textColor: Int) : Status()
    data class Failed(var status: String, var drawableStart: Int, var textColor: Int) : Status()
    data class Pending(var status: String, var drawableStart: Int, var textColor: Int) : Status()
}