package com.fypmoney.view.recharge.adapter

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
import com.fypmoney.databinding.RecentRechargeCardBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
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
            binding.rechargeStatusTv.setCompoundDrawablesWithIntrinsicBounds(item.statusDrawable,0,0,0)
            binding.rechargeStatusTv.setTextColor(item.statusTextColor)
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
    var statusDrawable:Int,
    var statusTextColor:Int,
    var operatorName:String?= null,
    var circle:String?=null,
    var cardType:String?=null,
    var requestOperatorId:String? = null,
    var amount:String? = null
){
    companion object{
        fun fromRechargeItem(context: Context, recentRechargeItem: RecentRechargeItem):RecentRechargeUiModel{
            val operatorLogo = if(recentRechargeItem.operatorName.equals("Airtel",true)){
                R.drawable.ic_airtel
            }else if(recentRechargeItem.operatorName.equals("JIO",true) ){
                R.drawable.ic_jio
            }else if(recentRechargeItem.operatorName.equals("Vodafone",true)){
                R.drawable.ic_vodafone
            }else if(recentRechargeItem.operatorName.equals("Airtel Digital Tv",true)){
                R.drawable.ic_dth_airtel_digital
            }else if(recentRechargeItem.operatorName.equals("Dish TV",true)) {
                R.drawable.ic_dth_dish_tv
            }else if(recentRechargeItem.operatorName.equals("Tata Sky",true)){
                R.drawable.ic_dth_tata_play
            }else if(recentRechargeItem.operatorName.equals("Videocon D2H",true)){
                R.drawable.ic_dth_videocon
            }else{
                R.drawable.ic_user
            }
            val operatorNameAndRechargeType = if(recentRechargeItem.operatorName.isNullOrEmpty())  Utility.toTitleCase(recentRechargeItem.cardType) else recentRechargeItem.operatorName+"-"+Utility.toTitleCase(recentRechargeItem.cardType)

            val lastRechargeDateAndTime = String.format(context.resources.getString(R.string.last_recharge_on),
                Utility.convertToRs(recentRechargeItem.planPrice!!),Utility.parseDateTime(
                    recentRechargeItem.txnTime,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT8
                ))
            val isCheckStatusIsVisible =  (recentRechargeItem.paymentStatus == "PENDING")
            val isRepeatRechargeIsVisible =  (recentRechargeItem.paymentStatus == "SUCCESS")
            val rechargeStatus = Utility.toTitleCase(recentRechargeItem.paymentStatus)
            val statusTextColor = when (recentRechargeItem.paymentStatus) {
                "SUCCESS" -> {
                    ContextCompat.getColor(context,R.color.reward_continue_button)
                }
                "FAILED" -> {
                    ContextCompat.getColor(context,R.color.text_color_red)
                }
                else -> {
                    ContextCompat.getColor(context,R.color.color_orange)
                }
            }
            val statusDrawable = when (recentRechargeItem.paymentStatus) {
                "SUCCESS" -> {
                    R.drawable.ic_success_status
                }
                "FAILED" -> {
                    R.drawable.ic_failed_status
                }
                else -> {
                    R.drawable.ic_pending_status
                }
            }
            val operatorName = recentRechargeItem.operatorName
            val circle = recentRechargeItem.circle
            val cardType = recentRechargeItem.cardType
            val requestOperatorId = recentRechargeItem.requestOperatorId
            val amount = recentRechargeItem.planPrice
            return RecentRechargeUiModel(
                operatorLogo = operatorLogo,
                mobileNumber = recentRechargeItem.cardNo!!,
                operatorNameAndRechargeType = operatorNameAndRechargeType!!,
                lastRechargeDateAndTime = lastRechargeDateAndTime,
                rechargeStatus = rechargeStatus!!,
                isCheckStatusIsVisible = isCheckStatusIsVisible,
                isRepeatRechargeIsVisible = isRepeatRechargeIsVisible,
                statusDrawable = statusDrawable,
                statusTextColor = statusTextColor,
                operatorName = operatorName,
                circle = circle,
                cardType = cardType!!,
                requestOperatorId = requestOperatorId,
                amount = amount
            )
        }

    }
}