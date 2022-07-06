package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentRewardsOverviewBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoActivityWithExplore
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserFeedsInAppWebview
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.rewardsAndWinnings.CashBackWonHistoryActivity
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsAndVM
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN


class RewardsOverviewFragment() :
    BaseFragment<FragmentRewardsOverviewBinding, RewardsAndVM>(),
    FeedsAdapter.OnFeedItemClickListener {

    private var mLastClickTime: Long = 0

    companion object {
        var page = 0
        fun newInstance():RewardsOverviewFragment{
            return RewardsOverviewFragment()
        }

    }

    private var mViewBinding: FragmentRewardsOverviewBinding? = null
    private var mViewmodel: RewardsAndVM? = null

    //private var feedsRewardsAdapter: FeedsRewardsAdapter? = null

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
        mViewBinding?.bootomPartCl?.setOnClickListener {
            val intent = Intent(requireContext(), CashBackWonHistoryActivity::class.java)
            startActivity(intent)
        }
        mViewBinding?.totalMyntsLayout?.setOnClickListener {
            //tabchangeListner.tabchange(0, getString(R.string.reward_history))
            findNavController().navigate(R.id.navigation_arcade)
        }
        mViewBinding?.goldenCardLayout?.setOnClickListener {

            //tabchangeListner.tabchange(0, getString(R.string.jackpot))
//            findNavController().navigate(R.id.navigation_jackpot)
            findNavController().navigate(R.id.spinWheelFragment)

        }

    }



    override fun onTryAgainClicked() {

    }

    private fun setRecyclerView(
        root: FragmentRewardsOverviewBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.exploreShimmerLayout.visibility = View.GONE
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.exploreHomeRv.layoutManager = layoutManager

        var arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        var exploreClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(position: Int, it1: SectionContentItem, exploreContentResponse: ExploreContentResponse?) {
                trackr {
                    it.name = TrackrEvent.home_explore_click
                    it.add(TrackrField.explore_content_id, it1.id)
                }
                openExploreFeatures(
                    it1.redirectionType,
                    it1.redirectionResource,
                    it1,
                    exploreContentResponse
                )


            }
        }
        val scale: Float = requireActivity().resources.displayMetrics.density
        val typeAdapter = ExploreBaseAdapter(
            arrayList,
            requireContext(),
            exploreClickListener2,
            scale,
            Color.WHITE
        )
        root.exploreHomeRv.adapter = typeAdapter
    }

    private fun openExploreFeatures(
        redirectionType: String?,
        redirectionResource: String?,
        sectionContentItem: SectionContentItem,
        exploreContentResponse: ExploreContentResponse?
    ) {
        when (redirectionType) {
            AppConstants.TYPE_VIDEO -> {
                val intent = Intent(requireActivity(), VideoActivity2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, redirectionResource)

                startActivity(intent)

            }
            AppConstants.TYPE_VIDEO_EXPLORE -> {
                val intent = Intent(requireActivity(), VideoActivityWithExplore::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, sectionContentItem.redirectionResource)
                intent.putExtra(AppConstants.ACTIONFLAG, sectionContentItem.actionFlagCode)

                startActivity(intent)
            }
            AppConstants.EXPLORE_SECTION_EXPLORE -> {
                val directions = exploreContentResponse?.sectionDisplayText?.let { it1 ->
                    RewardsOverviewFragmentDirections.actionExploreSectionExplore(
                        sectionExploreItem = sectionContentItem,
                        sectionExploreName = it1
                    )
                }
                directions?.let { it1 -> findNavController().navigate(it1) }
            }
            AppConstants.EXPLORE_IN_APP -> {

                redirectionResource?.let { uri ->

                    val redirectionResources = uri?.split(",")?.get(0)
                    if (redirectionResources == AppConstants.FyperScreen) {
                        findNavController().navigate(R.id.navigation_fyper)
                    } else if (redirectionResources == AppConstants.JACKPOTTAB) {
                        findNavController().navigate(R.id.navigation_jackpot)
                    } else if (redirectionResources == AppConstants.CardScreen) {
                        findNavController().navigate(R.id.navigation_card)
                    } else if (redirectionResources == AppConstants.RewardHistory) {
                        findNavController().navigate(R.id.navigation_rewards_history)
                    }else if (redirectionResources == AppConstants.ARCADE) {
                        findNavController().navigate(R.id.navigation_arcade)
                    }else {
                        redirectionResources?.let { it1 ->
                            Utility.deeplinkRedirection(
                                it1,
                                requireContext()
                            )
                        }
                    }


                }

            }
            AppConstants.EXPLORE_IN_APP_WEBVIEW -> {

                val intent = Intent(requireContext(), ExploreInAppWebview::class.java)
//        intent.putExtra(AppConstants.EXPLORE_RESPONSE, feedDetails)
                intent.putExtra(
                    AppConstants.FROM_WHICH_SCREEN,
                    AppConstants.EXPLORE_IN_APP_WEBVIEW
                )
                intent.putExtra(AppConstants.IN_APP_URL, redirectionResource)

                startActivity(intent)
            }
            AppConstants.IN_APP_WITH_CARD -> {

                val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, redirectionResource)
                startActivity(intent)
            }
            AppConstants.OFFER_REDIRECTION -> {
                mViewmodel?.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                mViewmodel?.callFetchFeedsApi(redirectionResource)

            }

            AppConstants.EXT_WEBVIEW -> {
                if (redirectionResource != null) {
                    startActivity(
                        Intent.createChooser(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(redirectionResource)
                            ), getString(R.string.browse_with)
                        )
                    )
                }


            }
            AppConstants.EXPLORE_TYPE_STORIES -> {
                if (!redirectionResource.isNullOrEmpty()) {
                    mViewmodel?.callFetchFeedsApi(redirectionResource)

                }

            }
        }
    }
    private fun setRecyclerView() {
       /* val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mViewBinding?.recyclerView?.layoutManager = layoutManager




        feedsRewardsAdapter = mViewmodel?.let { FeedsRewardsAdapter(requireActivity(), it, this) }
        mViewBinding?.recyclerView?.adapter = feedsRewardsAdapter*/

    }

    private fun observeInput(viewModel: RewardsAndVM) {
/*
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
*/

        viewModel.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->

            mViewBinding?.let { setRecyclerView(it, list) }
        }

        viewModel.openBottomSheet.observe(
            viewLifecycleOwner,
            { list ->

                if(list.isNotEmpty()){
                    callOfferDetailsSheeet(list[0])
                }
            })

        viewModel.feedDetail.observe(
            viewLifecycleOwner,
            { list ->

                when (list.displayCard) {

                    AppConstants.FEED_TYPE_BLOG -> {

                        intentToActivitytoBlog(
                            UserFeedsDetailView::class.java,
                            list,
                            AppConstants.FEED_TYPE_BLOG
                        )
                    }
                    AppConstants.FEED_TYPE_STORIES -> {

                        callDiduKnowBottomSheet(list.resourceArr)
                    }

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



    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        var bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(childFragmentManager, "TASKMESSAGE")
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

    private fun intentToActivitytoBlog(
        aClass: Class<*>,
        feedDetails: FeedDetails,
        type: String? = null
    ) {
        val intent = Intent(context, aClass)
        intent.putExtra(AppConstants.FEED_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

}