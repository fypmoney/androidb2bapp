package com.fypmoney.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.RewardHistoryResponse
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.reward_history_item_leaderboard.view.*

import java.util.*


class RewardsHistoryLeaderboardAdapter(
    val items: ArrayList<RewardHistoryResponse>,
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

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.numberofMynts.text = items[position].loyaltyPointsBurnt.toString() + " Mynts"
        holder.desc.text = items[position].descriptionl

        if (items[position].cashbackWon != null && items[position].cashbackWon!! > 0) {
            holder.amount.visibility = View.VISIBLE
            holder.won_tv.visibility = View.VISIBLE


        } else {
            holder.amount.visibility = View.VISIBLE
            holder.won_tv.visibility = View.VISIBLE
        }

        holder.amount.text = items[position].cashbackWon.toString()


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var desc = view.desc
        var won_tv = view.won_tv
        var amount = view.amount
        var numberofMynts = view.heading


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
