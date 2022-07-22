package com.fypmoney.view.arcadegames.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.databinding.ItemRotatingTreasuresBinding

class TreasurePagerAdapter(
) : RecyclerView.Adapter<TreasurePagerAdapter.TreasureViewHolder>() {

    private val treasureImages = mutableListOf<TreasureAdapterUiModel>()
//    lateinit var selected:ItemRotatingTreasuresBinding

//    private val sliderRunnable = Runnable {
//        rotatingTreasureVM.listAddedCount += 1
//        treasureImages.clear()
//        treasureImages.addAll(treasureImages)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreasureViewHolder {
         val mRowBinding = ItemRotatingTreasuresBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return TreasureViewHolder(mRowBinding)
    }

    override fun onBindViewHolder(holder: TreasureViewHolder, position: Int) {
        return holder.onBind(treasureImages[position])
    }

    override fun getItemCount(): Int {
        return treasureImages.size
    }

     inner class TreasureViewHolder(private val binding: ItemRotatingTreasuresBinding? = null) : RecyclerView.ViewHolder(binding!!.root) {
        fun onBind(item:TreasureAdapterUiModel){
            binding?.lottieRotatingVP?.setAnimation(item.boxImage)
//            if(item.isSelected){
//                selected = binding!!
//                binding.lottieRotatingVP.playAnimation()
//            }

//            if (absoluteAdapterPosition == treasureImages.size - 1) {
//                rotatingTreasureVP.post(sliderRunnable)
//            }
        }


    }


    /**
     * This will set the data in the list in adapter
     */
    fun setList(treasureList: List<TreasureAdapterUiModel>) {
        for (item in treasureList){
            treasureImages.add(item)
        }
        notifyDataSetChanged()
    }

    fun clearList(){
        treasureImages.clear()
        notifyDataSetChanged()
    }

}

data class TreasureAdapterUiModel(
    var boxImage:Int,
    var isSelected:Boolean = false
)

