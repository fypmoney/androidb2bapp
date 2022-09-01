package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentJackpotOverviewBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoWithExploreFragment
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserFeedsInAppWebview
import com.fypmoney.view.adapter.FeedsAdapter
import com.fypmoney.view.storeoffers.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.view.ExploreFragment
import com.fypmoney.view.rewardsAndWinnings.viewModel.RewardsJackpotVM
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.toolbar.*


class RewardsJackpotFragment : BaseFragment<FragmentJackpotOverviewBinding, RewardsJackpotVM>(),
    FeedsAdapter.OnFeedItemClickListener {

    companion object{
        fun newInstance():RewardsJackpotFragment{
            return RewardsJackpotFragment()
        }
    }
    var feedList: ArrayList<FeedDetails>? = ArrayList()
    private var mViewBinding: FragmentJackpotOverviewBinding? = null
    private var jackpotViewModel: RewardsJackpotVM? = null
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

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.jackpot),
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        trackr {
            it.name = TrackrEvent.open_jackpot
        }
        jackpotViewModel?.let { observeInput(it) }
        setObserver()
    }


    override fun onTryAgainClicked() {

    }

    private fun observeInput(mViewModel: RewardsJackpotVM) {
        mViewModel.totalJackpotAmount.observe(
            viewLifecycleOwner
        ) { list ->
            mViewBinding?.loadingAmountHdp?.clearAnimation()
            mViewBinding?.loadingAmountHdp?.visibility = View.GONE
            if (list.count != null) {
                mViewBinding?.totalRefralWonValueTv?.text =
                    "${list.count}"
            }
            if (list.totalJackpotMsg != null) {
                mViewBinding?.totalRefralWonTv?.text = "${list.totalJackpotMsg}"
            }

        }


    }
    private fun setObserver() {
        jackpotViewModel?.rewardHistoryList?.observe(viewLifecycleOwner) { list ->
            setRecyclerView(mViewBinding!!, list)
        }
        jackpotViewModel?.openBottomSheet?.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        jackpotViewModel?.feedDetail?.observe(
            viewLifecycleOwner
        ) { list ->

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


        }

    }

    private fun setRecyclerView(
        root: FragmentJackpotOverviewBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size == 0) {
            root.shimmerLayout.toVisible()
        }else{
            root.shimmerLayout.toGone()

        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.recyclerView.layoutManager = layoutManager

        val arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        val exploreClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(position: Int, it: SectionContentItem, exploreContentResponse: ExploreContentResponse?) {

                when (it.redirectionType) {
                    AppConstants.TYPE_VIDEO -> {
                        val intent = Intent(requireActivity(), VideoActivity2::class.java)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)

                        startActivity(intent)

                    }
                    AppConstants.TYPE_VIDEO_EXPLORE -> {
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/videowithexplore?videoUrl=${it.redirectionResource}&actionFlag=${it.actionFlagCode}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    AppConstants.EXPLORE_IN_APP -> {
                        it.redirectionResource?.let { uri ->
                            when (val redirectionResources = uri.split(",")[0]) {
                                AppConstants.FyperScreen -> {
                                    findNavController().navigate(R.id.navigation_fyper)
                                }
                                AppConstants.JACKPOTTAB -> {
                                    findNavController().navigate(R.id.navigation_jackpot)
                                }
                                AppConstants.CardScreen -> {
                                    findNavController().navigate(R.id.navigation_card)
                                }
                                AppConstants.RewardHistory -> {
                                    findNavController().navigate(R.id.navigation_rewards_history)
                                }
                                AppConstants.ARCADE -> {
                                    findNavController().navigate(R.id.navigation_arcade)
                                }
                                AppConstants.GIFT_VOUCHER -> {
                                    findNavController().navigate(Uri.parse("fypmoney://creategiftcard/${it.redirectionResource}"))
                                }
                                AppConstants.F_Store -> {
                                    findNavController().navigate(R.id.navigation_explore)
                                }
                                AppConstants.REWARDS -> {
                                    findNavController().navigate(R.id.navigation_rewards)
                                }
                                AppConstants.INSIGHTS -> {
                                    findNavController().navigate(R.id.navigation_insights)
                                }

                                else -> {
                                    Utility.deeplinkRedirection(redirectionResources, requireContext())
                                }
                            }
                        }
                    }
                    AppConstants.EXPLORE_IN_APP_WEBVIEW -> {
                        val intent = Intent(requireContext(), ExploreInAppWebview::class.java)
                        intent.putExtra(AppConstants.FROM_WHICH_SCREEN,
                            AppConstants.EXPLORE_IN_APP_WEBVIEW
                        )
                        intent.putExtra(AppConstants.IN_APP_URL, it.redirectionResource)

                        startActivity(intent)
                    }
                    AppConstants.IN_APP_WITH_CARD -> {
                        val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)
                        startActivity(intent)
                    }
                    AppConstants.OFFER_REDIRECTION -> {
                        jackpotViewModel?.callFetchOfferApi(it.redirectionResource)
                    }


                    AppConstants.FEED_TYPE_BLOG -> {
                        jackpotViewModel?.callFetchFeedsApi(it.redirectionResource)
                    }

                    AppConstants.EXT_WEBVIEW -> {
                        if (it.redirectionResource != null) {
                            startActivity(
                                Intent.createChooser(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(it.redirectionResource)
                                    ), getString(R.string.browse_with)
                                )
                            )
                        }


                    }
                    AppConstants.EXPLORE_TYPE_STORIES -> {
                        if (!it.redirectionResource.isNullOrEmpty()) {
                            jackpotViewModel?.callFetchFeedsApi(it.redirectionResource)

                        }

                    }
                    AppConstants.EXPLORE_SECTION_EXPLORE->{
                        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                            it.redirectionResource?.let {
                                param(
                                    FirebaseAnalytics.Param.SCREEN_NAME,
                                    it
                                )
                            }
                            param(FirebaseAnalytics.Param.SCREEN_CLASS, ExploreFragment::class.java.simpleName)
                        }
                        val directions = RewardsJackpotFragmentDirections.actionJackpotToSectionExplore(sectionExploreItem = it,
                                sectionExploreName= exploreContentResponse?.sectionDisplayText
                            )

                        directions.let { it1 -> findNavController().navigate(it1) }
                    }
                    AppConstants.GIFT_VOUCHER -> {
                        findNavController().navigate(Uri.parse("fypmoney://creategiftcard/${it.redirectionResource}"))
                    }
                }


            }
        }
        val scale: Float = requireActivity().resources.displayMetrics.density
        val typeAdapter = ExploreBaseAdapter(
            arrayList,
            requireContext(),
            exploreClickListener2,
            scale
        )
        root.recyclerView.adapter = typeAdapter
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

}