package com.fypmoney.view.discord.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.ItemTopTenUserBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.model.homemodel.Users
import com.fypmoney.util.Utility

class TopTenUsersAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: Users) -> Unit
) : ListAdapter<Users, RolesAdapter>(TopTenUsersDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesAdapter {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTopTenUserBinding.inflate(inflater, parent, false)
        return RolesAdapter(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: RolesAdapter, position: Int) {
        holder.bind(getItem(position))
    }

}

class RolesAdapter(
    private val binding: ItemTopTenUserBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: Users) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: Users) {
        binding.executeAfter {
            lifecycleOwner = this@RolesAdapter.lifecycleOwner

            loadImage(
                recentIv, user.profilePicResourceId,
                ContextCompat.getDrawable(this.recentIv.context, R.drawable.ic_profile_img), true
            )

            recentUserCl.setOnClickListener {
                onRecentUserClick(user)
            }
            userNameTv.text = Utility.getFirstName(user.name)
        }
    }

}

object TopTenUsersDiffUtils : DiffUtil.ItemCallback<Users>() {

    override fun areItemsTheSame(oldItem: Users, newItem: Users): Boolean {
        return ((oldItem.userId == newItem.userId) && (oldItem.profilePicResourceId === oldItem.profilePicResourceId))
    }

    override fun areContentsTheSame(oldItem: Users, newItem: Users): Boolean {
        return oldItem == newItem
    }
}