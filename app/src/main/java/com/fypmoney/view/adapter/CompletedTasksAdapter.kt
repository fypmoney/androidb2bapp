package com.fypmoney.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.model.HistoryModelResponse
import com.fypmoney.util.Utility
import com.fypmoney.view.interfaces.ListItemClickListener

import kotlinx.android.synthetic.main.card_task_history.view.*

import java.util.*


class CompletedTasksAdapter(
    val items: ArrayList<HistoryModelResponse>,
    val context: Context,
    val clickInterface: ListItemClickListener
) : RecyclerView.Adapter<CompletedTasksAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_task_history, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (items[position].historyTitle != null && items[position].historyTitle!!.isNotEmpty()) {
            holder.heading.text = items[position].historyTitle
        }
        if (items[position].description != null && items[position].description!!.isNotEmpty()) {
            holder.heading1.text = items[position].description
        }

        if (items[position].amount != null) {
            holder.invite.text = "₹ " + items[position].amount?.div(100)
        }
        holder.card.setOnClickListener(View.OnClickListener {
            clickInterface.onItemClicked(position)
        })
        if (items[position].historyIconResourceId != null) {
            Utility.setImageUsingGlide(
                url = items?.get(position)?.historyIconResourceId,
                imageView = holder.icon
            )

        }
        if (items[position].requesterName != null) {

            holder.givenby.text = items[position].requesterName.toString()
        }


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var heading = view.heading
        var invite = view.amount
        var heading1 = view.heading1
        var icon = view.ivServiceLogo

        var givenby = view.givenby

//


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
