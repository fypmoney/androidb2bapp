package com.fypmoney.view.home.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemCallToActionHomeBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.home.main.home.model.CallToActionUiModel
import kotlinx.android.synthetic.main.reward_offer_detail.*
import androidx.cardview.widget.CardView
import com.fypmoney.extension.toPx


class CallToActionAdapter (
    private val lifecycleOwner: LifecycleOwner,
    val onCallToActionClicked: (model: CallToActionUiModel) -> Unit
) : ListAdapter<CallToActionUiModel, CallToActionVH>(CallToActionDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallToActionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCallToActionHomeBinding.inflate(inflater, parent, false)
        return CallToActionVH(
            binding,
            lifecycleOwner,
            onCallToActionClicked
        )
    }

    override fun onBindViewHolder(holder: CallToActionVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class CallToActionVH(
    private val binding: ItemCallToActionHomeBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onCallToActionClicked: (model: CallToActionUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(callToAction: CallToActionUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@CallToActionVH.lifecycleOwner

            callToActionCv.setOnClickListener {
                onCallToActionClicked(callToAction)
            }
            val layoutParams = callToActionIv.layoutParams
            layoutParams.width = callToAction.contentX!!.toPx.toInt()
            layoutParams.height = callToAction.contentY!!.toPx.toInt()

            Utility.setImageUsingGlideWithShimmerPlaceholder(imageView = callToActionIv, url = callToAction.bannerImage)

        }
    }

}

object CallToActionDiffUtils : DiffUtil.ItemCallback<CallToActionUiModel>() {

    override fun areItemsTheSame(oldItem: CallToActionUiModel, newItem: CallToActionUiModel): Boolean {
        return ((oldItem.bannerImage == newItem.bannerImage) && (oldItem.bannerImage === oldItem.bannerImage))
    }

    override fun areContentsTheSame(oldItem: CallToActionUiModel, newItem: CallToActionUiModel): Boolean {
        return oldItem == newItem
    }
}