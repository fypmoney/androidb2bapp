package com.fypmoney.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.interfaces.ListItemClickListener

import java.util.*


class RewardsHistoryLeaderboardAdapter(
    val items: ArrayList<AssignedTaskResponse>,
    val context: Context,
    val clickInterface: ListItemClickListener
) : RecyclerView.Adapter<RewardsHistoryLeaderboardAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.reward_history_item_leaderboard, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return 10
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
