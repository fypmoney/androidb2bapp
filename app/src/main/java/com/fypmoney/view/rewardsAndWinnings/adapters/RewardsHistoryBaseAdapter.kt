package com.fypmoney.view.rewardsAndWinnings.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.model.HistoryItem
import com.fypmoney.model.RewardHistoryResponseNew
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener
import kotlinx.android.synthetic.main.reward_history_base.view.*
import kotlinx.android.synthetic.main.reward_history_item_leaderboard.view.*

import java.util.*


class RewardsHistoryBaseAdapter(
    val items: ArrayList<RewardHistoryResponseNew>,
    val context: Context,
    val clickInterface: ListRewardsItemClickListener
) : RecyclerView.Adapter<RewardsHistoryBaseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.reward_history_base, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.rv_list.layoutManager = mLayoutManager
        holder.rv_list.itemAnimator = DefaultItemAnimator()

        var itemClickListener2 = object : ListRewardsItemClickListener {

            override fun onItemClicked(historyItem: HistoryItem) {
                clickInterface.onItemClicked(historyItem)

            }
        }
        var arrayList: ArrayList<HistoryItem> = ArrayList()
        items[position].history?.forEach { item ->
            if (item != null) {
                arrayList.add(item)
            }
        }
        holder.date_tv.text = items[position].date


        var adapter =
            RewardsHistoryLeaderboardAdapter(arrayList, context, itemClickListener2)
        holder.rv_list.adapter = adapter

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var rv_list = view.rv_base

        var date_tv = view.date_tv


    }
}
