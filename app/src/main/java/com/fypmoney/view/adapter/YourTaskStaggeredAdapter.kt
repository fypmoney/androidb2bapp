package com.fypmoney.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R


class YourTaskStaggeredAdapter( private var listOfTasks: List<com.fypmoney.model.yourTaskModal.Data>) :
    RecyclerView.Adapter<YourTaskStaggeredAdapter.MyViewHolder>(){
    var onItemClick: ((com.fypmoney.model.yourTaskModal.Data) -> Unit)? = null
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var label: TextView = view.findViewById(R.id.label)
        var amount: TextView = view.findViewById(R.id.amount)
        var relation: TextView = view.findViewById(R.id.relation)
        var new_ll: LinearLayout = view.findViewById(R.id.new_ll)
        var timer1: ImageView = view.findViewById(R.id.timer1)
        var timer2: ImageView = view.findViewById(R.id.timer2)

    }
    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_your_task, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tasks = listOfTasks[position]
        holder.label.text = tasks.additionalAttributes.title
        holder.amount.text = tasks.additionalAttributes.amount.toString()
        holder.relation.text = "Father"
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(listOfTasks[position])
        }

        if(position==0){
            holder.new_ll.visible()
            holder.timer1.invisible()
            holder.timer2.invisible()
        }else if(position==1){
            holder.new_ll.invisible()
            holder.timer1.visible()
            holder.timer2.invisible()
        }else if(position==2){
            holder.new_ll.invisible()
            holder.timer1.invisible()
            holder.timer2.visible()
        }else if(position==3){
            holder.new_ll.invisible()
            holder.timer1.invisible()
            holder.timer2.visible()
        }

    }
    override fun getItemCount(): Int {
        return listOfTasks.size
    }

    fun setMovieList(listOfTasks: List<com.fypmoney.model.yourTaskModal.Data>) {
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