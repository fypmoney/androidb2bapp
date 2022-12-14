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


class YourTasksAdapter(
    val items: ArrayList<AssignedTaskResponse>,
    val context: Context,
    val clickInterface: ListItemClickListener
) : RecyclerView.Adapter<YourTasksAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_assigned, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (items[position].requesterName != null && items[position].requesterName!!.isNotEmpty()) {
            holder.heading.text = items[position].requesterName
        }
        if (items[position].title != null && items[position].title!!.isNotEmpty()) {
            holder.title_task.text = items[position].title
        }
        if (items[position].displayState != null && items[position].displayState!!.isNotEmpty()) {
            holder.timeleft.text = items[position].displayState
        }
        if (items[position].amount != null) {
            holder.invite.text = "₹" + items[position].amount?.div(100)
        }
        holder.card.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
                return@OnClickListener;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (position % 4 == 0) {
                ChoresActivity.mViewModel?.selectedPosition?.value = 0
            } else if (position % 4 == 1) {
                ChoresActivity.mViewModel?.selectedPosition?.value = 1
            } else if (position % 4 == 2) {
                ChoresActivity.mViewModel?.selectedPosition?.value = 2
            } else if (position % 4 == 3) {
                ChoresActivity.mViewModel?.selectedPosition?.value = 3
            }
            clickInterface.onItemClicked(position)
        })
        if (position % 4 == 0) {

            holder.card_bg.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_blue
                )
            )
            holder.bg_text.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_blue
                )
            )

        } else if (position % 4 == 1) {

            holder.card_bg.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_pink
                )
            )
            holder.bg_text.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_pink
                )
            )

        } else if (position % 4 == 2) {

            holder.card_bg.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_green
                )
            )
            holder.bg_text.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_green
                )
            )

        } else if (position % 4 == 3) {

            holder.card_bg.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_orange
                )
            )
            holder.bg_text.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_orange
                )
            )

        }
//        if (items[position].isNewTask != null && items[position].isNewTask == "YES") {
//            holder.new_tv.visibility = View.VISIBLE
//        } else {
//            holder.new_tv.visibility = View.INVISIBLE
//        }
        holder.emoji.text = items[position].emojis

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var new_tv = view.new_tv
        var card_bg = view.card_bg
        var heading = view.heading
        var invite = view.amount
        var bg_text = view.bg_text
        var timeleft = view.timeleft
        var title_task = view.title_task
        var emoji = view.emoji
//


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
