package com.fypmoney.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.util.Utility

import com.fypmoney.view.adapter.YourTasksAdapter
import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.fragment_rewards_overview.view.*


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
            observeInput(sharedViewModel!!, root!!)

        }


        return root
    }

    private fun observeInput(sharedViewModel: RewardsViewModel, root: View) {

        sharedViewModel.rewardSummaryStatus.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                root.totalearned.text = list.totalPoints.toString()
                root.burned_points.text = list.burntPoints.toString()
                root.points_left.text = list.remainingPoints.toString()
            })

        sharedViewModel.totalRewardsResponse.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                root.loading_amount_hdp.clearAnimation()
                root.loading_amount_hdp.visibility = View.GONE
                root.total_refral_won_value_tv.text = Utility.convertToRs("${list.amount}")

            })

    }


}