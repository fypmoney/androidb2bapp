package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardHistoryBinding
import com.fypmoney.model.HistoryItem
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.rewardsAndWinnings.adapters.RewardsHistoryLeaderboardAdapter
import com.fypmoney.view.interfaces.ListContactClickListener
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.activity.RewardsHistoryView
import com.fypmoney.view.rewardsAndWinnings.activity.ScratchCardActivity
import com.fypmoney.view.rewardsAndWinnings.activity.SpinWheelViewDark
import com.fypmoney.view.rewardsAndWinnings.interfaces.ListRewardsItemClickListener
import jp.wasabeef.blurry.Blurry
import kotlin.math.roundToInt


class RewardHistoryFragment : BaseFragment<FragmentRewardHistoryBinding, RewardsAndVM>() {
    companion object {
        var page = 0
    }

    private var typeAdapterHistory: RewardsHistoryLeaderboardAdapter? = null
    private var itemsArrayList: ArrayList<HistoryItem> = ArrayList()
    private var mViewBinding: FragmentRewardHistoryBinding? = null
    private var sharedViewModel: RewardsAndVM? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewBinding?.showHistory?.setOnClickListener(View.OnClickListener {

            val intent = Intent(requireContext(), RewardsHistoryView::class.java)
            requireContext().startActivity(intent)
        })
        Blurry.with(requireContext())
            .radius(25)
            .sampling(2)
            .async()
            .animate(500)
            .onto(mViewBinding?.blur as ViewGroup)

        setRecyclerView(mViewBinding)
        sharedViewModel?.let { setObserver(it) }
    }


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
            sharedViewModel = ViewModelProvider(it).get(RewardsAndVM::class.java)

//            setObserver(sharedViewModel!!)
        }

        return sharedViewModel!!
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
                        AppConstants.ORDER_ID,
                        historyItem.orderNumber.toString()
                    )
                    startActivity(intent)

//                    val args = Bundle()
//                    args.putSerializable("ARRAYLIST", itemsArrayList as Serializable)
//                    intent.putExtra("BUNDLE", args)


                } else {
                    val intent = Intent(requireContext(), ScratchCardActivity::class.java)
                    intent.putExtra(
                        AppConstants.ORDER_ID,
                        historyItem.orderNumber.toString()
                    )
                    startActivity(intent)

                }
            }
        }

        typeAdapterHistory =
            RewardsHistoryLeaderboardAdapter(itemsArrayList, requireContext(), itemClickListener2)
        root?.rvHistory?.adapter = typeAdapterHistory
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
                typeAdapterHistory?.notifyDataSetChanged()


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