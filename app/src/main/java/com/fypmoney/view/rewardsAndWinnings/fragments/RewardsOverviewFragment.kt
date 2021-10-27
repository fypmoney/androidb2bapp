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
import com.fypmoney.databinding.FragmentRewardsOverviewBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserFeedsInAppWebview
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.adapter.FeedsRewardsAdapter

import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.interfaces.ListItemClickListener
import com.fypmoney.view.rewardsAndWinnings.CashBackWonHistoryActivity


class RewardsOverviewFragment : BaseFragment<FragmentRewardsOverviewBinding, RewardsAndVM>(),
    FeedsAdapter.OnFeedItemClickListener {
    companion object {
        var page = 0

    }

    private var mViewBinding: FragmentRewardsOverviewBinding? = null
    private var sharedViewModel: RewardsAndVM? = null

    private var typeAdapter: FeedsRewardsAdapter? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()



        setRecyclerView()
        sharedViewModel?.let { observeInput(it) }
        mViewBinding?.bootomPartCl?.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), CashBackWonHistoryActivity::class.java)

            startActivity(intent)
        })
        mViewBinding?.totalMyntsLayout?.setOnClickListener(View.OnClickListener {
            sharedViewModel?.totalmyntsClicked?.postValue(true)
        })

    }


    override fun onTryAgainClicked() {

    }

    private fun setRecyclerView() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.recyclerView?.layoutManager = layoutManager


        var itemClickListener2 = object : ListItemClickListener {


            override fun onItemClicked(pos: Int) {


            }

            override fun onCallClicked(pos: Int) {

            }


        }


        typeAdapter = sharedViewModel?.let { FeedsRewardsAdapter(requireActivity(), it, this) }
        mViewBinding?.recyclerView?.adapter = typeAdapter

    }

    private fun observeInput(sharedViewModel: RewardsAndVM) {
        sharedViewModel.rewardfeedList.observe(requireActivity(), { list ->
            if (list.isNullOrEmpty()) {
                mViewBinding?.recyclerView?.visibility = View.GONE
            } else {
                mViewBinding?.shimmerLayout?.stopShimmer()
                typeAdapter?.setList(list)

                mViewBinding?.shimmerLayout?.visibility = View.GONE
                typeAdapter?.notifyDataSetChanged()

            }


        })


        sharedViewModel.totalRewardsResponse.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                mViewBinding?.loadingAmountHdp?.clearAnimation()
                mViewBinding?.loadingAmountHdp?.visibility = View.GONE
                mViewBinding?.totalRefralWonValueTv?.text =
                    "₹" + Utility.convertToRs("${list.amount}")

            })

        sharedViewModel.rewardSummaryStatus.observe(
            requireActivity(),
            androidx.lifecycle.Observer { list ->
                mViewBinding?.loadingAmountMynts?.clearAnimation()
                mViewBinding?.loadingAmountMynts?.visibility = View.GONE
                if (list.totalPoints != null) {
                    mViewBinding?.totalMyntsWonValueTv?.text =
                        String.format("%.0f", list.remainingPoints)
                }


            })

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_rewards_overview
    }

    override fun getViewModel(): RewardsAndVM {
        activity?.let {
            sharedViewModel = ViewModelProvider(it).get(RewardsAndVM::class.java)
//            observeInput(sharedViewModel!!)

        }
        return sharedViewModel!!
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