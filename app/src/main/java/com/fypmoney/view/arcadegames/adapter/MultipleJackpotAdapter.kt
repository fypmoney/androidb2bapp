package com.fypmoney.view.arcadegames.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemMultipleJackpotsBinding
import com.fypmoney.view.arcadegames.model.MultipleJackpotResponse

class MultipleJackpotAdapter() :
    ListAdapter<MultipleJackpotResponse, MultipleJackpotVH>(MultipleJackpotDiffUtils) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleJackpotVH {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MultipleJackpotVH, position: Int) {
        TODO("Not yet implemented")
    }
}

class MultipleJackpotVH(private val binding: ItemMultipleJackpotsBinding) :
    RecyclerView.ViewHolder(binding.root) {

}

object MultipleJackpotDiffUtils : DiffUtil.ItemCallback<MultipleJackpotResponse>() {
    override fun areItemsTheSame(
        oldItem: MultipleJackpotResponse,
        newItem: MultipleJackpotResponse
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun areContentsTheSame(
        oldItem: MultipleJackpotResponse,
        newItem: MultipleJackpotResponse
    ): Boolean {
        TODO("Not yet implemented")
    }


}
