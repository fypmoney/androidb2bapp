package com.fypmoney.view.adapter


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R

import com.fypmoney.view.interfaces.ListContactClickListener
import kotlinx.android.synthetic.main.card_assigned.view.*
import kotlinx.android.synthetic.main.item_banner_type.view.*
import kotlinx.android.synthetic.main.item_grid_type_offers.view.*
import kotlinx.android.synthetic.main.item_offer_detail.view.*

import java.util.*


class offerpointsAdapter(
    val items: ArrayList<String>,
    val context: Context,
    val clickInterface: ListContactClickListener,
    val white: Int? = Color.BLACK,
    val textcolor: Int? = ContextCompat.getColor(context, R.color.text_color_faded)
) : RecyclerView.Adapter<offerpointsAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_offer_detail, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (white != null) {
            holder.dot.background.setTint(white)
        }
        textcolor?.let { holder.detail.setTextColor(it) }

        holder.card.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return@OnClickListener;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            clickInterface.onItemClicked(position)
        })


//        Utility.setImageUsingGlide(url = items[position].resourceId, imageView = holder.image)
        holder.detail.text = items[position]


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var dot = view.dot
        var detail = view.detail

//


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
