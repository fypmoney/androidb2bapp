package com.fypmoney.view.register.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.util.Utility
import com.fypmoney.view.storeoffers.ListOfferClickListener
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import kotlinx.android.synthetic.main.lightening_offer_home.view.*
import kotlinx.android.synthetic.main.personalized_offer_card.view.*

import java.util.*


class OffersHomeAdapter(
    val items: ArrayList<offerDetailResponse>,
    val context: Context,
    val itemClickListener2: ListOfferClickListener
) :
    RecyclerView.Adapter<OffersHomeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.lightening_offer_home, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.card.setOnClickListener(View.OnClickListener {
            itemClickListener2.onItemClicked(items[position], "")
        })

        Glide.with(context).load(items[position].offerImage).placeholder(shimmerDrawable())
            .into(holder.logo)

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var card = view

        var logo = view.offer_image


    }
}
