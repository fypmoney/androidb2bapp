package com.fypmoney.view.adapter

import android.app.LauncherActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.fypmoney.databinding.MyProfileListRowItemBinding

class CardListViewAdapter(
    context: Context? = null, var onItemClickListener: OnListItemClickListener
) :
    ArrayAdapter<LauncherActivity.ListItem>(context!!, 0) {

    var iconList = ArrayList<Int>()
    var titleList = ArrayList<String>()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        /* val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_facilities_adapter, parent, false)
        return FacilitiesViewHolder(view)*/
        val binding =
            MyProfileListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.title = titleList[position]
        binding.image.setImageResource(iconList[position])

        binding.linear.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }
        return binding.root
    }

    override fun getCount(): Int {
        return titleList.size
    }


    fun setList(iconList1: List<Int>, titleList1: List<String>) {
        iconList.clear()
        titleList.clear()
        iconList.addAll(iconList1)
        titleList.addAll(titleList1)
        notifyDataSetChanged()
    }

    interface OnListItemClickListener {
        fun onItemClick(position: Int)
    }
}