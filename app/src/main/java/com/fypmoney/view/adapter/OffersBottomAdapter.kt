package com.fypmoney.view.adapter


import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_assigned.view.*
import kotlinx.android.synthetic.main.item_grid_type_offers.view.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class OffersBottomAdapter(
    val items: ArrayList<FeedDetails>,
    val context: Context,
    val clickInterface: ListContactClickListener
) : RecyclerView.Adapter<OffersBottomAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_grid_type_offers, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.card.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return@OnClickListener;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            clickInterface.onItemClicked(position)
        })
        Utility.setImageUsingGlide(url = items[position].resourceId, imageView = holder.image)

        holder.title_tv.text = items[position].title
        holder.desc.text = items[position].subTitle

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var image = view.offer_iv

        var title_tv = view.title_tv
        var desc = view.desc_tv

//


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
