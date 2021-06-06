package com.fypmoney.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import com.fypmoney.util.AppConstants
import com.fypmoney.view.activity.AddTaskActivity


internal class TaskHistoryStaggeredAdapter(context:Context, var listOfTasks: List<com.fypmoney.model.chaoseHistory.Data>) :
    RecyclerView.Adapter<TaskHistoryStaggeredAdapter.MyViewHolder>(){
    private val examples: List<com.fypmoney.model.chaoseHistory.Data>
    private val context: Context

    init {
        this.examples = listOfTasks
        this.context = context
    }
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
        holder.label.text = tasks!!.description

        if(position==0){
            holder.card_add.visible()
            holder.card_main.invisible()
            holder.label.text ="Create your own task"
            holder.ttl  .text = "Create your own task"

        }else{
            holder.card_add.invisible()
            holder.card_main.visible()
            holder.label.text = tasks.name
            holder.ttl  .text = tasks.description
            holder.amount.text = tasks.amount?.toString()
            holder.relation.text = "Father"
        }

        holder.card_add.setOnClickListener {
            val intten=Intent(context,AddTaskActivity::class.java)
            intten.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
            context.startActivity(intten)
        }

    }
    override fun getItemCount(): Int {
        return listOfTasks.size
    }

    fun setMovieList(listOfTasks: List<com.fypmoney.model.chaoseHistory.Data>) {
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
