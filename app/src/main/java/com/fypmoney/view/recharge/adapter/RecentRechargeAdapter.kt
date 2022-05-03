package com.fypmoney.view.recharge.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.RecentRechargeCardBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.NO
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.RecentRechargeItem


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
            binding.operatorIv.setImageResource(item.operatorLogo)
            binding.operatorRechargeTypeTv.text = item.operatorNameAndRechargeType
            binding.mobileNoTv.text = item.mobileNumber
            binding.lastRechargeTv.text = item.lastRechargeDateAndTime
            binding.rechargeStatusTv.setCompoundDrawables(item.statusDrawable,null,null,null)
            binding.rechargeStatusTv.text = item.rechargeStatus
            if(item.isRepeatRechargeIsVisible) binding.repeatRechargeTv.toVisible() else binding.repeatRechargeTv.toGone()
            if(item.isCheckStatusIsVisible) binding.checkRechargeStatusTv.toVisible() else binding.checkRechargeStatusTv.toGone()

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

@Keep
data class RecentRechargeUiModel(
    var operatorLogo:Int,
    var mobileNumber:String,
    var operatorNameAndRechargeType:String,
    var lastRechargeDateAndTime:String,
    var rechargeStatus:String,
    var isCheckStatusIsVisible:Boolean,
    var isRepeatRechargeIsVisible:Boolean,
    var statusDrawable:Drawable,
){
    companion object{
        fun fromRechargeItem(context: Context, recentRechargeItem: RecentRechargeItem):RecentRechargeUiModel{
            val operatorLogo = if(recentRechargeItem.operatorName.equals("Airtel",true)){
                R.drawable.ic_airtel
            }else if(recentRechargeItem.operatorName.equals("JIO",true) ){
                R.drawable.ic_jio
            }else if(recentRechargeItem.operatorName.equals("Vodafone",true)){
                R.drawable.ic_vodafone

            }else{
                R.drawable.ic_user
            }
            val operatorNameAndRechargeType = recentRechargeItem.operatorName+"-"+recentRechargeItem.cardType
            val lastRechargeDateAndTime = String.format(context.resources.getString(R.string.last_recharge_on),
                Utility.convertToRs(recentRechargeItem.planPrice!!),Utility.parseDateTime(
                    recentRechargeItem.txnTime,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT8
                ))
            val isCheckStatusIsVisible = recentRechargeItem.isPurchased == NO && recentRechargeItem.paymentStatus == "PENDING"
            val isRepeatRechargeIsVisible = recentRechargeItem.paymentStatus == "SUCCESS" && recentRechargeItem.isPurchased == YES
            val rechargeStatus = Utility.toTitleCase(recentRechargeItem.paymentStatus)
            val statusDrawable = when (recentRechargeItem.paymentStatus) {
                "SUCCESS" -> {
                    ContextCompat.getDrawable(context,R.drawable.ic_success_status)
                }
                "FAILED" -> {
                    ContextCompat.getDrawable(context,R.drawable.ic_failed_status)
                }
                else -> {
                    ContextCompat.getDrawable(context,R.drawable.ic_pending_status)

                }
            }
            return RecentRechargeUiModel(
                operatorLogo = operatorLogo,
                mobileNumber = recentRechargeItem.mobileNo,
                operatorNameAndRechargeType = operatorNameAndRechargeType,
                lastRechargeDateAndTime = lastRechargeDateAndTime,
                rechargeStatus = rechargeStatus!!,
                isCheckStatusIsVisible = isCheckStatusIsVisible,
                isRepeatRechargeIsVisible = isRepeatRechargeIsVisible,
                statusDrawable = statusDrawable!!
            )
        }

    }
}