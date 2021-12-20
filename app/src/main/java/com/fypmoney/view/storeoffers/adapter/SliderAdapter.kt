package com.fypmoney.view.storeoffers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.view.storeoffers.ListOfferClickListener
import com.fypmoney.view.storeoffers.adapter.SliderAdapter.SliderViewHolder
import com.fypmoney.view.storeoffers.model.offerDetailResponse

class SliderAdapter(
    sliderItems: ArrayList<offerDetailResponse>,
    viewPager2: ViewPager2,
    val itemClickListener2: ListOfferClickListener,
    val context: Context
) :
    RecyclerView.Adapter<SliderViewHolder>() {
    private val sliderItems: List<offerDetailResponse>
    private val viewPager2: ViewPager2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: kotlin.Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slide_item_container, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: kotlin.Int) {

        if (position != sliderItems.size - 1)
            holder.setImage(sliderItems[position].offerImage)
        else
            holder.imageView.setImageResource(R.drawable.view_more)
        holder.card.setOnClickListener {
            if (position != sliderItems.size - 1) {
                itemClickListener2.onItemClicked(sliderItems[position], "middle")
            } else {
                itemClickListener2.onItemClicked(sliderItems[position], "last")
            }

        }


        //        if (position == sliderItems.size - 2) {
//            viewPager2.post(runnable)
//        }
    }

    override fun getItemCount(): kotlin.Int {
        return sliderItems.size
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView
        val card: View

        init {
            card = itemView
            imageView = itemView.findViewById(R.id.offer_image)
        }

        fun setImage(sliderItems: String?) {
            Glide.with(context).load(sliderItems).placeholder(shimmerDrawable()).into(imageView)
        }


    }

    private val runnable = Runnable {
        sliderItems.addAll(sliderItems)
        notifyDataSetChanged()
    }

    init {
        this.sliderItems = sliderItems
        this.viewPager2 = viewPager2
    }
}