package com.fypmoney.view.giftCardModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemGiftCardBinding
import com.fypmoney.databinding.ItemProductsAmountGiftBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.view.giftCardModule.model.VoucherProductItem


class GiftProductListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: VoucherProductItem) -> Unit
) : ListAdapter<VoucherProductItem, GiftProducstVH>(GiftProductDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftProducstVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductsAmountGiftBinding.inflate(inflater, parent, false)
        return GiftProducstVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )


    }

    override fun onBindViewHolder(holder: GiftProducstVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class GiftProducstVH(
    private val binding: ItemProductsAmountGiftBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: VoucherProductItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: VoucherProductItem) {
        binding.executeAfter {
            lifecycleOwner = this@GiftProducstVH.lifecycleOwner
            var item = user
//            loadImage(recentIv,user.profilePicResourceId,
//                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img),true)


            amountTv.text = user.amount.toString()
        }
    }

}

object GiftProductDiffUtils : DiffUtil.ItemCallback<VoucherProductItem>() {

    override fun areItemsTheSame(
        oldItem: VoucherProductItem,
        newItem: VoucherProductItem
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: VoucherProductItem,
        newItem: VoucherProductItem
    ): Boolean {
        return oldItem == newItem
    }
}