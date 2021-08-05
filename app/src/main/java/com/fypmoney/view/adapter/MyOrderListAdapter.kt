package com.fypmoney.view.adapter

import android.app.LauncherActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.fypmoney.databinding.MyOrderListRowItemBinding

class MyOrderListAdapter(
    context: Context? = null,
    var fromWhichScreen: String? = null
) :
    ArrayAdapter<LauncherActivity.ListItem>(context!!, 0) {

    var iconList = ArrayList<Int>()
    var titleList = ArrayList<String>()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding =
            MyOrderListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.title = titleList[position]
        binding.icon = iconList[position]

        binding.image.setImageResource(iconList[position])
        return binding.root
    }

    override fun getCount(): Int {
        return titleList.size
    }

    /*
    * This method is used to set data in the list
    * */
    fun setList(iconList1: List<Int>, titleList1: List<String>) {
        iconList.clear()
        titleList.clear()
        iconList.addAll(iconList1)
        titleList.addAll(titleList1)
        notifyDataSetChanged()
    }

    /*
      * This method is used to set update data in the list
      * */
    fun updateList(value: String) {
        titleList[1] = value
        notifyDataSetChanged()

    }

    /*
    * This method is used to set update data in the list
    * */
    fun removeList(value: String) {
        titleList[1] = value
        notifyDataSetChanged()

    }

    interface OnListItemClickListener {
        fun onItemClick(position: Int)
    }
}