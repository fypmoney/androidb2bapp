package com.fypmoney.view.adapter


import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fypmoney.R
import com.fypmoney.model.RelationModel
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.AddMemberViewModel

import kotlinx.android.synthetic.main.relation_row_item.view.*


import java.util.*


class RelationSelectAdapter(
    val items: ArrayList<RelationModel>,
    val context: Context,

    var mViewModel: AddMemberViewModel
) : RecyclerView.Adapter<RelationSelectAdapter.ViewHolder>() {


    private var selectedPos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.relation_row_item, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        holder.heading.text=items[position].relationName
//
//        Glide.with(context).load(items[position].relationImage).into(holder.image)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            holder.image.setImageResource(items[position].relationImage!!);
//        } else {
//            holder.image.setImageResource(items[position].relationImage!!);
//        }
//        if (selectedPos == position) {
//
//          holder.image .background =
//                context?.let { ContextCompat.getDrawable(it, R.drawable.curved_background16) }
//
//        } else {
//            holder.image.background =
//                context?.let { ContextCompat.getDrawable(it, R.drawable.curved_background14) }
//
//        }
//        holder.card.setOnClickListener(View.OnClickListener {
//            selectedPos = holder.absoluteAdapterPosition
//
//            mViewModel.selectedRelationList.value= items?.get(position)
//
//            notifyDataSetChanged()
//        })
//
//

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var image = view.ivServiceLogo
        var heading = view.heading

//


//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
