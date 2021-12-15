package com.fypmoney.view.register.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.util.Utility
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import kotlinx.android.synthetic.main.personalized_offer_card.view.*

import java.util.*


class OffersAdapterAdapter(val items: ArrayList<offerDetailResponse>, val context: Context) :
    RecyclerView.Adapter<OffersAdapterAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.personalized_offer_card, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.offer_title.text = items[position].offerShortTitle
        holder.brandName.text = items[position].brandName
        Utility.setImageUsingGlideWithShimmerPlaceholder(
            imageView = holder.logo,
            url = items[position].brandLogo
        )

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var offer_title = view.tvServiceName
        var brandName = view.brand_name
        var logo = view.ivServiceLogo


    }
}
