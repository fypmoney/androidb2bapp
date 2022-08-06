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
import com.fypmoney.bindingAdapters.shimmerColorDrawable
import com.fypmoney.extension.setMargin
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.Utility
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

        if(items[position].bgHeight!=null && items[position].bgWidth!=null && items[position].backgroundImage!=null){
            val set = ConstraintSet()
            set.clone(holder.rlBase)
            val ratio =
                items[position].bgWidth + ":" + items[position].bgHeight
            set.setDimensionRatio(holder.ivBackgroundImage.id, ratio)
            set.applyTo(holder.rlBase)

            Glide.with(context).load(items[position].backgroundImage)
                .into(holder.ivBackgroundImage)
        }

        //holder.date_tv.setTextColor(titleTextColor)
        if (items[position].sectionDisplayText.isNullOrEmpty()) {
            holder.date_tv.visibility = View.GONE
        } else {
            holder.date_tv.text = items[position].sectionDisplayText
            items[position].titleColor?.let {
                holder.date_tv.setTextColor(Color.parseColor(it))
            }?: kotlin.run { holder.date_tv.setTextColor(titleTextColor) }
        }
        if(items[position].sectionSubTitle.isNullOrEmpty()){
            holder.tvSubTitle.toGone()
        }else{
            holder.tvSubTitle.toVisible()
            holder.tvSubTitle.text = items[position].sectionSubTitle
            items[position].subTitleColor?.let {
                holder.tvSubTitle.setTextColor(Color.parseColor(it))
            }?: kotlin.run { holder.tvSubTitle.setTextColor(titleTextColor) }
        }
        items[position].topSpacing?.let {
            holder.rlBase.setMargin(left = 0.0f, top = it.toFloatOrNull()?:0.0f, right = 0.0f, bottom = 0.0f)
        }
        if (items[position].sectionContent != null && items[position].sectionContent?.size!! > 1) {
            val itemClickListener2 = object : ExploreItemClickListener {
                override fun onItemClicked(position: Int, sectionContentItem: SectionContentItem,exploreContentResponse: ExploreContentResponse?) {
                    Utility.hapticVibrate(context)
                    clickInterface.onItemClicked(position, sectionContentItem,exploreContentResponse)
                }
            }
            holder.card.visibility = View.GONE
            holder.rv_list.visibility = View.VISIBLE
            val layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.rv_list.layoutManager = layoutManager
            val arrayList: ArrayList<SectionContentItem> = ArrayList()

            items[position].sectionContent?.forEach { section ->
                if (section != null) {
                    arrayList.add(section)
                }
            }
            val typeAdapter =
                ExploreAdapter(itemClickListener2, arrayList, context, scale,items[position])
            holder.rv_list.adapter = typeAdapter
            items[position].scrolDisplay?.let {
                if(it==YES){
                    items[position].scrollColor?.let { color->
                        holder.indicator.dotColor = Color.parseColor(color)
                    }
                    holder.indicator.attachToRecyclerView(holder.rv_list)
                }else{
                    holder.indicator.toGone()
                }
            }?: kotlin.run { holder.indicator.toGone() }
        }
        else {
            holder.rv_list.visibility = View.GONE
            holder.card.visibility = View.VISIBLE
            val set = ConstraintSet()
            set.clone(holder.contraint)
            val ratio =
                items[position].sectionContent?.get(0)?.contentDimensionX.toString() + ":" + items[position].sectionContent?.get(
                    0
                )?.contentDimensionY
            set.setDimensionRatio(holder.card.id, ratio)
            set.applyTo(holder.contraint)
            Glide.with(context).load(items[position].sectionContent?.get(0)?.contentResourceUri)
                .placeholder(
                    shimmerColorDrawable()
                )
                .into(holder.baseImage)


            holder.viewItem.setOnClickListener {

                items[position].sectionContent?.get(0)?.let { it1 ->
                    Utility.hapticVibrate(context)
                    clickInterface.onItemClicked(
                        position,
                        it1,
                        items[position]
                    )
                }

            }
        }


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var rv_list = view.rv_base
        var rlBase = view.rlBase
        var indicator = view.indicator
        var date_tv = view.date_tv
        var tvSubTitle = view.tvSubTitle
        var contraint = view.contraint
        var card = view.main_cv
        var viewItem = view

        var baseImage = view.base_image
        var ivBackgroundImage = view.ivBackgroundImage
    }
}


