package com.fypmoney.view.arcadegames.brandedcoupons.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.databinding.ItemCouponDetailsTitleBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetailsTitleModel

class CouponDetailsTitleAdapter(private val onItemExpendedClick:OnDetailsClicked) : ListAdapter<CouponDetailsTitleUiModel, CouponDetailsTitleVH>(
        CouponDetailsTitleDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponDetailsTitleVH {
        val binding = ItemCouponDetailsTitleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponDetailsTitleVH(binding,onItemExpendedClick)
    }

    override fun onBindViewHolder(holder: CouponDetailsTitleVH, position: Int) {

        holder.bind(getItem(position))

    }

    private fun setRecyclerData(
        binding: ItemCouponDetailsTitleBinding,
        isExpanded: Boolean,
        arrayCouponDetails: List<String>?
    ) {
//        val animationCollapse: Animation =
//            AnimationUtils.loadAnimation(binding.root.context, R.anim.list_collapse)
//        val animationExpanded: Animation =
//            AnimationUtils.loadAnimation(binding.root.context, R.anim.list_expand)




//        if (bindingAdapterPosition == 0) {
            if (!isExpanded) {
                binding.rvCouponDetailsContent.toGone()

                val redeemArray = arrayCouponDetails?.map {
                    CouponDetailsContentUiModel(it)
                }

//                binding.rvCouponDetailsContent.startAnimation(animationExpanded)

//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(90F)

                val adapter = CouponDetailsContentAdapter()
                binding.rvCouponDetailsContent.adapter = adapter
                adapter.submitList(redeemArray)
                binding.rvCouponDetailsContent.toVisible()


            }
            else{
                binding.rvCouponDetailsContent.toGone()
//                binding.cvCouponRedeemRules.startAnimation(animationCollapse)
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(0F)
            }

    }


}

class CouponDetailsTitleVH(private val binding: ItemCouponDetailsTitleBinding,
private val onItemExpendedClick:OnDetailsClicked
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CouponDetailsTitleUiModel) {

        binding.tvRedeemRulesTitle.text = item.couponDetailsTitle
        binding.ivRedeemRulesOffer.setImageResource(item.couponDetailsIcon)


        val layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.rvCouponDetailsContent.layoutManager = layoutManager

        if (item.isExpended) {

            val redeemArray = item.arrayCouponDetails?.map {
                CouponDetailsContentUiModel(it)
            }

            Glide.with(binding.ivExpandRedeemRules.context).load(R.drawable.ic_vector_down).into(binding.ivExpandRedeemRules)
//                binding.rvCouponDetailsContent.startAnimation(animationExpanded)

//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(90F)

            val adapter = CouponDetailsContentAdapter()
            binding.rvCouponDetailsContent.adapter = adapter
            adapter.submitList(redeemArray)
            binding.rvCouponDetailsContent.toVisible()

        }
        else{
            binding.rvCouponDetailsContent.toGone()
            Glide.with(binding.ivExpandRedeemRules.context).load(R.drawable.ic_arrow_right_black).into(binding.ivExpandRedeemRules)
//                binding.cvCouponRedeemRules.startAnimation(animationCollapse)
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(0F)
        }

        binding.cvCouponRedeemRules.setOnClickListener {
            // setRecyclerData(binding, bindingAdapterPosition, isExpanded, item.arrayCouponDetails)
            item.isExpended = !item.isExpended
            onItemExpendedClick.expendDetails(item)

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
        return ((oldItem.couponDetailsTitle == newItem.couponDetailsTitle) && (oldItem.isExpended == newItem.isExpended) && (oldItem.couponDetailsIcon == newItem.couponDetailsIcon))
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