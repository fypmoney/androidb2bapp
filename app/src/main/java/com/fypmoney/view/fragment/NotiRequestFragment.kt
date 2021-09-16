package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.R
import com.fypmoney.base.PaginationListener
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.model.NotificationModel
import com.fypmoney.view.activity.ChoresActivity
import com.fypmoney.view.adapter.NotificationAdapter

import com.fypmoney.view.adapter.YourTasksAdapter
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.NotificationViewModel

import kotlinx.android.synthetic.main.fragment_your_task.view.*


import kotlin.collections.ArrayList


class NotiRequestFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var itemsArrayList: ArrayList<NotificationModel.NotificationResponseDetails> =
        ArrayList()
    private var isLoading = false
    private var typeAdapter: NotificationAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_noti_request, container, false)
        setRecyclerView(root!!)
        activity?.let {
            val sharedViewModel = ViewModelProviders.of(it).get(NotificationViewModel::class.java)
            observeInput(sharedViewModel)

        }




        return root
    }

    private fun observeInput(sharedViewModel: NotificationViewModel) {
        sharedViewModel.RequestNotificationList.observe(
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

        typeAdapter = NotificationAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
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