package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemTopTenUserBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.model.homemodel.Users
import com.fypmoney.util.Utility

class TopTenUsersAdapter(
    private val lifecycleOwner: LifecycleOwner,
) : ListAdapter<Users, TopTenUsersVH>(TopTenUsersDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopTenUsersVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTopTenUserBinding.inflate(inflater, parent, false)
        return TopTenUsersVH(
            binding,
            lifecycleOwner
        )

    }

    override fun onBindViewHolder(holder: TopTenUsersVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class TopTenUsersVH(
    private val binding: ItemTopTenUserBinding,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: Users) {
        binding.executeAfter {
            lifecycleOwner = this@TopTenUsersVH.lifecycleOwner
            item = user
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