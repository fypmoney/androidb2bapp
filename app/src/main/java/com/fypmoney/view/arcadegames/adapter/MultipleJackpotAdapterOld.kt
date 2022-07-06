package com.fypmoney.view.arcadegames.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemMultipleJackpotsBinding
import com.fypmoney.view.arcadegames.model.MultipleJackpotResponse

class MultipleJackpotAdapterOld : RecyclerView.Adapter<MultipleJackpotViewHolder>() {

    var multipleJackpotList: ArrayList<MultipleJackpotResponse>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleJackpotViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MultipleJackpotViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


}

class MultipleJackpotViewHolder(private val binding: ItemMultipleJackpotsBinding) :
    RecyclerView.ViewHolder(binding.root) {

}
