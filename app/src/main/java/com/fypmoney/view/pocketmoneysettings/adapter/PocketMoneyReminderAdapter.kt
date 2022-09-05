package com.fypmoney.view.pocketmoneysettings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemPocketMoneyReminderBinding
import com.fypmoney.view.pocketmoneysettings.model.DataItem

class PocketMoneyReminderAdapter(
    val onClickNotifyDelete: (mobileNumber: String) -> Unit,
    val onClickOpenEditSheet: (reminderData: PocketMoneyReminderUiModel) -> Unit
) :
    ListAdapter<PocketMoneyReminderUiModel, PocketMoneyReminderAdapter.PocketMoneyReminderVH>(
        PocketMoneyReminderDiffUtil
    ) {

    inner class PocketMoneyReminderVH(private val binding: ItemPocketMoneyReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PocketMoneyReminderUiModel) {
            binding.tvName.text = item.name
            binding.tvPocketAmount.text = String.format("₹" + item.amount.toString())
            binding.tvPocketReminderFrequency.text = item.frequency
            binding.tvMobileNumber.text = String.format("+91 " + item.mobile)

            binding.ivReminderEdit.setOnClickListener {
                onClickOpenEditSheet(item)
            }

            binding.ivReminderDelete.setOnClickListener {
                onClickNotifyDelete(item.mobile)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PocketMoneyReminderVH {
        val binding = ItemPocketMoneyReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PocketMoneyReminderVH(binding)
    }

    override fun onBindViewHolder(holder: PocketMoneyReminderVH, position: Int) {
        holder.bind(getItem(position))
    }

}

@Keep
data class PocketMoneyReminderUiModel(
    var mobile: String,
    var name: String?,
    var amount: Int?,
    var frequency: String?
) {
    companion object {
        fun fromPocketMoneyReminderItem(dataItem: DataItem): PocketMoneyReminderUiModel {
            val mobile = dataItem.mobile
            val name = dataItem.name
            val amount = dataItem.amount
            val frequency = dataItem.frequency

            return PocketMoneyReminderUiModel(
                mobile = mobile.toString(),
                name = name,
                amount = amount,
                frequency = frequency
            )
        }
    }
}

object PocketMoneyReminderDiffUtil : DiffUtil.ItemCallback<PocketMoneyReminderUiModel>() {
    override fun areItemsTheSame(
        oldItem: PocketMoneyReminderUiModel,
        newItem: PocketMoneyReminderUiModel
    ): Boolean {
        return ((oldItem.mobile == newItem.mobile) && (oldItem.name == newItem.name) && (oldItem.amount == newItem.amount) && (oldItem.frequency == newItem.frequency))
    }

    override fun areContentsTheSame(
        oldItem: PocketMoneyReminderUiModel,
        newItem: PocketMoneyReminderUiModel
    ): Boolean {
        return oldItem == newItem
    }

}
