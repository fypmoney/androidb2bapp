package com.fypmoney.view.giftCardModule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.databinding.CardHistoryGiftBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.model.GiftHistoryResponseModel


class GiftCardHistoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onItemClicked: (model: GiftHistoryResponseModel) -> Unit,
    val mobile: String?,
    val onReloadClicked: (model: GiftHistoryResponseModel) -> Unit
) : ListAdapter<GiftHistoryResponseModel, GiftCardHistoryVH>(GiftCardHistoryDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCardHistoryVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardHistoryGiftBinding.inflate(inflater, parent, false)
        return GiftCardHistoryVH(
            binding,
            lifecycleOwner,
            onItemClicked,
            mobile,
            onReloadClicked
        )
    }

    override fun onBindViewHolder(holder: GiftCardHistoryVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class GiftCardHistoryVH(
    private val binding: CardHistoryGiftBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: GiftHistoryResponseModel) -> Unit,
    val mobile: String?,
    val onReloadClicked: (model: GiftHistoryResponseModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(user: GiftHistoryResponseModel) {
        binding.executeAfter {
            lifecycleOwner = this@GiftCardHistoryVH.lifecycleOwner
            if (user?.voucherStatus == "PENDING" && user?.isVoucherPurchased == AppConstants.NO) {
                binding.reload.visibility = View.VISIBLE
            } else {
                binding.reload.visibility = View.GONE
            }

            Glide.with(binding.offerIv.context).load(user.detailImage)
                .placeholder(shimmerDrawable()).into(binding.offerIv)

            binding.descTv.text = user.description


            if (user.destinationMobileNo == mobile) {

                if(user.voucherStatus=="COMPLETE"){
                    binding.buygift.text = "Purchased"
                    binding.buygift.background.setTint(
                        ContextCompat.getColor(
                            binding.buygift.context,
                            R.color.reward_continue_button
                        )
                    )
                }else if(user.voucherStatus=="PENDING"){
                    binding.buygift.text = "Pending"
                    binding.buygift.background.setTint(
                        ContextCompat.getColor(
                            binding.buygift.context,
                            R.color.colorSelectedMenu
                        )
                    )
                }

            } else if (user.destinationMobileNo != mobile) {
                if (user.isGifted == AppConstants.NO) {
                    binding.buygift.text = "RECIEVED"
                    binding.buygift.background.setTint(
                        ContextCompat.getColor(
                            binding.buygift.context,
                            R.color.color_green
                        )
                    )
                } else {
                    binding.buygift.background.setTint(
                        ContextCompat.getColor(
                            binding.buygift.context,
                            R.color.colorSelectedMenu
                        )
                    )
                    binding.buygift.text = "Gifted"
                }

            }

            binding.giftTitle.text = user.brandName
            binding.gridOfferCv.setOnClickListener {
                if (user?.isVoucherPurchased == AppConstants.YES) {
                    onRecentUserClick(user)
                }

            }
            binding.reload.setOnClickListener {

                onReloadClicked(user)
            }
            binding.price.text = "₹" + Utility.convertToRs(user.amount?.toString())

        }
    }

}

object GiftCardHistoryDiffUtils : DiffUtil.ItemCallback<GiftHistoryResponseModel>() {

    override fun areItemsTheSame(
        oldItem: GiftHistoryResponseModel,
        newItem: GiftHistoryResponseModel
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: GiftHistoryResponseModel,
        newItem: GiftHistoryResponseModel
    ): Boolean {
        return oldItem == newItem
    }
}