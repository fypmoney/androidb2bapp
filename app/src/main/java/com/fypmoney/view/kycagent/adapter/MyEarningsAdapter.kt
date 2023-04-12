package com.fypmoney.view.kycagent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemMyEarningsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.model.EarningItem

class MyEarningsAdapter : ListAdapter<MyEarningsUiModel, MyEarningsAdapter.MyEarningsVH>(MyEarningsDiffUtils) {

    class MyEarningsVH(private val binding : ItemMyEarningsBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: MyEarningsUiModel){
            binding.tvEarningsDate.text = item.kycEarningDate
            binding.tvEarnings.text = item.kycEarningsValue
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEarningsVH {
        val binding = ItemMyEarningsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyEarningsVH(binding)
    }

    override fun onBindViewHolder(holder: MyEarningsVH, position: Int) {
        holder.bind(getItem(position))
    }
}

@Keep
data class MyEarningsUiModel(
    var earningsName: String,
    var kycEarningDate: String,
    var kycEarningsValue: String
) {
    companion object {
        fun fromEarningsItem(
            earningItem: EarningItem
        ): MyEarningsUiModel {
            return MyEarningsUiModel(
                "KYC Activity",
                Utility.parseDateTime(
                    earningItem.dateTime,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT1
                ),
                "+ ₹ " + Utility.convertToRs(earningItem.earningAmount.toString()).toString()
            )
        }
    }
}

object MyEarningsDiffUtils : DiffUtil.ItemCallback<MyEarningsUiModel>() {
    override fun areItemsTheSame(oldItem: MyEarningsUiModel, newItem: MyEarningsUiModel): Boolean {
        return (oldItem == newItem)
    }

    override fun areContentsTheSame(
        oldItem: MyEarningsUiModel,
        newItem: MyEarningsUiModel
    ): Boolean {
        return (
                (oldItem.kycEarningDate == newItem.kycEarningDate) &&
                        (oldItem.kycEarningsValue == newItem.kycEarningsValue)
                )
    }

}