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
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_assigned.view.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class SpinnerAdapter(
    val items: ArrayList<AssignedTaskResponse>,
    val context: Context,
    val clickInterface: ListItemClickListener
) : RecyclerView.Adapter<SpinnerAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_spin_item, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return 3
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.card.setOnClickListener(View.OnClickListener {
            clickInterface.onItemClicked(position)
        })

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
