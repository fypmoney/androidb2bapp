package com.fypmoney.view.discord.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.CardRolesBinding
import com.fypmoney.extension.executeAfter

import com.fypmoney.util.Utility
import com.fypmoney.view.discord.model.RolesItem

class RolesAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: RolesItem) -> Unit
) : ListAdapter<RolesItem, RolesVH>(RolesDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardRolesBinding.inflate(inflater, parent, false)
        return RolesVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: RolesVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class RolesVH(
    private val binding: CardRolesBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: RolesItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: RolesItem) {
        binding.executeAfter {
            lifecycleOwner = this@RolesVH.lifecycleOwner
        try {
            dot.background.setTint(
                Color.parseColor(user.color)
            )
        }catch (e:Exception){

        }

            layoutRole.setOnClickListener {
                onRecentUserClick(user)
            }
            amountTv.text = Utility.getFirstName(user.name)
        }
    }

}

object RolesDiffUtils : DiffUtil.ItemCallback<RolesItem>() {

    override fun areItemsTheSame(oldItem: RolesItem, newItem: RolesItem): Boolean {
        return (oldItem == newItem)
    }

    override fun areContentsTheSame(oldItem: RolesItem, newItem: RolesItem): Boolean {
        return oldItem == newItem
    }
}