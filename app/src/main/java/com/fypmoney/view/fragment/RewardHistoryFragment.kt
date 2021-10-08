package com.fypmoney.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.R
import com.fypmoney.model.RewardHistoryResponse2
import com.fypmoney.view.activity.SpinWheelViewDark
import com.fypmoney.view.adapter.RewardsHistoryLeaderboardAdapter

import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.viewmodel.RewardsViewModel
import kotlinx.android.synthetic.main.fragment_reward_history.view.*


import kotlin.collections.ArrayList


class RewardHistoryFragment : Fragment() {
    companion object {
        var page = 0

    }

    private var sharedViewModel: RewardsViewModel? = null
    private var itemsArrayList: ArrayList<RewardHistoryResponse2> = ArrayList()
    private var isLoading = false
    private var typeAdapterHistory: RewardsHistoryLeaderboardAdapter? = null
    private var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_reward_history, container, false)
        setRecyclerView(root!!)

        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(RewardsViewModel::class.java)
            observeInput(sharedViewModel!!)

        }



        return root
    }


    private fun observeInput(sharedViewModel: RewardsViewModel) {

        page = 0
        sharedViewModel.rewardHistoryList2.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                root?.LoadProgressBar?.visibility = View.GONE

                if (page == 0) {
                    itemsArrayList.clear()

                }
                itemsArrayList.addAll(list)

                isLoading = false
                typeAdapterHistory?.notifyDataSetChanged()

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
        root.rv_history?.layoutManager = layoutManager


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {
                val intent = Intent(requireContext(), SpinWheelViewDark::class.java)
                SpinWheelViewDark.sectionArrayList.clear()


//                    val args = Bundle()
//                    args.putSerializable("ARRAYLIST", itemsArrayList as Serializable)
//                    intent.putExtra("BUNDLE", args)
                requireContext().startActivity(intent)
            }

            override fun onCallClicked(pos: Int) {


            }


        }

        typeAdapterHistory =
            RewardsHistoryLeaderboardAdapter(itemsArrayList, requireContext(), itemClickListener2!!)
        root.rv_history!!.adapter = typeAdapterHistory
    }



}