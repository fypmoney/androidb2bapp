package com.fypmoney.view.arcadegames.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.fypmoney.databinding.ItemRotatingTreasuresBinding

class TreasurePagerAdapter(var imagesList: MutableList<TreasureAdapterUiModel>, var pagerTreasures: ViewPager2) : RecyclerView.Adapter<TreasurePagerAdapter.TreasureViewHolder>() {

    var newTreasureImages: List<TreasureAdapterUiModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreasureViewHolder {
         val mRowBinding = ItemRotatingTreasuresBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return TreasureViewHolder(mRowBinding)
    }

    override fun onBindViewHolder(holder: TreasureViewHolder, position: Int) {
        return holder.onBind(imagesList[position])
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

     inner class TreasureViewHolder(private val binding: ItemRotatingTreasuresBinding? = null) : RecyclerView.ViewHolder(binding!!.root) {
        fun onBind(item:TreasureAdapterUiModel){
            binding?.lottieRotatingVP?.setAnimation(item.boxImage)

            if (absoluteAdapterPosition == imagesList.size - 2)
                pagerTreasures.post(runnable)
        }


    }

    val runnable = Runnable {
        imagesList.addAll(newTreasureImages)
        notifyDataSetChanged()
    }

}

data class TreasureAdapterUiModel(
    var boxImage:Int,
    var isSelected:Boolean = false
)

