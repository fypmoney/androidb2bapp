package com.fypmoney.view.home.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemQuickActionBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.view.home.main.home.model.QuickActionUiModel

class QuickActionAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onQuickActionClicked: (model: QuickActionUiModel) -> Unit
) : ListAdapter<QuickActionUiModel, QuickActionVH>(QuickActionDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickActionVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemQuickActionBinding.inflate(inflater, parent, false)
        return QuickActionVH(
            binding,
            lifecycleOwner,
            onQuickActionClicked
        )
    }

    override fun onBindViewHolder(holder: QuickActionVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class QuickActionVH(
    private val binding: ItemQuickActionBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onQuickActionClicked: (model: QuickActionUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(quickAction: QuickActionUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@QuickActionVH.lifecycleOwner

            quickActionAib.background = quickAction.image
            quickActionCl.setOnClickListener {
                onQuickActionClicked(quickAction)
            }
            quickActionNameTv.text = quickAction.name
        }
    }

}

object QuickActionDiffUtils : DiffUtil.ItemCallback<QuickActionUiModel>() {

    override fun areItemsTheSame(oldItem: QuickActionUiModel, newItem: QuickActionUiModel): Boolean {
        return ((oldItem.image == newItem.image) && (oldItem.name === oldItem.name))
    }

    override fun areContentsTheSame(oldItem: QuickActionUiModel, newItem: QuickActionUiModel): Boolean {
        return oldItem == newItem
    }
}