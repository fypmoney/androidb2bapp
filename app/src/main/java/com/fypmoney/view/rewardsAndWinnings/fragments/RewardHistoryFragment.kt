package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardHistoryBinding
import com.fypmoney.model.HistoryItem
import com.fypmoney.util.AppConstants
import com.fypmoney.view.rewardsAndWinnings.adapters.RewardsHistoryLeaderboardAdapter
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.activity.RewardsHistoryView
import com.fypmoney.view.rewardsAndWinnings.activity.SpinWheelViewDark
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener

import kotlin.math.roundToInt


class RewardHistoryFragment : BaseFragment<FragmentRewardHistoryBinding, RewardsAndVM>() {
    companion object {
        var page = 0
    }

    private var rewardAdapterHistory: RewardsHistoryLeaderboardAdapter? = null
    private var itemsArrayList: ArrayList<HistoryItem> = ArrayList()
    private var mViewBinding: FragmentRewardHistoryBinding? = null
    private var mViewModel: RewardsAndVM? = null
    override fun onTryAgainClicked() {

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_reward_history
    }

    override fun getViewModel(): RewardsAndVM {
        activity?.let {
            mViewModel = ViewModelProvider(it).get(RewardsAndVM::class.java)
        }

        return mViewModel!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewBinding?.showHistory?.setOnClickListener {

            val intent = Intent(requireContext(), RewardsHistoryView::class.java)
            requireContext().startActivity(intent)
        }

        setRecyclerView(mViewBinding)
        mViewModel?.let { setObserver(it) }
    }


    private fun setRecyclerView(root: FragmentRewardHistoryBinding?) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root?.rvHistory?.layoutManager = layoutManager

        var itemClickListener2 = object : ListRewardsItemClickListener {


            override fun onItemClicked(historyItem: HistoryItem) {
                if (historyItem.productType == AppConstants.PRODUCT_SPIN) {
                    val intent = Intent(requireContext(), SpinWheelViewDark::class.java)
                    SpinWheelViewDark.sectionArrayList.clear()
                    intent.putExtra(
                        AppConstants.ORDER_NUM,
                        historyItem.orderNumber.toString()
                    )
                    intent.putExtra(AppConstants.NO_GOLDED_CARD, historyItem.noOfJackpotTicket)
                    startActivity(intent)

                } else {
                    mViewModel?.callProductsDetailsApi(historyItem.orderNumber)

                }
            }
        }

        rewardAdapterHistory =
            RewardsHistoryLeaderboardAdapter(itemsArrayList, requireContext(), itemClickListener2)
        root?.rvHistory?.adapter = rewardAdapterHistory
    }
    private fun setObserver(sharedViewModel: RewardsAndVM) {

        sharedViewModel.rewardHistoryList.observe(
            requireActivity(),
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
            requireActivity(),
            androidx.lifecycle.Observer { list ->
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


            })
    }


}