package com.fypmoney.view.home.main.explore.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM
import kotlinx.android.synthetic.main.reward_history_base.view.*

import kotlin.collections.ArrayList


class ExploreBaseAdapter(
    val items: ArrayList<ExploreContentResponse>,
    val context: Context,
    val clickInterface: ExploreItemClickListener,
    val exploreFragmentVM: ExploreFragmentVM,
    val scale: Float
) : RecyclerView.Adapter<ExploreBaseAdapter.ViewHolder>() {


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


        holder.rv_list.itemAnimator = DefaultItemAnimator()

        var itemClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(position: Int, sectionContentItem: SectionContentItem) {
                clickInterface.onItemClicked(position, sectionContentItem)

            }
        }

        val layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.rv_list.layoutManager = layoutManager
        holder.date_tv.text = items[position].sectionDisplayText
        var arrayList: ArrayList<SectionContentItem> = ArrayList()

        items[position].sectionContent?.forEach { section ->
            if (section != null) {
                arrayList.add(section)
            }
        }

//        if(items[position].showMore==AppConstants.YES){
//            var sectionItem = SectionContentItem()
//         sectionItem.contentDimensionX=80
//            sectionItem.contentDimensionY=100
//
//            sectionItem.showmore="SHOWMORE"
//            sectionItem.showmoreURI=items[position].showMoreRedirectionResource
//            arrayList.add(sectionItem)
//        }

        var typeAdapter =
            ExploreAdapter(exploreFragmentVM, itemClickListener2, arrayList, context, scale)
        holder.rv_list.adapter = typeAdapter
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var rv_list = view.rv_base

        var date_tv = view.date_tv


    }
}
