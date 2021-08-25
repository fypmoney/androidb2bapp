package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.R
import com.fypmoney.base.PaginationListener
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.adapter.AssignedTasksAdapter
import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.fragment_assigned_task.view.*
import kotlinx.android.synthetic.main.fragment_assigned_task.view.empty_screen
import kotlinx.android.synthetic.main.fragment_assigned_task.view.rv_assigned
import kotlinx.android.synthetic.main.fragment_your_task.view.*
import kotlinx.android.synthetic.main.view_transaction_history.*


import kotlin.collections.ArrayList


class YourTasksFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: AssignedTasksAdapter? = null
    private var root: View?=null


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_your_task, container, false)
        setRecyclerView(root!!)

        ChoresActivity.mViewModel!!.YourAssigned.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                root?.LoadProgressBar?.visibility = View.GONE

                if (page == 0) {
                    itemsArrayList.clear()
                }
                itemsArrayList.addAll(list)
                isLoading = false
                typeAdapter!!.notifyDataSetChanged()
                page += 1
                if (list.size > 0) {
                    root?.empty_screen?.visibility = View.GONE

                } else if (page == 0) {
                    root?.empty_screen?.visibility = View.VISIBLE
                }
            })




    return root
    }


    private fun setRecyclerView(root: View) {
        val layoutManager = GridLayoutManager(requireContext(), 2)
        root.rv_assigned!!.layoutManager = layoutManager

        root.rv_assigned!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                loadMore(root)
            }

            override fun loadMoreTopItems() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {


                ChoresActivity.mViewModel!!.callTaskDetail(itemsArrayList[pos].id.toString())


            }

            override fun onCallClicked(pos: Int) {

            }


        }

        typeAdapter = AssignedTasksAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_assigned!!.adapter = typeAdapter
    }

    private fun loadMore(root: View) {
        ChoresActivity.mViewModel?.callLoadMoreTask(page)
        //LoadProgressBar?.visibility = View.VISIBLE
        root.LoadProgressBar?.visibility = View.VISIBLE
        Log.d("chorespage", page.toString())
        isLoading = true

    }

}