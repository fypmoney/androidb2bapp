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
import com.fypmoney.databinding.ItemMonthsBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.view.insights.viewmodel.MonthItem

class MonthListAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onMonthClick:(position:Int)->Unit):ListAdapter<MonthItem, MonthListAdapter.MonthListVH>(MonthListItemDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthListVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMonthsBinding.inflate(inflater,parent,false)
        return MonthListVH(binding,lifecycleOwner,onMonthClick)
    }

    override fun onBindViewHolder(holder: MonthListVH, position: Int) {
        holder.onBind(getItem(position))
    }

    class MonthListVH(
        private val binding: ItemMonthsBinding,
        private val lifecycleOwner: LifecycleOwner,
        private val onMonthClick:(position:Int)->Unit
    ):RecyclerView.ViewHolder(binding.root){
        fun onBind(item: MonthItem){
            binding.executeAfter {
                lifecycleOwner = lifecycleOwner
                tvMonth.text = item.monthName
                if(item.isSelected){
                    setBackgroundDrawable(clMonths,
                        ContextCompat.getColor(clMonths.context, R.color.color_orange),
                        14.0f,false,
                    )
                }else{
                    setBackgroundDrawable(clMonths,
                        ContextCompat.getColor(clMonths.context, R.color.black12),
                        14.0f,false,
                    )
                }
                clMonths.setOnClickListener {
                    setBackgroundDrawable(clMonths,
                        ContextCompat.getColor(clMonths.context, R.color.color_orange),
                        14.0f,false,
                    )
                    onMonthClick(item.position)
                }
            }
        }
    }
}

object MonthListItemDiffUtils:DiffUtil.ItemCallback<MonthItem>() {
    override fun areItemsTheSame(oldItem: MonthItem, newItem: MonthItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MonthItem, newItem: MonthItem): Boolean {
        return ((oldItem.monthName==newItem.monthName) && (oldItem.position == newItem.position) && (oldItem.isSelected==newItem.isSelected))
    }
}