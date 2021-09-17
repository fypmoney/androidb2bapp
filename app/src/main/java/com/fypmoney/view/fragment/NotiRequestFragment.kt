package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.base.PaginationListener
import com.fypmoney.model.NotificationModel
import com.fypmoney.view.adapter.NotificationAdapter

import com.fypmoney.viewmodel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_noti_request.view.*

import kotlinx.android.synthetic.main.fragment_your_task.view.LoadProgressBar
import kotlinx.android.synthetic.main.fragment_your_task.view.empty_screen


import kotlin.collections.ArrayList


class NotiRequestFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var sharedViewModel: NotificationViewModel? = null

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
            sharedViewModel = ViewModelProviders.of(it).get(NotificationViewModel::class.java)
            observeInput(sharedViewModel!!)

        }




        return root
    }

    private fun observeInput(sharedViewModel: NotificationViewModel) {
        sharedViewModel.removedItem.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->

                if (list != null) {
                    sharedViewModel.onRefresh()
                    typeAdapter?.updateList(
                        notification = list,
                        position = sharedViewModel.positionSelected.get()!!
                    )
                    sharedViewModel.removedItem.postValue(null)
                }

            })


        sharedViewModel.RequestNotificationList.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                root?.LoadProgressBar?.visibility = View.GONE

                if (page == 0) {
                    typeAdapter?.setList(null)
                }

                isLoading = false
                typeAdapter?.setList(list)
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
        root.rv_request!!.layoutManager = layoutManager

        root.rv_request!!.addOnScrollListener(object : PaginationListener(layoutManager) {
            override fun loadMoreItems() {
                loadMore(root)
            }

            override fun loadMoreTopItems() {

            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        var itemClickListener2 = object : NotificationAdapter.OnNotificationClickListener {
            override fun onNotificationClick(
                notification: NotificationModel.NotificationResponseDetails?,
                position: Int
            ) {
                sharedViewModel?.positionSelected?.set(position)
                sharedViewModel?.notificationSelectedResponse = notification!!
                sharedViewModel?.onNotificationClicked?.value = true
            }


        }

        typeAdapter = NotificationAdapter(itemClickListener2)
        root.rv_request!!.adapter = typeAdapter
    }

    private fun loadMore(root: View) {
        sharedViewModel?.callGetFamilyNotificationApi(page)
//        //LoadProgressBar?.visibility = View.VISIBLE
        root.LoadProgressBar?.visibility = View.VISIBLE
        Log.d("chorespage", page.toString())
        isLoading = true

    }

}