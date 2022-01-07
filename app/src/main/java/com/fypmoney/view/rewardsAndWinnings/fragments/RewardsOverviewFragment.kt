package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardsOverviewBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserFeedsInAppWebview
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsRewardsAdapter
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.interfaces.HomeTabChangeClickListener
import com.fypmoney.view.rewardsAndWinnings.CashBackWonHistoryActivity
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM


class RewardsOverviewFragment(val tabchangeListner: HomeTabChangeClickListener) :
    BaseFragment<FragmentRewardsOverviewBinding, RewardsAndVM>(),
    FeedsAdapter.OnFeedItemClickListener {

    private var mLastClickTime: Long = 0

    companion object {
        var page = 0
        fun newInstance( tabchangeListner: HomeTabChangeClickListener):RewardsOverviewFragment{
            return RewardsOverviewFragment(tabchangeListner)
        }

    }

    private var mViewBinding: FragmentRewardsOverviewBinding? = null
    private var mViewmodel: RewardsAndVM? = null

    private var feedsRewardsAdapter: FeedsRewardsAdapter? = null

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_rewards_overview
    }

    override fun getViewModel(): RewardsAndVM {

        mViewmodel = ViewModelProvider(this).get(RewardsAndVM::class.java)



        return mViewmodel!!
    }

    override fun onFeedClick(position: Int, it: FeedDetails) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        when (it.displayCard) {
            AppConstants.FEED_TYPE_DEEPLINK -> {
                it.action?.url?.let {
                    Utility.deeplinkRedirection(it.split(",")[0], requireContext())

                }
            }
            AppConstants.FEED_TYPE_INAPPWEB2 -> {
                intentToActivity(
                    UserFeedsInAppWebview::class.java,
                    it,
                    AppConstants.FEED_TYPE_INAPPWEB
                )
            }
            AppConstants.FEED_TYPE_EXTWEBVIEW2 -> {
                startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(it.action?.url)
                        ), getString(R.string.browse_with)
                    )
                )

            }
            AppConstants.FEED_TYPE_INAPPWEB -> {
                intentToActivity(
                    UserFeedsInAppWebview::class.java,
                    it,
                    AppConstants.FEED_TYPE_INAPPWEB
                )
            }
            AppConstants.FEED_TYPE_BLOG -> {
                intentToActivity(
                    UserFeedsDetailView::class.java,
                    it,
                    AppConstants.FEED_TYPE_BLOG
                )
            }
            AppConstants.FEED_TYPE_EXTWEBVIEW -> {
                startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(it.action?.url)
                        ), getString(R.string.browse_with)
                    )
                )

            }
            AppConstants.FEED_TYPE_STORIES -> {
                if (!it.resourceArr.isNullOrEmpty()) {
                    callDiduKnowBottomSheet(it.resourceArr)
                }

            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()




        mViewBinding?.loadingGoldenCards?.visibility = View.VISIBLE
        mViewBinding?.loadingAmountMynts?.visibility = View.VISIBLE
        mViewBinding?.loadingAmountHdp?.visibility = View.VISIBLE

        setRecyclerView()
        mViewmodel?.let { observeInput(it) }
        mViewBinding?.bootomPartCl?.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), CashBackWonHistoryActivity::class.java)
            startActivity(intent)
        })
        mViewBinding?.totalMyntsLayout?.setOnClickListener(View.OnClickListener {
            tabchangeListner.tabchange(0, getString(R.string.reward_history))
        })
        mViewBinding?.goldenCardLayout?.setOnClickListener(View.OnClickListener {

            tabchangeListner.tabchange(0, getString(R.string.jackpot))
        })

    }

    override fun onStart() {
        super.onStart()
//        if (!mViewBinding?.totalRefralWonValueTv?.text.isNullOrEmpty()) {
//            mViewBinding?.loadingAmountHdp?.visibility = View.GONE
//        }
//
//        if (!mViewBinding?.amountGolderTv?.text.isNullOrEmpty()) {
//            mViewBinding?.loadingGoldenCards?.visibility = View.GONE
//        }
//        if (!mViewBinding?.totalMyntsWonValueTv?.text.isNullOrEmpty()) {
//            mViewBinding?.loadingAmountMynts?.visibility = View.GONE
//        }
    }

    override fun onTryAgainClicked() {

    }

    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.recyclerView?.layoutManager = layoutManager




        feedsRewardsAdapter = mViewmodel?.let { FeedsRewardsAdapter(requireActivity(), it, this) }
        mViewBinding?.recyclerView?.adapter = feedsRewardsAdapter

    }

    private fun observeInput(viewModel: RewardsAndVM) {
        viewModel.rewardfeedList.observe(viewLifecycleOwner, { list ->
            if (list.isNullOrEmpty()) {


                mViewBinding?.recyclerView?.visibility = View.GONE
            } else {
                mViewBinding?.shimmerLayout?.stopShimmer()
                feedsRewardsAdapter?.setList(list)

                mViewBinding?.shimmerLayout?.visibility = View.GONE
                feedsRewardsAdapter?.notifyDataSetChanged()

            }


        })


        viewModel.totalRewardsResponse.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                mViewBinding?.loadingAmountHdp?.clearAnimation()
                mViewBinding?.loadingAmountHdp?.visibility = View.GONE
                mViewBinding?.totalRefralWonValueTv?.text =
                    getString(R.string.rupee_symbol) + Utility.convertToRs("${list.amount}")


            })

        viewModel.rewardSummaryStatus.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                mViewBinding?.loadingAmountMynts?.clearAnimation()
                mViewBinding?.loadingAmountMynts?.visibility = View.GONE
                if (list.totalPoints != null) {
                    mViewBinding?.totalMyntsWonValueTv?.text =
                        String.format("%.0f", list.remainingPoints)
                }


            })

        viewModel.totalJackpotAmount.observe(
            viewLifecycleOwner,
            { list ->
                mViewBinding?.loadingGoldenCards?.clearAnimation()
                mViewBinding?.loadingGoldenCards?.visibility = View.GONE
                mViewBinding?.amountGolderTv?.visibility = View.VISIBLE
                if (list.count != null) {
                    mViewBinding?.amountGolderTv?.text =
                        "${list.count}"
                }
                if (list.totalJackpotMsg != null) {
                    mViewBinding?.golderCardWonHeading?.text = "${list.totalJackpotMsg}"
                }

            })
    }



    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "DidUKnowSheet")
    }

    private fun intentToActivity(aClass: Class<*>, feedDetails: FeedDetails, type: String? = null) {
        val intent = Intent(context, aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

}