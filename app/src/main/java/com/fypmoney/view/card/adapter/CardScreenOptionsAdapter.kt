package com.fypmoney.view.card.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemCardOptionBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.view.card.model.CardOptionUiModel

class CardScreenOptionsAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onCardOptionClicked: (model: CardOptionUiModel) -> Unit
) : ListAdapter<CardOptionUiModel, CardScreenOptionsVH>(CardScreenOptionsDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardScreenOptionsVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCardOptionBinding.inflate(inflater, parent, false)
        return CardScreenOptionsVH(
            binding,
            lifecycleOwner,
            onCardOptionClicked
        )
    }

    override fun onBindViewHolder(holder: CardScreenOptionsVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class CardScreenOptionsVH(
    private val binding: ItemCardOptionBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onCardActionClicked: (model: CardOptionUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cardOptionUiModel: CardOptionUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@CardScreenOptionsVH.lifecycleOwner
            cardOptionIv.setImageDrawable(cardOptionUiModel.icon)
            cardOptionName.text = cardOptionUiModel.name
            cardOptionCv.setOnClickListener {
                onCardActionClicked(cardOptionUiModel)
            }
        }
    }

}

object CardScreenOptionsDiffUtils : DiffUtil.ItemCallback<CardOptionUiModel>() {

    override fun areItemsTheSame(oldItem: CardOptionUiModel, newItem: CardOptionUiModel): Boolean {
        return ((oldItem.icon == newItem.icon) && (oldItem.name === oldItem.name))
    }

    override fun areContentsTheSame(
        oldItem: CardOptionUiModel,
        newItem: CardOptionUiModel
    ): Boolean {
        return oldItem == newItem
    }
}