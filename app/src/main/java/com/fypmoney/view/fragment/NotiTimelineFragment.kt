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

import com.fypmoney.view.adapter.YourTasksAdapter
import com.fypmoney.view.interfaces.ListItemClickListener

import kotlinx.android.synthetic.main.fragment_your_task.view.*


import kotlin.collections.ArrayList


class NotiTimelineFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: YourTasksAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_noti_timeline, container, false)
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

                if (itemsArrayList.size > 0) {
                    root?.empty_screen?.visibility = View.GONE

                } else {
                    if (page == 0) {
                        root?.empty_screen?.visibility = View.VISIBLE
                    }

                }
                page += 1
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

                ChoresActivity.mViewModel?.yourtask?.set(true)
                ChoresActivity.mViewModel!!.callTaskDetail(itemsArrayList[pos].id.toString())


            }

            override fun onCallClicked(pos: Int) {

            }


        }

        typeAdapter = YourTasksAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
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