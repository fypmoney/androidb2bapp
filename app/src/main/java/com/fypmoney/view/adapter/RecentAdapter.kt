package com.fypmoney.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.fypmoney.model.homemodel.RecentTransactionUser

class RecentAdapter {
}

private class PlantDiffCallback : DiffUtil.ItemCallback<RecentTransactionUser>() {

    override fun areItemsTheSame(oldItem: RecentTransactionUser, newItem: RecentTransactionUser): Boolean {
        return ((oldItem.userId == newItem.userId) && (oldItem.profilePicResourceId == oldItem.profilePicResourceId))
    }

    override fun areContentsTheSame(oldItem: RecentTransactionUser, newItem: RecentTransactionUser): Boolean {
        return oldItem == newItem
    }
}