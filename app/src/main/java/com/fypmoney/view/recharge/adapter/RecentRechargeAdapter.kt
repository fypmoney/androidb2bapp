package com.fypmoney.view.recharge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.RecentRechargeCardBinding
import com.fypmoney.extension.executeAfter

@Keep
data class RecentRechargeUiModel(
    var operatorLogo:Int,
    var mobileNumber:String,
    var operatorNameAndRechargeType:String,
    var lastRechargeDateAndTime:String,
    var rechargeStatus:String,
    var isCheckStatusIsVisible:Boolean,
    var isRepeatRechargeIsVisible:Boolean
)
class RecentRechargeAdapter(
    private val lifeCycleOwner: LifecycleOwner,
    private val onCheckStatusClick:(rechargeItem:RecentRechargeUiModel)->Unit,
    private val onRepeatRechargeClick:(rechargeItem:RecentRechargeUiModel)->Unit) :
    ListAdapter<RecentRechargeUiModel, RecentRechargeAdapter.RechargeItemVH>(RecentRechargeDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RechargeItemVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecentRechargeCardBinding.inflate(inflater, parent, false)
        return RechargeItemVH(
            binding,
            lifeCycleOwner,
            onCheckStatusClick,
            onRepeatRechargeClick
        )
    }


    override fun onBindViewHolder(holder: RechargeItemVH, position: Int) {
        holder.bind(getItem(position))
    }
    class RechargeItemVH(
        private val binding: RecentRechargeCardBinding,
        private val lifecycleOwner: LifecycleOwner,
        val onCheckStatusClick:(rechargeItem:RecentRechargeUiModel)->Unit,
        val onRepeatRechargeClick:(rechargeItem:RecentRechargeUiModel)->Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentRechargeUiModel) {
            binding.executeAfter {
                lifecycleOwner = this@RechargeItemVH.lifecycleOwner
                binding.checkRechargeStatusTv.setOnClickListener {
                    onCheckStatusClick(item)
                }
                binding.repeatRechargeTv.setOnClickListener {
                    onRepeatRechargeClick(item)
                }
            }
        }

    }
}

object RecentRechargeDiffUtils : DiffUtil.ItemCallback<RecentRechargeUiModel>() {

    override fun areItemsTheSame(oldItem: RecentRechargeUiModel, newItem: RecentRechargeUiModel): Boolean {
        return (oldItem.mobileNumber == newItem.mobileNumber)
    }

    override fun areContentsTheSame(oldItem: RecentRechargeUiModel, newItem: RecentRechargeUiModel): Boolean {
        return ((oldItem.operatorLogo == newItem.operatorLogo) && (oldItem.mobileNumber == newItem.mobileNumber)
                && (oldItem.rechargeStatus == newItem.rechargeStatus) &&
            (oldItem.operatorNameAndRechargeType == newItem.operatorNameAndRechargeType)&&
            (oldItem.isCheckStatusIsVisible == newItem.isCheckStatusIsVisible)&&
            (oldItem.isRepeatRechargeIsVisible == newItem.isRepeatRechargeIsVisible))
    }
}