package com.fypmoney.view.giftCardModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.databinding.CardHistoryGiftBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.model.GiftHistoryResponseModel


class GiftCardHistoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: GiftHistoryResponseModel) -> Unit,
    val mobile: String?
) : ListAdapter<GiftHistoryResponseModel, GiftCardHistoryVH>(GiftCardHistoryDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCardHistoryVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardHistoryGiftBinding.inflate(inflater, parent, false)
        return GiftCardHistoryVH(
            binding,
            lifecycleOwner,
            onRecentUserClick,
            mobile
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
    val mobile: String?
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(user: GiftHistoryResponseModel) {
        binding.executeAfter {
            lifecycleOwner = this@GiftCardHistoryVH.lifecycleOwner


            Glide.with(binding.offerIv.context).load(user.detailImage)
                .placeholder(shimmerDrawable()).into(binding.offerIv)

            binding.descTv.text = user.description

            if (user.destinationMobileNo == mobile) {
                binding.buygift.text = "Purchased"
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
                onRecentUserClick(user)
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