package com.fypmoney.view.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.model.YourTaskModel
import com.fypmoney.view.fragment.AcceptRejectTaskFragment
import kotlinx.android.synthetic.main.card_your_task.view.*


internal class TaskHistoryStaggeredAdapter(private var listOfTasks: List<YourTaskModel>) :
    RecyclerView.Adapter<TaskHistoryStaggeredAdapter.MyViewHolder>(){
    var onItemClick: ((YourTaskModel) -> Unit)? = null
    internal inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var label: TextView = view.findViewById(R.id.label)
        var ttl: TextView = view.findViewById(R.id.ttl)
        var amount: TextView = view.findViewById(R.id.amount)
        var relation: TextView = view.findViewById(R.id.relation)
        var card_add: CardView = view.findViewById(R.id.card_add)
        var card_main: CardView = view.findViewById(R.id.card_main)

    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_task_history, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tasks = listOfTasks[position]
        holder.label.text = tasks.Title
        holder.ttl  .text = tasks.Title
        holder.amount.text = tasks.Amount
        holder.relation.text = tasks.Relation
        holder.itemView.setOnClickListener {
                onItemClick?.invoke(listOfTasks[position])
            }

        if(position==0){
            holder.card_add.visible()
            holder.card_main.invisible()
        }else{
            holder.card_add.invisible()
            holder.card_main.visible()
        }

    }
    override fun getItemCount(): Int {
        return listOfTasks.size
    }

    fun setMovieList(listOfTasks: List<YourTaskModel>) {
        this.listOfTasks = listOfTasks
        notifyDataSetChanged()
    }

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

}