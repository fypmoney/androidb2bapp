package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardHistoryBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.rewardsAndWinnings.activity.RewardsHistoryView
import kotlin.math.roundToInt


class RewardHistoryFragment : BaseFragment<FragmentRewardHistoryBinding, RewardsAndVM>() {
    companion object {
        var page = 0

    }

    private var mViewBinding: FragmentRewardHistoryBinding? = null
    private var sharedViewModel: RewardsAndVM? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewBinding?.showHistory?.setOnClickListener(View.OnClickListener {

            val intent = Intent(requireContext(), RewardsHistoryView::class.java)
            requireContext().startActivity(intent)
        })
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

    private fun setObserver(sharedViewModel: RewardsAndVM) {
        sharedViewModel.rewardSummaryStatus.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->

                mViewBinding?.contraint?.visibility = View.VISIBLE
                mViewBinding?.shimmerLayout?.visibility = View.GONE
                mViewBinding?.shimmerLayout?.stopShimmer()
                mViewBinding?.totalearned?.text = Utility.convertToRs(list.totalPoints.toString())
                mViewBinding?.burnedPoints?.text = Utility.convertToRs(list.burntPoints.toString())
                mViewBinding?.statsProgressbar?.progress =
                    ((list.burntPoints?.div(list.totalPoints!!))!! * 100).roundToInt()
                mViewBinding?.pointsLeft?.text =
                    Utility.convertToRs(list.remainingPoints.toString())
            })
    }


}