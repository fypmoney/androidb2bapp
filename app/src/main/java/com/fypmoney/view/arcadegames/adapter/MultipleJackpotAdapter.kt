package com.fypmoney.view.arcadegames.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.ItemMultipleJackpotsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.model.JackpotDetailsItem

class MultipleJackpotAdapter(val onJackpotClick:(productCode:String)->Unit) : ListAdapter<MultipleJackpotUiModel, MultipleJackpotAdapter.MultipleJackpotVH>(MultipleJackpotDiffUtils) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleJackpotVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMultipleJackpotsBinding.inflate(inflater, parent, false)
        return MultipleJackpotVH(binding,onJackpotClick)
    }

    override fun onBindViewHolder(holder: MultipleJackpotVH, position: Int) {
        holder.bind(getItem(position))
    }

    class MultipleJackpotVH(private val binding: ItemMultipleJackpotsBinding,
                            val onJackpotClick:(productCode:String)->Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MultipleJackpotUiModel) {

            Glide.with(binding.ivMultipleJackpotProduct.context).load(item.jackpotImage)
                .into(binding.ivMultipleJackpotProduct)
            binding.tvMultipleProductName.text = item.jackpotName
            binding.tvMultipleTicketDuration.text = item.jackpotDuration
            binding.tvMultipleTicketValue.text = item.jackpotTicketValue.toString()

            if (item.isExpired == "NO") {
                binding.tvExpiredProduct.visibility = View.GONE
            } else {
                binding.tvExpiredProduct.visibility = View.VISIBLE
            }
            binding.cvJackpotItem.setOnClickListener {
                onJackpotClick(item.productCode)
            }


        }

    }

}

@Keep
data class MultipleJackpotUiModel(
    var jackpotImage: String?,
    var jackpotName: String,
    var jackpotDuration: String,
    var jackpotTicketValue: Int?,
    var isExpired: String,
    var productCode:String
) {
    companion object {
        fun fromMultipleJackpotItem(
            context: Context,
            jackpotDetailsItem: JackpotDetailsItem
        ): MultipleJackpotUiModel {
            val jackpotImage = jackpotDetailsItem.detailImage
            val jackpotName = jackpotDetailsItem.productName
            val jackpotDuration = String.format(
                context.resources.getString(R.string.jackpot_duration), Utility.parseDateTime(
                    jackpotDetailsItem.startDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                ),
                Utility.parseDateTime(
                    jackpotDetailsItem.endDate,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                )
            )

            val jackpotTicketValue = jackpotDetailsItem.count

            val isExpired = jackpotDetailsItem.isExpired

            return MultipleJackpotUiModel(
                jackpotImage = jackpotImage,
                jackpotName = jackpotName,
                jackpotDuration = jackpotDuration,
                jackpotTicketValue = jackpotTicketValue,
                isExpired = isExpired,
                productCode = jackpotDetailsItem.productCode!!
            )
        }
    }
}

object MultipleJackpotDiffUtils : DiffUtil.ItemCallback<MultipleJackpotUiModel>() {
    override fun areItemsTheSame(
        oldItem: MultipleJackpotUiModel,
        newItem: MultipleJackpotUiModel
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(
        oldItem: MultipleJackpotUiModel,
        newItem: MultipleJackpotUiModel
    ): Boolean {
        TODO("Not yet implemented")
    }


}
