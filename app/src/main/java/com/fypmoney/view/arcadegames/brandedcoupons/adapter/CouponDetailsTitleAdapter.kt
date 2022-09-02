package com.fypmoney.view.arcadegames.brandedcoupons.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.ItemCouponDetailsTitleBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetailsTitleModel

class CouponDetailsTitleAdapter(private val lifecycleOwner: LifecycleOwner,private val onItemExpendedClick:OnDetailsClicked) :
    ListAdapter<CouponDetailsTitleUiModel, CouponDetailsTitleVH>(
        CouponDetailsTitleDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponDetailsTitleVH {
        val binding = ItemCouponDetailsTitleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponDetailsTitleVH(binding,lifecycleOwner,onItemExpendedClick)
    }

    override fun onBindViewHolder(holder: CouponDetailsTitleVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class CouponDetailsTitleVH(private val binding: ItemCouponDetailsTitleBinding,
                           private val lifecycleOwner1: LifecycleOwner,
private val onItemExpendedClick:OnDetailsClicked
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponDetailsTitleUiModel) {
        binding.executeAfter {
            lifecycleOwner = lifecycleOwner1
            binding.tvRedeemRulesTitle.text = item.couponDetailsTitle
            binding.ivRedeemRulesOffer.setImageResource(item.couponDetailsIcon)

            val redeemArray = item.arrayCouponDetails?.map {
                CouponDetailsContentUiModel(it)
            }

            val adapter = CouponDetailsContentAdapter()
            binding.rvCouponDetailsContent.adapter = adapter
            adapter.submitList(redeemArray)



            binding.cvCouponRedeemRules.setOnClickListener {
                /*item.isExpended = !item.isExpended
                onItemExpendedClick.expendDetails(item)*/
                if (!item.isExpended) {
                    binding.ivExpandRedeemRules.setImageResource(R.drawable.ic_forword_arrow)
                    binding.ivExpandRedeemRules.rotation = 90.0f
                    binding.rvCouponDetailsContent.toVisible()
                    item.isExpended = true
                }
                else{
                    item.isExpended = false
                    binding.ivExpandRedeemRules.rotation = 0.0f
                    binding.ivExpandRedeemRules.setImageResource(R.drawable.ic_forword_arrow)
                    binding.rvCouponDetailsContent.toGone()
                }

            }
        }

    }
}

object CouponDetailsTitleDiffUtils : DiffUtil.ItemCallback<CouponDetailsTitleUiModel>() {
    override fun areItemsTheSame(
        oldItem: CouponDetailsTitleUiModel,
        newItem: CouponDetailsTitleUiModel
    ): Boolean {
        return (oldItem == newItem)
    }

    override fun areContentsTheSame(
        oldItem: CouponDetailsTitleUiModel,
        newItem: CouponDetailsTitleUiModel
    ): Boolean {
        return ((oldItem.isExpended == newItem.isExpended))
    }

}

@Keep
data class CouponDetailsTitleUiModel(
    var couponDetailsTitle: String,
    var couponDetailsIcon: Int,
    var arrayCouponDetails: List<String>?,
    var isExpended:Boolean = false
) {
    companion object {
        fun fromCouponDetailsTitle(couponDetailsTitleModel: CouponDetailsTitleModel): CouponDetailsTitleUiModel {
            val couponDetailsTitle = couponDetailsTitleModel.couponDetailsTitle
            val couponDetailsIcon = couponDetailsTitleModel.couponDetailsIcon
            val arrayCouponDetails = couponDetailsTitleModel.arrayCouponDetails
            return CouponDetailsTitleUiModel(couponDetailsTitle, couponDetailsIcon, arrayCouponDetails,couponDetailsTitleModel.isExpended)
        }
    }
}

interface OnDetailsClicked{
     fun expendDetails(copounDetailsItem:CouponDetailsTitleUiModel)
}