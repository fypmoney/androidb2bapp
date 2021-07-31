package com.fypmoney.view.adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.card_assigned.view.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AssignedTasksAdapter(val items: ArrayList<AssignedTaskResponse>, val context: Context, val clickInterface: ListItemClickListener) : RecyclerView.Adapter<AssignedTasksAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_assigned, parent, false))

    }

    override fun getItemCount(): Int {

        return items.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       
       if(items[position].title!=null && items[position].title!!.isNotEmpty()) {
    holder.heading.text = items[position].title
        }
        if(items[position].remainingTime!=null && items[position].remainingTime!!.isNotEmpty()) {
            holder.timeleft.text = items[position].remainingTime
        }
        if(items[position].amount!=null) {
            holder.invite.text ="₹ "+ items[position].amount.toString()
        }
//        holder.card.setOnClickListener(View.OnClickListener {
//            clickInterface.onCallClicked(position)
//        })



    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {



        var card=view
        var heading=view.heading
        var invite=view.amount
var timeleft=view.timeleft

//



//        init {
//            offer.z = context.resources.getDimension(R.dimen.list_item_elevation)
//        }

    }
}
