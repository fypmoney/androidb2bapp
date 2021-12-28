package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentJackpotOverviewBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserFeedsInAppWebview
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsRewardsJackpotAdapter

import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsJackpotVM
import kotlin.collections.ArrayList


class RewardsJackpotFragment : BaseFragment<FragmentJackpotOverviewBinding, RewardsJackpotVM>(),
    FeedsAdapter.OnFeedItemClickListener {

    var feedList: ArrayList<FeedDetails>? = ArrayList()
    private var mViewBinding: FragmentJackpotOverviewBinding? = null
    private var jackpotViewModel: RewardsJackpotVM? = null
    private var feedJackpotAdapter: FeedsRewardsJackpotAdapter? = null
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_jackpot_overview
    }

    override fun getViewModel(): RewardsJackpotVM {

        jackpotViewModel = ViewModelProvider(this).get(RewardsJackpotVM::class.java)
        return jackpotViewModel!!
    }

    override fun onFeedClick(position: Int, it: FeedDetails) {
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

        setRecyclerView()

        jackpotViewModel?.let { observeInput(it) }


    }


    override fun onTryAgainClicked() {

    }

    override fun onStart() {
        super.onStart()
//        if (!mViewBinding?.totalRefralWonValueTv?.text.isNullOrEmpty()) {
//            mViewBinding?.loadingAmountHdp?.clearAnimation()
//            mViewBinding?.loadingAmountHdp?.visibility = View.GONE
//        }
    }

    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.recyclerView?.layoutManager = layoutManager

        if (!feedList.isNullOrEmpty()) {
            mViewBinding?.shimmerLayout?.stopShimmer()
            mViewBinding?.shimmerLayout?.visibility = View.GONE
        }

        feedJackpotAdapter = jackpotViewModel?.let {
            FeedsRewardsJackpotAdapter(
                requireActivity(),
                it,
                this,
                feedList
            )
        }
        mViewBinding?.recyclerView?.adapter = feedJackpotAdapter

    }

    private fun observeInput(mViewModel: RewardsJackpotVM) {

        mViewModel.jackpotfeedList.observe(viewLifecycleOwner, { list ->
            if (!list.isNullOrEmpty()) {
                mViewBinding?.shimmerLayout?.stopShimmer()

                feedList?.addAll(list)
                mViewBinding?.shimmerLayout?.visibility = View.GONE
                feedJackpotAdapter?.notifyDataSetChanged()
            }


        })


        mViewModel.totalJackpotAmount.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                mViewBinding?.loadingAmountHdp?.clearAnimation()
                mViewBinding?.loadingAmountHdp?.visibility = View.GONE
                if (list.count != null) {
                    mViewBinding?.totalRefralWonValueTv?.text =
                        "${list.count}"
                }
                if (list.totalJackpotMsg != null) {
                    mViewBinding?.totalRefralWonTv?.text = "${list.totalJackpotMsg}"
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