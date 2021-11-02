package com.fypmoney.view.giftCardModule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemGiftCardBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.model.FeedDetails


class GiftCardAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: FeedDetails) -> Unit
) : ListAdapter<FeedDetails, GiftCardVH>(GiftCardDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftCardVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGiftCardBinding.inflate(inflater, parent, false)
        return GiftCardVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: GiftCardVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class GiftCardVH(
    private val binding: ItemGiftCardBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: FeedDetails) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: FeedDetails) {
        binding.executeAfter {
            lifecycleOwner = this@GiftCardVH.lifecycleOwner
            var item = user
//            loadImage(recentIv,user.profilePicResourceId,
//                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img),true)
//
            binding.buygift.setOnClickListener {
                onRecentUserClick(user)
            }
//            userNameTv.text = Utility.getFirstName(user.name)
        }
    }

}

object GiftCardDiffUtils : DiffUtil.ItemCallback<FeedDetails>() {

    override fun areItemsTheSame(oldItem: FeedDetails, newItem: FeedDetails): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: FeedDetails, newItem: FeedDetails): Boolean {
        return oldItem == newItem
    }
}