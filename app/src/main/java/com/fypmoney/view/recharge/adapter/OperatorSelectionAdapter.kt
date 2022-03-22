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
import com.fypmoney.view.recharge.model.OperatorResponse

class OperatorSelectionAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: OperatorResponse) -> Unit
) : ListAdapter<OperatorResponse, TopTenUsersVH>(TopTenUsersDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTenUsersVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardOperatorBinding.inflate(inflater, parent, false)
        return TopTenUsersVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: TopTenUsersVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class TopTenUsersVH(
    private val binding: CardOperatorBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: OperatorResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: OperatorResponse) {
        binding.executeAfter {
            lifecycleOwner = this@TopTenUsersVH.lifecycleOwner

            loadImage(
                recentIv, user.icon,
                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img), false
            )

            recentUserCl.setOnClickListener {
                onRecentUserClick(user)
            }
            userNameTv.text = Utility.getFirstName(user.name)
        }
    }

}

object TopTenUsersDiffUtils : DiffUtil.ItemCallback<OperatorResponse>() {

    override fun areItemsTheSame(oldItem: OperatorResponse, newItem: OperatorResponse): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: OperatorResponse, newItem: OperatorResponse): Boolean {
        return oldItem == newItem
    }
}