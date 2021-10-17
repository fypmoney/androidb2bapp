package com.fypmoney.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.model.aRewardProductResponse
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_spin_item.view.*

import java.util.*


class SpinnerAdapter(
    val items: ArrayList<aRewardProductResponse>,
    val context: Context,
    val clickInterface: ListContactClickListener
) : RecyclerView.Adapter<SpinnerAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_spin_item, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.card.setOnClickListener(View.OnClickListener {
            clickInterface.onItemClicked(position)
        })

        holder.coins_to_be_burn.text = items[position].appDisplayText
        holder.desc.text = items[position].description

        Glide.with(context).load(items[position].listResource).into(holder.bg)

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view

        var coins_to_be_burn = view.coins_to_be_burn
        var desc = view.desc

        var bg = view.image_illus

//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
