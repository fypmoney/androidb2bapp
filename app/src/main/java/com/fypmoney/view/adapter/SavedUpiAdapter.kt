package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.CardSavedUpiBinding
import com.fypmoney.extension.executeAfter

@Keep
data class SavedUpiUiModel(
    val upiId:String,
    var isSelected:Boolean = false
)
class SavedUpiAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onUpiClicked: (savedUpiUiModel: SavedUpiUiModel) -> Unit
) : ListAdapter<SavedUpiUiModel, SavedStringVH>(SavedUpiDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedStringVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardSavedUpiBinding.inflate(inflater, parent, false)
        return SavedStringVH(
            binding,
            lifecycleOwner,
            onUpiClicked
        )

    }

    override fun onBindViewHolder(holder: SavedStringVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class SavedStringVH(
    private val binding: CardSavedUpiBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onUpiClicked: (model: SavedUpiUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(savedUpiUiModel: SavedUpiUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@SavedStringVH.lifecycleOwner


            binding.upiIdCb.setOnClickListener {
                onUpiClicked(savedUpiUiModel)
            }
            binding.upiIdCb.isChecked = savedUpiUiModel.isSelected
            binding.upiIdCb.text = savedUpiUiModel.upiId
        }
    }

}

object SavedUpiDiffUtils : DiffUtil.ItemCallback<SavedUpiUiModel>() {

    override fun areItemsTheSame(oldItem: SavedUpiUiModel, newItem: SavedUpiUiModel): Boolean {
        return (oldItem == newItem)
    }

    override fun areContentsTheSame(oldItem: SavedUpiUiModel, newItem: SavedUpiUiModel): Boolean {
        return ((oldItem.upiId == newItem.upiId) && (oldItem.isSelected == newItem.isSelected))
    }
}