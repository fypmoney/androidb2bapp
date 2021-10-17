package com.fypmoney.view.adapter


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.model.aRewardProductResponse
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_scratch_item.view.*


import java.util.*


class ScratchItemAdapter(
    val items: ArrayList<aRewardProductResponse>,
    val context: Context,
    val clickInterface: ListContactClickListener
) : RecyclerView.Adapter<ScratchItemAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.card_scratch_item, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.card.setOnClickListener(View.OnClickListener {
            clickInterface.onItemClicked(position)
        })
        holder.burnAmount.setOnClickListener(View.OnClickListener {
            clickInterface.onItemClicked(position)
        })
        holder.desc.text = items[position].description
        Glide.with(context).load(items[position].listResource).into(holder.bg)
//        holder.coins_to_be_burn.text=items[position].appDisplayText
//        holder.desc.text=items[position].description

        if (items[position].backgroundColor != null) {
            holder.bg_card.background.setTint(
                Color.parseColor(items[position].backgroundColor)
            )
            holder.burnAmount.background.setTint(
                Color.parseColor(items[position].backgroundColor)

            )
        } else {
            holder.bg_card.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_task_card_blue
                )
            )


        }


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view

        var burnAmount = view.burn_amount
        var desc = view.desc
        var bg = view.image_illus
        var bg_card = view.bg_card

//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
