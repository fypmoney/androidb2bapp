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
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ItemProductsAmountGiftBinding
import com.fypmoney.extension.dp
import com.fypmoney.extension.executeAfter


class GiftCardPossibleDenominationAmountListAdapter(
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
            giftCardAmountTv.setBackgroundColor(model.amountColorCode)
            binding.giftCardAmountTv.invalidate()
            setBackgroundDrawable(view = binding.giftCardAmountTv,
                backgroundColor = model.amountColorCode,
                cornerRadius = (30.0f.dp).toFloat(),
                strokeColor = null,
                strokeWidth = null,
                isRounded = false
            )
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
){
    companion object{
        fun possibleDenominationListToGiftCardPossibleDenominationAmountUiModel(context: Context, amount:String):GiftCardPossibleDenominationAmountUiModel{
            return GiftCardPossibleDenominationAmountUiModel(
                amount = amount.toInt(),
                amountText = String.format(context.getString(R.string.amount_with_currency),amount),
                amountColorCode = getRandomColorCode(context)
            )
        }

        fun getRandomColorCode(context: Context):Int{
            val listOfColor = arrayOf(ContextCompat.getColor(context,R.color.amount_bg1),
                ContextCompat.getColor(context,R.color.amount_bg2),
                ContextCompat.getColor(context,R.color.amount_bg3),
                ContextCompat.getColor(context,R.color.amount_bg4))
            return listOfColor[(0..3).random()]
        }
    }
}