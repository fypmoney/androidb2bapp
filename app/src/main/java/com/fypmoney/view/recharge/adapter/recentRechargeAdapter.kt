package com.fypmoney.view.recharge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R

class recentRechargeAdapter : RecyclerView.Adapter<recentRechargeAdapter.ViewHolder>() {

    private var images = intArrayOf(
        R.drawable.ic_user2,
        R.drawable.ic_user2,
        R.drawable.ic_user2,
        R.drawable.ic_user2
    )
    private var name = arrayOf("Sahil", "Sahil", "Sahil", "Sahil")
    private var phoneNumber = longArrayOf(8574982934, 8574982934, 8574982934, 8574982934)
    private var lastRecharge = arrayOf(
        "Last recharge: 300 on 16 sep 2021",
        "Last recharge: 300 on 16 sep 2021",
        "Last recharge: 300 on 16 sep 2021",
        "Last recharge: 300 on 16 sep 2021"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_recharge_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivPhoto.setImageResource(images[position])
        holder.tvName.text = name[position]
        holder.tvPhone.text = phoneNumber[position].toString()
        holder.tvRecharge.text = lastRecharge[position]
    }

    override fun getItemCount(): Int {
        return name.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivPhoto: ImageView
        var tvName: TextView
        var tvPhone: TextView
        var tvRecharge: TextView

        init {
            tvName = itemView.findViewById(R.id.tvUserName)
            ivPhoto = itemView.findViewById(R.id.ivUser)
            tvPhone = itemView.findViewById(R.id.tvUserNumber)
            tvRecharge = itemView.findViewById(R.id.tvLastRecharge)

        }
    }
}