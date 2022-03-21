package com.fypmoney.view.recharge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.CardOperatorBinding
import com.fypmoney.extension.executeAfter

import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.model.CircleResponse

class CircleSelectionAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: CircleResponse) -> Unit
) : ListAdapter<CircleResponse, CircleSelectionVH>(CircleDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleSelectionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardOperatorBinding.inflate(inflater, parent, false)
        return CircleSelectionVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: CircleSelectionVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class CircleSelectionVH(
    private val binding: CardOperatorBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: CircleResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: CircleResponse) {
        binding.executeAfter {
            lifecycleOwner = this@CircleSelectionVH.lifecycleOwner

//            loadImage(recentIv,user.profilePicResourceId,
//                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img),true)

            recentUserCl.setOnClickListener {
                onRecentUserClick(user)
            }
            userNameTv.text = Utility.getFirstName(user.name)
        }
    }

}

object CircleDiffUtils : DiffUtil.ItemCallback<CircleResponse>() {

    override fun areItemsTheSame(oldItem: CircleResponse, newItem: CircleResponse): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: CircleResponse, newItem: CircleResponse): Boolean {
        return oldItem == newItem
    }
}