package com.fypmoney.view.insights.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ItemMerchantCategoryBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.insights.model.MerchantCategoryUiModel

class MerchantCategoryListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onCategoryClick:(categoryCode:String)->Unit):
    ListAdapter<MerchantCategoryUiModel, MerchantCategoryListAdapter.MerchantCategoryListVH>(MerchantCategoryListItemDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantCategoryListVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMerchantCategoryBinding.inflate(inflater,parent,false)
        return MerchantCategoryListVH(binding,lifecycleOwner,onCategoryClick)
    }

    override fun onBindViewHolder(holder: MerchantCategoryListVH, position: Int) {
        holder.onBind(getItem(position))
    }

    class MerchantCategoryListVH(
        private val binding: ItemMerchantCategoryBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val onCategoryClick: (categoryCode: String) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        fun onBind(item: MerchantCategoryUiModel){
            binding.executeAfter {
                lifecycleOwner = lifecycleOwner
                tvCategoryName.text = item.categoryName
                Utility.setImageUsingGlideWithShimmerPlaceholder(
                    url = item.categoryIcon,
                    imageView = binding.ivSelectedCategory
                )
                if(item.isSelected){
                    setBackgroundDrawable(clCategory,
                        ContextCompat.getColor(clCategory.context, R.color.orange1),
                        31.0f,false,
                    )
                    tvCategoryName.setTextColor(ContextCompat.getColor(clCategory.context, R.color.black10))
                }else{
                    setBackgroundDrawable(clCategory,
                        ContextCompat.getColor(clCategory.context, R.color.black13),
                        31.0f,false,
                    )
                    tvCategoryName.setTextColor(ContextCompat.getColor(clCategory.context, R.color.white))
                }
                clCategory.setOnClickListener {
                    setBackgroundDrawable(clCategory,
                        ContextCompat.getColor(clCategory.context, R.color.orange1),
                        31.0f,false,
                    )
                    onCategoryClick(item.categoryCode)
                }
            }
        }
    }
}

object MerchantCategoryListItemDiffUtils: DiffUtil.ItemCallback<MerchantCategoryUiModel>() {
    override fun areItemsTheSame(oldItem: MerchantCategoryUiModel, newItem: MerchantCategoryUiModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MerchantCategoryUiModel, newItem: MerchantCategoryUiModel): Boolean {
        return ((oldItem.categoryIcon==newItem.categoryIcon) && (oldItem.categoryName == newItem.categoryName))
    }
}