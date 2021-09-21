package com.fypmoney.view.fragment

import android.graphics.Color
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
import kotlinx.android.synthetic.main.fragment_rewards_overview.view.*

import kotlinx.android.synthetic.main.fragment_your_task.view.*
import kotlinx.android.synthetic.main.fragment_your_task.view.LoadProgressBar


import kotlin.collections.ArrayList
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.utils.ColorTemplate

import com.github.mikephil.charting.data.PieData

import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class RewardsOverviewFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: YourTasksAdapter? = null
    private var root: View? = null
    var pieChart: PieChart? = null
    var pieDataSet: PieDataSet? = null
    var pieData: PieData? = null
    var pieEntries: ArrayList<PieEntry> = ArrayList()
    val JOYFUL_COLORS = intArrayOf(
        Color.rgb(217, 80, 138), Color.rgb(254, 149, 7), Color.rgb(254, 247, 120),
        Color.rgb(106, 167, 134), Color.rgb(53, 194, 209)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_rewards_overview, container, false)

        pieChart = root!!.pieChart
        getEntries();
        pieDataSet = PieDataSet(pieEntries, "")
        pieData = PieData(pieDataSet)
        pieChart?.data = pieData
        pieDataSet!!.setColors(Color.rgb(217, 80, 138), Color.rgb(254, 149, 7))

        pieChart!!.transparentCircleRadius = 36f;
        pieChart!!.holeRadius = 70f

        pieDataSet!!.valueTextColor = Color.TRANSPARENT
        pieDataSet!!.valueTextSize = 10f
        pieChart!!.setHoleColor(Color.TRANSPARENT);
        pieDataSet!!.sliceSpace = 0f

        return root
    }

    private fun getEntries() {
        pieEntries = ArrayList()
        pieEntries.add(PieEntry(2f, 0))
        pieEntries.add(PieEntry(4f, 1))

    }


    private fun loadMore(root: View) {

        //LoadProgressBar?.visibility = View.VISIBLE
        root.LoadProgressBar?.visibility = View.VISIBLE
        Log.d("chorespage", page.toString())
        isLoading = true

    }

}