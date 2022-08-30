package com.fypmoney.view.arcadegames.brandedcoupons.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.ItemBrandedActiveCouponsBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.brandedcoupons.model.ActiveCouponsListItem

class ActiveBrandedCouponAdapter(
    val onActiveCouponClick: (couponCode: String?) -> Unit,
    val items: ArrayList<ActiveCouponsListItem>,
    val context: Context
) :
    RecyclerView.Adapter<ActiveBrandedCouponAdapter.BrandedActiveCouponVH>() {

    inner class BrandedActiveCouponVH(
        private val binding: ItemBrandedActiveCouponsBinding,
        val onActiveCouponClick: (couponCode: String?) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ActiveCouponsListItem) {
            binding.tvActiveBrandedTitle.text = item.title
            Glide.with(binding.ivActiveBrandLogo.context).load(item.brandLogo)
                .into(binding.ivActiveBrandLogo)
            val couponTimeRemaining = String.format(
                context.resources.getString(R.string.active_coupon_expiry), Utility.parseDateTime(
                    item.expiry,
                    inputFormat = AppConstants.SERVER_DATE_TIME_FORMAT1,
                    outputFormat = AppConstants.CHANGED_DATE_TIME_FORMAT5
                )
            )
            binding.tvActiveBrandedCouponExpiry.text = couponTimeRemaining

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
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}