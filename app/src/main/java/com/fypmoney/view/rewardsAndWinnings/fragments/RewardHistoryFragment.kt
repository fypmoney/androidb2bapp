package com.fypmoney.view.rewardsAndWinnings.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardHistoryBinding
import com.fypmoney.model.HistoryItem
import com.fypmoney.util.AppConstants
import com.fypmoney.view.rewardsAndWinnings.adapters.RewardsHistoryLeaderboardAdapter
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardHistoryFragmentVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.roundToInt

class RewardHistoryFragment :
    BaseFragment<FragmentRewardHistoryBinding, RewardHistoryFragmentVM>() {
    companion object {
        var page = 0
        fun newInstance(): RewardHistoryFragment {
            return RewardHistoryFragment()
        }
    }

    private var rewardAdapterHistory: RewardsHistoryLeaderboardAdapter? = null
    private var itemsArrayList: ArrayList<HistoryItem> = ArrayList()
    private var mViewBinding: FragmentRewardHistoryBinding? = null
    private var mViewModel: RewardHistoryFragmentVM? = null
    override fun onTryAgainClicked() {

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_reward_history
    }

    override fun getViewModel(): RewardHistoryFragmentVM {

        mViewModel = ViewModelProvider(this).get(RewardHistoryFragmentVM::class.java)


        return mViewModel!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.reward_history),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        mViewBinding?.showHistory?.setOnClickListener {

            findNavController().navigate(R.id.navigation_more_history, null, navOptions {
                anim {
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_righ
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                }
            })
        }

        setRecyclerView(mViewBinding)
        mViewModel?.let { setObserver(it) }
    }

    override fun onStart() {
        super.onStart()
        mViewModel?.callRewardHistory()
        mViewModel?.callRewardSummary()
    }

    private fun setRecyclerView(root: FragmentRewardHistoryBinding?) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root?.rvHistory?.layoutManager = layoutManager

        val itemClickListener2 = object : ListRewardsItemClickListener {
            override fun onItemClicked(historyItem: HistoryItem) {
                when (historyItem.productType) {
                    AppConstants.PRODUCT_SPIN -> {
                        val productId = historyItem.orderNumber.toString()
                        val productCode = historyItem.productCode.toString()
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/spinwheel/${productCode}/${productId}"),
                            navOptions {
                            anim {
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_righ
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                            }
                        })
                    }
                    AppConstants.PRODUCT_TREASURE_BOX -> {
                        val productId = historyItem.orderNumber.toString()
                        val productCode = historyItem.productCode.toString()
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/rotating_treasure/${productCode}/${productId}"), navOptions {
                            anim {
                                popEnter = R.anim.slide_in_left
                                popExit = R.anim.slide_out_righ
                                enter = R.anim.slide_in_right
                                exit = R.anim.slide_out_left
                            }
                        })
                    }
                    else -> {
                        mViewModel?.callProductsDetailsApi(historyItem.orderNumber)
                    }
                }
            }
        }

        rewardAdapterHistory =
            RewardsHistoryLeaderboardAdapter(itemsArrayList, requireContext(), itemClickListener2)
        root?.rvHistory?.adapter = rewardAdapterHistory
    }

    private fun setObserver(sharedViewModel: RewardHistoryFragmentVM) {

        sharedViewModel.rewardHistoryList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                itemsArrayList.clear()
                list.forEach { item ->
                    item.history?.forEach { historyItem ->
                        if (historyItem != null) {
                            itemsArrayList.add(historyItem)
                        }
                    }

            }
            if (list.size > 0) {

                mViewBinding?.showHistory?.visibility = View.VISIBLE
                mViewBinding?.emptyScreen?.visibility = View.GONE
            } else {
                mViewBinding?.showHistory?.visibility = View.GONE
                mViewBinding?.emptyScreen?.visibility = View.VISIBLE


            }
            rewardAdapterHistory?.notifyDataSetChanged()


            })
        sharedViewModel.rewardSummaryStatus.observe(
            viewLifecycleOwner
        ) { list ->
            mViewBinding?.contraint?.visibility = View.VISIBLE
            mViewBinding?.shimmerLayout?.visibility = View.GONE
            mViewBinding?.shimmerLayout?.stopShimmer()

            if (list.totalPoints != null) {
                mViewBinding?.totalearned?.text = String.format("%.0f", list.totalPoints)
            }
            if (list.burntPoints != null) {
                mViewBinding?.burnedPoints?.text = String.format("%.0f", list.burntPoints)
            }

            if ((list.burntPoints != 0.0f) and (list.totalPoints != 0.0f)) {
                mViewBinding?.statsProgressbar?.progress =
                    ((list.burntPoints?.div(list.totalPoints!!))!! * 100).roundToInt()
            } else {
                mViewBinding?.statsProgressbar?.progress = 0
            }

            if (list.remainingPoints != null) {
                mViewBinding?.pointsLeft?.text = String.format("%.0f", list.remainingPoints)
            }


        }
    }



}