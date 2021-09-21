package com.fypmoney.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.R
import com.fypmoney.base.PaginationListener
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.activity.ChoresActivity

import com.fypmoney.view.adapter.YourTasksAdapter
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.NotificationViewModel
import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.fragment_rewards_overview.view.*

import kotlinx.android.synthetic.main.fragment_your_task.view.*
import kotlinx.android.synthetic.main.fragment_your_task.view.LoadProgressBar


import kotlin.collections.ArrayList



class RewardsOverviewFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var sharedViewModel: RewardsViewModel? = null
    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: YourTasksAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_rewards_overview, container, false)
        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(RewardsViewModel::class.java)
            observeInput(sharedViewModel!!)

        }


        return root
    }

    private fun observeInput(sharedViewModel: RewardsViewModel) {

    }



}