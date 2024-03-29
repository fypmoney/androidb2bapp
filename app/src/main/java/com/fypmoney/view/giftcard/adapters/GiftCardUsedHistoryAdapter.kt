package com.fypmoney.view.giftcard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.CardHistoryGiftBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.giftcard.model.GiftHistoryResponseModel


class GiftCardUnsedHistoryAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: GiftHistoryResponseModel) -> Unit,
    val mobile: String?
) : ListAdapter<GiftHistoryResponseModel, GiftCardUnsedHistoryVH>(GiftCardUnsedHistoryDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCardUnsedHistoryVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardHistoryGiftBinding.inflate(inflater, parent, false)
        return GiftCardUnsedHistoryVH(
            binding,
            lifecycleOwner,
            onRecentUserClick,
            mobile
        )
    }

    override fun onBindViewHolder(holder: GiftCardUnsedHistoryVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class GiftCardUnsedHistoryVH(
    private val binding: CardHistoryGiftBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: GiftHistoryResponseModel) -> Unit,
    val mobile: String?
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(user: GiftHistoryResponseModel) {
        binding.executeAfter {
            lifecycleOwner = this@GiftCardUnsedHistoryVH.lifecycleOwner

//            loadImage(recentIv,user.profilePicResourceId,
//                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img),true)
//
            binding.descTv.text = user.message

            if (user.destinationMobileNo == mobile) {
                binding.buygift.text = "Purchased"
            } else if (user.destinationMobileNo != mobile) {
                binding.buygift.text = "RECIEVED"
            }
            binding.giftTitle.text = user.productName
            binding.buygift.setOnClickListener {
                onRecentUserClick(user)
            }
            binding.price.text = "₹" + Utility.convertToRs(user.amount?.toString())

        }
    }

}

object GiftCardUnsedHistoryDiffUtils : DiffUtil.ItemCallback<GiftHistoryResponseModel>() {

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