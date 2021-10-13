package com.fypmoney.view.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

//        holder.coins_to_be_burn.text=items[position].appDisplayText
//        holder.desc.text=items[position].description

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view

        var burnAmount = view.burn_amount
        var desc = view.desc

//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
