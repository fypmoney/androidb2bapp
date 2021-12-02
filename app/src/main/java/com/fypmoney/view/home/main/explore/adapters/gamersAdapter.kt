package com.fypmoney.view.home.main.explore.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse

import kotlin.collections.ArrayList


class gamersAdapter(val context: Context) : RecyclerView.Adapter<gamersAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_games, parent, false))

    }

    override fun getItemCount(): Int {

        return 10
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view


    }
}
