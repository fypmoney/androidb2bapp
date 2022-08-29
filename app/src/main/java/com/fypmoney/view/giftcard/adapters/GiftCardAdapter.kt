package com.fypmoney.view.giftcard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.databinding.ItemGiftCardBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.view.giftcard.model.VoucherBrandItem


class GiftCardAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: VoucherBrandItem) -> Unit
) : ListAdapter<VoucherBrandItem, GiftCardVH>(GiftCardDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCardVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGiftCardBinding.inflate(inflater, parent, false)
        return GiftCardVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )
    }

    override fun onBindViewHolder(holder: GiftCardVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class GiftCardVH(
    private val binding: ItemGiftCardBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: VoucherBrandItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: VoucherBrandItem) {
        binding.executeAfter {
            lifecycleOwner = this@GiftCardVH.lifecycleOwner

//            loadImage(recentIv,user.profilePicResourceId,
//                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img),true)
//
            binding.descTv.text = user.brandTagLine1
            Glide.with(binding.brandLogo.context).load(user?.brandLogo)
                .placeholder(shimmerDrawable()).into(binding.brandLogo)

            binding.buygift.setOnClickListener {
                onRecentUserClick(user)
            }


        }

    }

}

object GiftCardDiffUtils : DiffUtil.ItemCallback<VoucherBrandItem>() {

    override fun areItemsTheSame(oldItem: VoucherBrandItem, newItem: VoucherBrandItem): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: VoucherBrandItem, newItem: VoucherBrandItem): Boolean {
        return oldItem == newItem
    }
}