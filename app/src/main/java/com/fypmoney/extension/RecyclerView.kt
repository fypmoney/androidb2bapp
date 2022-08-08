package com.fypmoney.extension

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemOnlyTextBinding


inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

@Keep
data class SimpleTextUiModel(
    var txt:String,
    var txtColor:Int
)
class SimpleTextAdapter(
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<SimpleTextUiModel, SimpleTextVH>(SimpleTextDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTextVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOnlyTextBinding.inflate(inflater, parent, false)
        return SimpleTextVH(
            binding,
            lifecycleOwner)

    }

    override fun onBindViewHolder(holder: SimpleTextVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class SimpleTextVH(
    private val binding: ItemOnlyTextBinding,
    private val lifecycleOwner: LifecycleOwner) : RecyclerView.ViewHolder(binding.root) {
    fun bind(model: SimpleTextUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@SimpleTextVH.lifecycleOwner
            textTv.text = "•" + model.txt
            textTv.setTextColor(model.txtColor)
        }
    }

}

object SimpleTextDiffUtils : DiffUtil.ItemCallback<SimpleTextUiModel>() {

    override fun areItemsTheSame(oldItem: SimpleTextUiModel, newItem: SimpleTextUiModel): Boolean {
        return ((oldItem.txt == newItem.txt))
    }

    override fun areContentsTheSame(oldItem: SimpleTextUiModel, newItem: SimpleTextUiModel): Boolean {
        return oldItem == newItem
    }
}

