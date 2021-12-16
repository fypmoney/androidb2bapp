package com.fypmoney.view.home.main.explore.adapters


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.bindingAdapters.shimmerDrawable
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import kotlinx.android.synthetic.main.reward_history_base.view.*


class ExploreBaseAdapter(
    val items: ArrayList<ExploreContentResponse>,
    val context: Context,
    val clickInterface: ExploreItemClickListener,
    val scale: Float,
    val titleTextColor: Int = Color.WHITE
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
        holder.date_tv.setTextColor(titleTextColor)
        holder.date_tv.text = items[position].sectionDisplayText
        if (items[position].sectionContent != null && items[position].sectionContent?.size!! > 1) {
            var itemClickListener2 = object : ExploreItemClickListener {
                override fun onItemClicked(position: Int, sectionContentItem: SectionContentItem) {
                    clickInterface.onItemClicked(position, sectionContentItem)

                }
            }
            holder.card.visibility = View.GONE
            holder.rv_list.visibility = View.VISIBLE
            val layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.rv_list.layoutManager = layoutManager

            var arrayList: ArrayList<SectionContentItem> = ArrayList()

            items[position].sectionContent?.forEach { section ->
                if (section != null) {
                    arrayList.add(section)
                }
            }


            var typeAdapter =
                ExploreAdapter(itemClickListener2, arrayList, context, scale)
            holder.rv_list.adapter = typeAdapter
        } else {
            holder.rv_list.visibility = View.GONE
            holder.card.visibility = View.VISIBLE
            val set = ConstraintSet()
            set.clone(holder.contraint)
            var ratio =
                items[position].sectionContent?.get(0)?.contentDimensionX.toString() + ":" + items[position].sectionContent?.get(
                    0
                )?.contentDimensionY
            set.setDimensionRatio(holder.card.id, ratio)
            set.applyTo(holder.contraint)
            Glide.with(context).load(items[position].sectionContent?.get(0)?.contentResourceUri)
                .placeholder(
                    shimmerDrawable()
                )
                .into(holder.baseImage)


            holder.viewItem.setOnClickListener(View.OnClickListener {

                items[position].sectionContent?.get(0)?.let { it1 ->
                    clickInterface.onItemClicked(
                        position,
                        it1
                    )
                }

            })

//            val params: ViewGroup.LayoutParams =
//                holder.baseImage.layoutParams as ViewGroup.LayoutParams
//
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT
//            params.height = (scale * items[position].sectionContent?.get(0)?.contentDimensionY!!).toInt()
//            holder.baseImage.layoutParams = params

        }


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var rv_list = view.rv_base

        var date_tv = view.date_tv
        var contraint = view.contraint
        var card = view.main_cv
        var viewItem = view

        var baseImage = view.base_image


    }
}
