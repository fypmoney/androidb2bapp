package com.fypmoney.view.giftCardModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.ItemProductsAmountGiftBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.model.VoucherProductItem


class GiftProductListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val giftcardClicked: (model: Int) -> Unit
) : ListAdapter<VoucherProductItem, GiftProducstVH>(GiftProductDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftProducstVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductsAmountGiftBinding.inflate(inflater, parent, false)
        return GiftProducstVH(
            binding,
            lifecycleOwner,
            giftcardClicked
        )


    }

    override fun onBindViewHolder(holder: GiftProducstVH, position: Int) {
        holder.bind(getItem(position))
    }


}

class GiftProducstVH(
    private val binding: ItemProductsAmountGiftBinding,
    private val lifecycleOwner: LifecycleOwner,
    val giftcardClicked: (model: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var selected_position: Int? = null

    fun bind(user: VoucherProductItem) {
        binding.executeAfter {
            lifecycleOwner = this@GiftProducstVH.lifecycleOwner

            binding.layoutAmount.setOnClickListener {

                giftcardClicked(bindingAdapterPosition)

            }
            if (user.selected == true) {
                binding.layoutAmount.setBackgroundResource(R.drawable.curved_background30_grey)

            } else {
                binding.layoutAmount.setBackgroundResource(R.drawable.curved_background29)
            }


            amountTv.text = Utility.convertToRs(user.amount.toString())
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