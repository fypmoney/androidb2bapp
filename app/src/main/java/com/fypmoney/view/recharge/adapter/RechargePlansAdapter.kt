package com.fypmoney.view.recharge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.databinding.CardRechargePlanBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.view.recharge.model.ValueItem

class RechargePlansAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: ValueItem) -> Unit
) : ListAdapter<ValueItem, RechargePlansVH>(PlansDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RechargePlansVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardRechargePlanBinding.inflate(inflater, parent, false)
        return RechargePlansVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: RechargePlansVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class RechargePlansVH(
    private val binding: CardRechargePlanBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: ValueItem) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: ValueItem) {
        binding.executeAfter {
            lifecycleOwner = this@RechargePlansVH.lifecycleOwner
            card.setOnClickListener {
                onRecentUserClick(user)
            }
            tvRs.text = tvRs.context.resources.getString(R.string.Rs)+" "+user.rs
            tvDetails.text = user.desc
            tvValidity.text = if (user.validity=="N/A") "Not Applicable" else  user.validity
        }
    }

}

object PlansDiffUtils : DiffUtil.ItemCallback<ValueItem>() {

    override fun areItemsTheSame(oldItem: ValueItem, newItem: ValueItem): Boolean {
        return (oldItem.rs == newItem.rs)
    }

    override fun areContentsTheSame(oldItem: ValueItem, newItem: ValueItem): Boolean {
        return oldItem == newItem
    }
}