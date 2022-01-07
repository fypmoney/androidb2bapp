package com.fypmoney.view.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.UserProfileListRowItemBinding
import com.fypmoney.extension.executeAfter

@Keep
data class ListUiModel(
    var postion: Int,
    var name:String,
    var icon: Drawable?
)
class GlobalListAdapter (private val lifecycleOwner: LifecycleOwner,
val onListItemClicked: (model: ListUiModel) -> Unit
) : ListAdapter<ListUiModel, GlobalListVH>(GlobalListDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalListVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserProfileListRowItemBinding.inflate(inflater, parent, false)
        return GlobalListVH(
            binding,
            lifecycleOwner,
            onListItemClicked
        )
    }

    override fun onBindViewHolder(holder: GlobalListVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class GlobalListVH(
    private val binding: UserProfileListRowItemBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onListItemClicked: (model: ListUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(uiModel: ListUiModel) {
        binding.executeAfter {
            lifecycleOwner = this@GlobalListVH.lifecycleOwner
            image.setImageDrawable(uiModel.icon)
            titleTv.text = uiModel.name
            linear.setOnClickListener {
                onListItemClicked(uiModel)
            }
        }
    }

}

object GlobalListDiffUtils : DiffUtil.ItemCallback<ListUiModel>() {

    override fun areItemsTheSame(oldItem: ListUiModel, newItem: ListUiModel): Boolean {
        return ((oldItem.icon == newItem.icon) && (oldItem.name === oldItem.name))
    }

    override fun areContentsTheSame(
        oldItem: ListUiModel,
        newItem: ListUiModel
    ): Boolean {
        return oldItem == newItem
    }
}