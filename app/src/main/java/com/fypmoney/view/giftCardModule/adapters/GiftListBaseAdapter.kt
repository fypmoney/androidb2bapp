package com.fypmoney.view.giftCardModule.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.view.giftCardModule.GiftCardAdapter
import com.fypmoney.view.giftCardModule.model.GiftSearchResponse
import com.fypmoney.view.giftCardModule.model.VoucherBrandItem
import kotlinx.android.synthetic.main.gift_section_base.view.*
import kotlinx.android.synthetic.main.reward_history_base.view.*
import kotlinx.android.synthetic.main.reward_history_base.view.date_tv
import kotlinx.android.synthetic.main.reward_history_base.view.rv_base
import kotlinx.android.synthetic.main.reward_history_item_leaderboard.view.*

import java.util.*


class GiftListBaseAdapter(
    val items: List<GiftSearchResponse>,
    val contextR: Context,
    val onRecentUserClick: (model: VoucherBrandItem) -> Unit,

    private val lifecycleOwner: LifecycleOwner,
    val viewAllClicked: (model: GiftSearchResponse) -> Unit
    ) : RecyclerView.Adapter<GiftListBaseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(contextR)
                .inflate(R.layout.gift_section_base, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var mLayoutManager = LinearLayoutManager(contextR, LinearLayoutManager.VERTICAL, false)
        holder.rv_list.layoutManager = mLayoutManager
        holder.rv_list.itemAnimator = DefaultItemAnimator()


        var arrayList: ArrayList<VoucherBrandItem> = ArrayList()
        items[position].voucherBrand?.forEachIndexed { pos, item ->
            if (item != null && pos < 2) {
                arrayList.add(item)
            }
        }
        holder.date_tv.text = items[position].name


        val giftCardAdapter = GiftCardAdapter(
            lifecycleOwner, onRecentUserClick = {
                onRecentUserClick(it)
            }
        )
        holder.view_all.setOnClickListener {
            viewAllClicked(items[position])

        }

        with(holder.rv_list!!) {
            adapter = giftCardAdapter
            layoutManager = GridLayoutManager(
                context,
                2
            )
        }

        (holder.rv_list?.adapter as GiftCardAdapter).run {
            submitList(arrayList)
        }

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var rv_list = view.rv_base

        var date_tv = view.date_tv

        var view_all = view.view_all
    }
}
