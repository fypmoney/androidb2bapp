package com.fypmoney.view.arcadegames.brandedcoupons.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemCouponDetailsTitleBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetailsTitleModel

class CouponDetailsTitleAdapter(
    array: List<String>?,
    howToRedeemArray: List<String>?,
    offerDetailsArray: List<String>?
) :
    ListAdapter<CouponDetailsTitleUiModel, CouponDetailsTitleAdapter.CouponDetailsTitleVH>(
        CouponDetailsTitleDiffUtils
    ) {

    private var redeemArray = arrayListOf<CouponDetailsContentUiModel>()
    private var offerArray = arrayListOf<CouponDetailsContentUiModel>()
    private var newTncArray = arrayListOf<CouponDetailsContentUiModel>()

    private lateinit var arrayRedeem: List<String>
    private lateinit var arrayOffer: List<String>
    private lateinit var arrayTNC: List<String>

    private var mExpandedPosition = -1

    init {
        if (array != null) {
            arrayTNC = array
        }
        if (offerDetailsArray != null) {
            arrayOffer = offerDetailsArray
        }
        if (howToRedeemArray != null) {
            arrayRedeem = howToRedeemArray
        }
    }

    inner class CouponDetailsTitleVH(private val binding: ItemCouponDetailsTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CouponDetailsTitleUiModel) {

            binding.tvRedeemRulesTitle.text = item.couponDetailsTitle
            binding.ivRedeemRulesOffer.setImageResource(item.couponDetailsIcon)

            val isExpanded: Boolean = bindingAdapterPosition == mExpandedPosition

            val layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            binding.rvCouponDetailsContent.layoutManager = layoutManager

            binding.cvCouponRedeemRules.setOnClickListener {
                mExpandedPosition = if (isExpanded) -1 else bindingAdapterPosition
                setRecyclerData(binding, bindingAdapterPosition, isExpanded)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponDetailsTitleVH {
        val binding = ItemCouponDetailsTitleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CouponDetailsTitleVH(binding)
    }

    override fun onBindViewHolder(holder: CouponDetailsTitleVH, position: Int) {

        holder.bind(getItem(position))

    }

    private fun setRecyclerData(
        binding: ItemCouponDetailsTitleBinding,
        bindingAdapterPosition: Int,
        isExpanded: Boolean
    ) {
//        val animationCollapse: Animation =
//            AnimationUtils.loadAnimation(binding.root.context, R.anim.list_collapse)
//        val animationExpanded: Animation =
//            AnimationUtils.loadAnimation(binding.root.context, R.anim.list_expand)

        redeemArray.clear()
        newTncArray.clear()
        offerArray.clear()
        notifyDataSetChanged()

        if (bindingAdapterPosition == 0) {
            if (!isExpanded) {
                binding.rvCouponDetailsContent.toGone()
                for (i in arrayRedeem.indices)
                    redeemArray.add(CouponDetailsContentUiModel(arrayRedeem[i]))

                binding.rvCouponDetailsContent.toVisible()

//                binding.rvCouponDetailsContent.startAnimation(animationExpanded)

//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(90F)

                val adapter = CouponDetailsContentAdapter()
                adapter.submitList(redeemArray)
                binding.rvCouponDetailsContent.adapter = adapter

            }
            else{
                binding.rvCouponDetailsContent.toGone()
//                binding.cvCouponRedeemRules.startAnimation(animationCollapse)
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(0F)
            }
        }

        if (bindingAdapterPosition == 1) {
            if (!isExpanded) {
                binding.rvCouponDetailsContent.toGone()
                for (i in arrayOffer.indices)
                    offerArray.add(CouponDetailsContentUiModel(arrayOffer[i]))

                binding.rvCouponDetailsContent.toVisible()

//                binding.cvCouponRedeemRules.animation = animationExpanded
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(90f)

                val adapter = CouponDetailsContentAdapter()
                adapter.submitList(offerArray)
                binding.rvCouponDetailsContent.adapter = adapter
            }else{
                binding.rvCouponDetailsContent.toGone()
//                binding.cvCouponRedeemRules.animation = animationCollapse
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(0f)
            }
        }

        if (bindingAdapterPosition == 2) {
            if (!isExpanded) {
                binding.rvCouponDetailsContent.toGone()
                for (i in arrayTNC.indices)
                    newTncArray.add(CouponDetailsContentUiModel(arrayTNC[i]))

                binding.rvCouponDetailsContent.toVisible()

//                binding.cvCouponRedeemRules.animation = animationExpanded
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(90f)
                val adapter = CouponDetailsContentAdapter()
                adapter.submitList(newTncArray)
                binding.rvCouponDetailsContent.adapter = adapter
            }else{
                binding.rvCouponDetailsContent.toGone()
//                binding.cvCouponRedeemRules.animation = animationCollapse
//                binding.ivExpandRedeemRules.animate().setDuration(400).rotation(0f)
            }
        }
    }


}

object CouponDetailsTitleDiffUtils : DiffUtil.ItemCallback<CouponDetailsTitleUiModel>() {
    override fun areItemsTheSame(
        oldItem: CouponDetailsTitleUiModel,
        newItem: CouponDetailsTitleUiModel
    ): Boolean {
        return (oldItem.couponDetailsTitle == newItem.couponDetailsTitle)
    }

    override fun areContentsTheSame(
        oldItem: CouponDetailsTitleUiModel,
        newItem: CouponDetailsTitleUiModel
    ): Boolean {
        return ((oldItem.couponDetailsTitle == newItem.couponDetailsTitle))
    }

}

@Keep
data class CouponDetailsTitleUiModel(
    var couponDetailsTitle: String,
    var couponDetailsIcon: Int
) {
    companion object {
        fun fromCouponDetailsTitle(couponDetailsTitleModel: CouponDetailsTitleModel): CouponDetailsTitleUiModel {

            val couponDetailsTitle = couponDetailsTitleModel.couponDetailsTitle
            val couponDetailsIcon = couponDetailsTitleModel.couponDetailsIcon

            return CouponDetailsTitleUiModel(couponDetailsTitle, couponDetailsIcon)
        }
    }
}