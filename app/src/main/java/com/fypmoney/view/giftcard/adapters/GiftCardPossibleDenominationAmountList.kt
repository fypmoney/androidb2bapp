package com.fypmoney.view.giftcard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemProductsAmountGiftBinding
import com.fypmoney.extension.executeAfter


class GiftCardPossibleDenominationAmountList(
    private val lifecycleOwner: LifecycleOwner,
    val onAmountClicked: (model: Int) -> Unit
) : ListAdapter<GiftCardPossibleDenominationAmountUiModel,
        GiftCardPossibleDenominationAmountVH>(GiftCardPossibleDenominationAmountUiModelDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCardPossibleDenominationAmountVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemProductsAmountGiftBinding.inflate(inflater, parent, false)
        return GiftCardPossibleDenominationAmountVH(
            binding,
            lifecycleOwner,
            onAmountClicked
        )


    }

    override fun onBindViewHolder(holder: GiftCardPossibleDenominationAmountVH, position: Int) {
        holder.bind(getItem(position))
    }


}


class GiftCardPossibleDenominationAmountVH(
    private val binding: ItemProductsAmountGiftBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onAmountClicked: (model: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: GiftCardPossibleDenominationAmountUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@GiftCardPossibleDenominationAmountVH.lifecycleOwner

            giftCardAmountTv.setOnClickListener {
                onAmountClicked(model.amount)
            }
            giftCardAmountTv.text = model.amountText

        }

    }

}


object GiftCardPossibleDenominationAmountUiModelDiffUtils : DiffUtil.ItemCallback<GiftCardPossibleDenominationAmountUiModel>() {

    override fun areItemsTheSame(
        oldItem: GiftCardPossibleDenominationAmountUiModel,
        newItem: GiftCardPossibleDenominationAmountUiModel
    ): Boolean {
        return oldItem.amount == newItem.amount
    }

    override fun areContentsTheSame(
        oldItem: GiftCardPossibleDenominationAmountUiModel,
        newItem: GiftCardPossibleDenominationAmountUiModel
    ): Boolean {
        return (oldItem.amountText == newItem.amountText) && (oldItem.amountColorCode == newItem.amountColorCode)
    }
}

@Keep
data class GiftCardPossibleDenominationAmountUiModel(
    var amount:Int,
    var amountText:String,
    var amountColorCode:Int
)