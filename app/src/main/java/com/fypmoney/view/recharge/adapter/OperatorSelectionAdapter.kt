package com.fypmoney.view.recharge.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.CardOperatorBinding
import com.fypmoney.extension.executeAfter
import com.fypmoney.extension.toInvisible
import com.fypmoney.view.recharge.model.OperatorResponse

class OperatorSelectionAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val onOperatorClick: (model: OperatorResponse) -> Unit
) : ListAdapter<OperatorResponse, OperatorVH>(OperatorDiffUtils) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperatorVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardOperatorBinding.inflate(inflater, parent, false)
        return OperatorVH(
            binding,
            lifecycleOwner,
            onOperatorClick
        )

    }

    override fun onBindViewHolder(holder: OperatorVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class OperatorVH(
    private val binding: CardOperatorBinding,
    private val lifecycleOwner: LifecycleOwner,
    val onOperatorClick: (model: OperatorResponse) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(operator: OperatorResponse) {
        binding.executeAfter {
            lifecycleOwner = this@OperatorVH.lifecycleOwner
            operatorName.text = operator.name
            when (operator.name) {
                "Airtel" -> {
                    loadImage(
                        operatorIv, operator.icon,
                        ContextCompat.getDrawable(this.operatorIv.context, R.drawable.ic_airtel), false
                    )
                }
                "Vodafone" -> {
                    loadImage(
                        operatorIv, operator.icon,
                        ContextCompat.getDrawable(this.operatorIv.context, R.drawable.ic_vodafone), false
                    )
                }
                "JIO" -> {
                    loadImage(
                        operatorIv, operator.icon,
                        ContextCompat.getDrawable(this.operatorIv.context, R.drawable.ic_jio), false
                    )
                }

                else -> {
                    operatorIv.toInvisible()
                    operatorName.setTextColor(operatorName.resources.getColor(R.color.reward_continue_button))
                }
            }

            operatorCl.setOnClickListener {
                onOperatorClick(operator)
            }
        }
    }

}

object OperatorDiffUtils : DiffUtil.ItemCallback<OperatorResponse>() {

    override fun areItemsTheSame(oldItem: OperatorResponse, newItem: OperatorResponse): Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: OperatorResponse, newItem: OperatorResponse): Boolean {
        return oldItem == newItem
    }
}