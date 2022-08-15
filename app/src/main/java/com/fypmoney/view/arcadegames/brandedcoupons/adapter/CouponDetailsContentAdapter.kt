package com.fypmoney.view.arcadegames.brandedcoupons.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemCouponDetailsExpandedContentBinding
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetailsContentModel

class CouponDetailsContentAdapter :
    ListAdapter<CouponDetailsContentUiModel, CouponDetailsContentAdapter.CouponDetailsContentVH>(
        CouponDetailsContentDiffUtils
    ) {

    class CouponDetailsContentVH(private val binding: ItemCouponDetailsExpandedContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CouponDetailsContentUiModel) {
            binding.tvExpandRedeemContent.text = item.couponDetailsContent
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponDetailsContentVH {
        val binding = ItemCouponDetailsExpandedContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponDetailsContentVH(binding)
    }

    override fun onBindViewHolder(holder: CouponDetailsContentVH, position: Int) {
        holder.bind(getItem(position))
    }
}

object CouponDetailsContentDiffUtils : DiffUtil.ItemCallback<CouponDetailsContentUiModel>() {
    override fun areItemsTheSame(
        oldItem: CouponDetailsContentUiModel,
        newItem: CouponDetailsContentUiModel
    ): Boolean {
        return (oldItem.couponDetailsContent == newItem.couponDetailsContent)
    }

    override fun areContentsTheSame(
        oldItem: CouponDetailsContentUiModel,
        newItem: CouponDetailsContentUiModel
    ): Boolean {
        return ((oldItem.couponDetailsContent == newItem.couponDetailsContent))
    }

}

@Keep
data class CouponDetailsContentUiModel(
    var couponDetailsContent: String
) {
    companion object {
        fun fromCouponDetailsContent(couponDetailsContentModel: CouponDetailsContentModel): CouponDetailsContentUiModel {
            val couponDetailsContent = couponDetailsContentModel.couponDetailsContent

            return CouponDetailsContentUiModel(couponDetailsContent)
        }
    }
}