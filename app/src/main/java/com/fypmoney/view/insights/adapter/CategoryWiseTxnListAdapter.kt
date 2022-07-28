package com.fypmoney.view.insights.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemTransactionCategoryWiseBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.util.Utility
import com.fypmoney.view.insights.model.CategoriesWiseTransactionUiModel

class CategoryWiseTxnListAdapter(private val lifecycleOwner: LifecycleOwner,
                                 private val onCategoryItemClick:(txnId:String?)->Unit):ListAdapter<CategoriesWiseTransactionUiModel, CategoryWiseTxnListAdapter.CategoryWiseTxnListVH>(CategoryWiseTxnItemDiffUtils) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryWiseTxnListVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTransactionCategoryWiseBinding.inflate(inflater,parent,false)
        return CategoryWiseTxnListVH(binding,lifecycleOwner,onCategoryItemClick)
    }

    override fun onBindViewHolder(holder: CategoryWiseTxnListVH, position: Int) {
        holder.bind(getItem(position))
    }


    class CategoryWiseTxnListVH(private val binding:ItemTransactionCategoryWiseBinding,
                                private val lifecycleOwner: LifecycleOwner,
                                private val onCategoryItemClick:(txnId:String?)->Unit
    ):RecyclerView.ViewHolder(binding.root){
        fun bind(item:CategoriesWiseTransactionUiModel){
            binding.executeAfter {
                lifecycleOwner = lifecycleOwner
                Utility.setImageUsingGlideWithShimmerPlaceholder(
                    url = item.categoryIconLink,
                    imageView = ivCategory
                )
                tvTransactionTitle.text = item.categoryTxnTitle
                tvTransactionDate.text = item.categoryTxnTime
                tvTransactionAmount.text = item.txnAmount
                cvTransactionItem.setOnClickListener {
                    onCategoryItemClick(item.txnRefNumber)
                }
            }
        }
    }
}

object CategoryWiseTxnItemDiffUtils:DiffUtil.ItemCallback<CategoriesWiseTransactionUiModel>(){
    override fun areItemsTheSame(
        oldItem: CategoriesWiseTransactionUiModel,
        newItem: CategoriesWiseTransactionUiModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CategoriesWiseTransactionUiModel,
        newItem: CategoriesWiseTransactionUiModel
    ): Boolean {
        return ((oldItem.categoryTxnTitle==newItem.categoryTxnTitle) && (oldItem.categoryTxnTime == newItem.categoryTxnTime) &&
                (oldItem.categoryIconLink==newItem.categoryIconLink) && (oldItem.txnAmount==newItem.txnAmount))
    }

}