package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.base.PaginationListener
import com.fypmoney.model.NotificationModel
import com.fypmoney.view.adapter.NotificationAdapter
import com.fypmoney.view.adapter.UserTimeLineAdapter

import com.fypmoney.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_noti_timeline.view.*


import kotlin.collections.ArrayList


class NotiTimelineFragment : Fragment(), NotificationAdapter.OnNotificationClickListener {
    companion object {
        var page = 0

    }

    private var sharedViewModel: NotificationViewModel? = null

    private var isLoading = false
    private var typeAdapter: UserTimeLineAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_noti_timeline, container, false)
        setRecyclerView(root!!)
        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(NotificationViewModel::class.java)
            observeInput(sharedViewModel!!)

        }





        return root
    }

    private fun observeInput(sharedViewModel: NotificationViewModel) {
        sharedViewModel.timelineList.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                root?.LoadProgressBar?.visibility = View.GONE

                if (page == 0) {
                    typeAdapter?.setList(null)
                }

                typeAdapter?.setList(list)
                isLoading = false
                typeAdapter!!.notifyDataSetChanged()

                if (list.size > 0) {
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
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rv_timeline!!.layoutManager = layoutManager

        root.rv_timeline!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                loadMore(root)
            }

            override fun loadMoreTopItems() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })


        typeAdapter = UserTimeLineAdapter()
        root.rv_timeline!!.adapter = typeAdapter
    }

    private fun loadMore(root: View) {
        sharedViewModel?.callUserTimeLineApi(page)
//        //LoadProgressBar?.visibility = View.VISIBLE
        root.LoadProgressBar?.visibility = View.VISIBLE
        Log.d("chorespage", page.toString())
        isLoading = true

    }

    override fun onNotificationClick(
        notification: NotificationModel.NotificationResponseDetails?,
        position: Int
    ) {

    }

}