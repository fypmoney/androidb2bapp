package com.fypmoney.view.giftCardModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.ItemProductsAmountGiftBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.giftCardModule.model.VoucherProductAmountsItem


class GiftProductListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val giftcardClicked: (model: Int) -> Unit
) : ListAdapter<VoucherProductAmountsItem, GiftProducstVH>(GiftProductDiffUtils) {

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
        holder.bind(getItem(position), position)
    }


}


class GiftProducstVH(
    private val binding: ItemProductsAmountGiftBinding,
    private val lifecycleOwner: LifecycleOwner,
    val giftcardClicked: (model: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var selected_position: Int? = null

    fun bind(user: VoucherProductAmountsItem, position: Int) {
        binding.executeAfter {
            lifecycleOwner = this@GiftProducstVH.lifecycleOwner

            binding.layoutAmount.setOnClickListener {

                giftcardClicked(bindingAdapterPosition)

            }


            if (user.selected == true) {
                binding.layoutAmount.setBackgroundResource(R.drawable.curved_background30_grey)

            } else {
                if (position % 4 == 0) {

                    binding.layoutAmount.background.setTint(
                        ContextCompat.getColor(
                            binding.layoutAmount.context,
                            R.color.color_green
                        )
                    )


                } else if (position % 4 == 1) {

                    binding.layoutAmount.background.setTint(
                        ContextCompat.getColor(
                            binding.layoutAmount.context,
                            R.color.chores_background
                        )
                    )

                } else if (position % 4 == 2) {

                    binding.layoutAmount.background.setTint(
                        ContextCompat.getColor(
                            binding.layoutAmount.context,
                            R.color.color_pink
                        )
                    )


                } else if (position % 4 == 3) {


                    binding.layoutAmount.background.setTint(
                        ContextCompat.getColor(
                            binding.layoutAmount.context,
                            R.color.card_gift
                        )
                    )

                }
            }




            amountTv.text = user.name.toString()

        }

    }

}


object GiftProductDiffUtils : DiffUtil.ItemCallback<VoucherProductAmountsItem>() {

    override fun areItemsTheSame(
        oldItem: VoucherProductAmountsItem,
        newItem: VoucherProductAmountsItem
    ): Boolean {
        return false
    }

    override fun areContentsTheSame(
        oldItem: VoucherProductAmountsItem,
        newItem: VoucherProductAmountsItem
    ): Boolean {
        return oldItem == newItem
    }
}