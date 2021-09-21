package com.fypmoney.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.model.AssignedTaskResponse
import com.fypmoney.view.adapter.RewardsLeaderboardAdapter

import com.fypmoney.view.interfaces.ListItemClickListener
import kotlinx.android.synthetic.main.fragment_spinner_list.view.*


import kotlin.collections.ArrayList


class RewardHistoryFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var itemsArrayList: ArrayList<AssignedTaskResponse> = ArrayList()
    private var isLoading = false
    private var typeAdapter: RewardsLeaderboardAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_spinner_list, container, false)
        setRecyclerView(root!!)





        return root
    }


    private fun setRecyclerView(root: View) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rv_assigned!!.layoutManager = layoutManager


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {

            }

            override fun onCallClicked(pos: Int) {

            }


        }

        typeAdapter =
            RewardsLeaderboardAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_assigned!!.adapter = typeAdapter
    }

    private fun loadMore(root: View) {

        //LoadProgressBar?.visibility = View.VISIBLE
        root.LoadProgressBar?.visibility = View.VISIBLE
        Log.d("chorespage", page.toString())
        isLoading = true

    }

}