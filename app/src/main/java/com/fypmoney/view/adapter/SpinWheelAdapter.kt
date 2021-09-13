package com.fypmoney.view.adapter


import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.R
import kotlinx.android.synthetic.main.spin_banner_item.view.*


class SpinWheelAdapter(
    val items: IntArray,
    val context: Context
) : RecyclerView.Adapter<SpinWheelAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.spin_banner_item, parent, false)
        )

    }

    override fun getItemCount(): Int {

        return items.size
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.banner.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_app_reward_banner_1
                )
            )
        } else if (position == 1) {
            holder.banner.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_app_reward_banner_02
                )
            )
        } else if (position == 2) {
            holder.banner.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_app_reward_banner_03
                )
            )
        }


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var card = view
        var banner = view.banner


    }
}
