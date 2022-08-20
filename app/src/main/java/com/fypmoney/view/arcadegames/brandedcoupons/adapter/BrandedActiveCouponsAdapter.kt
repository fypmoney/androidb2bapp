package com.fypmoney.view.arcadegames.brandedcoupons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.ItemBrandedActiveCouponsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.brandedcoupons.model.ActiveCouponsListItem

class BrandedActiveCouponsAdapter(val onActiveCouponClick: (couponCode: String) -> Unit) :
    ListAdapter<BrandedActiveCouponsUiModel, BrandedActiveCouponsAdapter.BrandedActiveCouponVH>(
        BrandedActiveCouponDiffUtils
    ) {
    class BrandedActiveCouponVH(
        private val binding: ItemBrandedActiveCouponsBinding,
        val onActiveCouponClick: (couponCode: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BrandedActiveCouponsUiModel) {
            binding.tvActiveBrandedTitle.text = item.couponTitle
            Glide.with(binding.ivActiveBrandLogo.context).load(item.brandLogo)
                .into(binding.ivActiveBrandLogo)
            binding.tvActiveBrandedCouponExpiry.text = item.couponTimeRemaining

            binding.tvActiveBrandedRedeemNow.setOnClickListener {
                onActiveCouponClick(item.couponCode)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandedActiveCouponVH {
        val binding = ItemBrandedActiveCouponsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BrandedActiveCouponVH(binding, onActiveCouponClick)
    }

    override fun onBindViewHolder(holder: BrandedActiveCouponVH, position: Int) {
        holder.bind(getItem(position))
    }
}

@Keep
data class BrandedActiveCouponsUiModel(
    var brandLogo: String?,
    var couponTitle: String?,
    var couponTimeRemaining: String?,
    var couponCode: String
) {
    companion object {
        fun fromBrandedActiveCouponItem(
            context: Context,
            activeCouponsListItem: ActiveCouponsListItem
        ): BrandedActiveCouponsUiModel {
            val brandLogo = activeCouponsListItem.brandLogo
            val couponTitle = activeCouponsListItem.title
            val couponTimeRemaining = String.format(
                context.resources.getString(R.string.active_coupon_expiry), Utility.parseDateTime(
                    activeCouponsListItem.expiry,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT5
                )
            )

            val couponCode = activeCouponsListItem.couponCode

            return BrandedActiveCouponsUiModel(
                brandLogo = brandLogo,
                couponTitle = couponTitle,
                couponTimeRemaining = couponTimeRemaining,
                couponCode = couponCode.toString()
            )
        }
    }
}

object BrandedActiveCouponDiffUtils : DiffUtil.ItemCallback<BrandedActiveCouponsUiModel>() {
    override fun areItemsTheSame(
        oldItem: BrandedActiveCouponsUiModel,
        newItem: BrandedActiveCouponsUiModel
    ): Boolean {
        return (oldItem.couponTitle == newItem.couponTitle)
    }

    override fun areContentsTheSame(
        oldItem: BrandedActiveCouponsUiModel,
        newItem: BrandedActiveCouponsUiModel
    ): Boolean {
        return oldItem == newItem
    }

}