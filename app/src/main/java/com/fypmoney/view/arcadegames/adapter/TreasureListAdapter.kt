package com.fypmoney.view.arcadegames.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemRotatingTreasuresBinding
import com.fypmoney.extension.executeAfter

class TreasureListAdapter(
    private val lifecycleOwner: LifecycleOwner) : ListAdapter<TreasureAdapterUiModel, TreasureListAdapterVH>(TreasureListDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreasureListAdapterVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRotatingTreasuresBinding.inflate(inflater, parent, false)
        return TreasureListAdapterVH(
            binding,
            lifecycleOwner,
        )

    }

    override fun onBindViewHolder(holder: TreasureListAdapterVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class TreasureListAdapterVH(
    private val binding: ItemRotatingTreasuresBinding,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: TreasureAdapterUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@TreasureListAdapterVH.lifecycleOwner
        }
        binding.lottieRotatingVP.setAnimation(model.boxImage)
    }

}

object TreasureListDiffUtils : DiffUtil.ItemCallback<TreasureAdapterUiModel>() {

    override fun areItemsTheSame(oldItem: TreasureAdapterUiModel, newItem: TreasureAdapterUiModel): Boolean {
        return ((oldItem.isSelected == newItem.isSelected))
    }

    override fun areContentsTheSame(oldItem: TreasureAdapterUiModel, newItem: TreasureAdapterUiModel): Boolean {
        return oldItem == newItem
    }
}