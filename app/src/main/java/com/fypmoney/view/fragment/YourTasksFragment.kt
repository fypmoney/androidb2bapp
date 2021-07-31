package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.adapter.AssignedTasksAdapter
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*


import kotlin.collections.ArrayList


class YourTasksFragment : Fragment() {
    companion object {


    }
    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()

    private var typeAdapter: AssignedTasksAdapter?=null
    private var root: View?=null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_assigned_task, container, false)
        setRecyclerView(root!!)



        ChoresActivity.mViewModel!!.AllAssigned.observe(requireActivity(), androidx.lifecycle.Observer { list ->

            itemsArrayList.clear()
            itemsArrayList.addAll(list)



            typeAdapter!!.notifyDataSetChanged()



        })






    return root
    }


    private fun setRecyclerView(root: View) {
        val layoutManager = GridLayoutManager(requireContext(),2)
         root.rv_assigned!!.layoutManager = layoutManager

        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {


            }
            override fun onCallClicked(pos: Int) {

            }


        }

         typeAdapter = AssignedTasksAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_assigned!!.adapter = typeAdapter
    }


}