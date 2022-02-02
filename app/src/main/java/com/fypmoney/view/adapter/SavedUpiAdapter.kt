package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.CardSavedUpiBinding
import com.fypmoney.extension.executeAfter

import com.fypmoney.util.Utility

class SavedUpiAdapter(
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: String) -> Unit
) : ListAdapter<String, SavedStringVH>(SavedUpiDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedStringVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardSavedUpiBinding.inflate(inflater, parent, false)
        return SavedStringVH(
            binding,
            lifecycleOwner,
            onRecentUserClick
        )

    }

    override fun onBindViewHolder(holder: SavedStringVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class SavedStringVH(
    private val binding: CardSavedUpiBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onRecentUserClick: (model: String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(user: String) {
        binding.executeAfter {
            lifecycleOwner = this@SavedStringVH.lifecycleOwner


            binding.layout.setOnClickListener {
                onRecentUserClick(user)
            }
            binding.upiId.text = user
        }
    }

}

object SavedUpiDiffUtils : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return (oldItem == newItem)
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}