package com.fypmoney.view.rewardsAndWinnings.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentRewardsOverviewBinding
import com.fypmoney.extension.toGone
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
import com.fypmoney.view.arcadegames.model.ArcadeType
import com.fypmoney.view.arcadegames.model.checkTheArcadeType
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


class RewardsOverviewFragment :
    BaseFragment<FragmentRewardsOverviewBinding, RewardsAndVM>(),
    FeedsAdapter.OnFeedItemClickListener {

    private var mLastClickTime: Long = 0

    companion object {
        var page = 0
        fun newInstance(): RewardsOverviewFragment {
            return RewardsOverviewFragment()
        }

    }

    private var mViewBinding: FragmentRewardsOverviewBinding? = null
    private var mViewmodel: RewardsAndVM? = null

//    val startForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == 99) {
//                mViewmodel?.totalMynts?.postValue(true)
//            }
//
//        }
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

    override fun onFeedClick(position: Int, feedDetails: FeedDetails) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1200) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (feedDetails.displayCard) {
            AppConstants.FEED_TYPE_DEEPLINK -> {
                feedDetails.action?.url?.let {
                    Utility.deeplinkRedirection(it.split(",")[0], requireContext())

                }
            }
            AppConstants.FEED_TYPE_INAPPWEB2 -> {
                intentToActivity(
                    UserFeedsInAppWebview::class.java,
                    feedDetails,
                    AppConstants.FEED_TYPE_INAPPWEB
                )
            }
            AppConstants.FEED_TYPE_EXTWEBVIEW2 -> {
                startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(feedDetails.action?.url)
                        ), getString(R.string.browse_with)
                    )
                )

            }
            AppConstants.FEED_TYPE_INAPPWEB -> {
                intentToActivity(
                    UserFeedsInAppWebview::class.java,
                    feedDetails,
                    AppConstants.FEED_TYPE_INAPPWEB
                )
            }
            AppConstants.FEED_TYPE_BLOG -> {
                intentToActivity(
                    UserFeedsDetailView::class.java,
                    feedDetails,
                    AppConstants.FEED_TYPE_BLOG
                )
            }
            AppConstants.FEED_TYPE_EXTWEBVIEW -> {
                startActivity(
                    Intent.createChooser(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(feedDetails.action?.url)
                        ), getString(R.string.browse_with)
                    )
                )

            }
            AppConstants.FEED_TYPE_STORIES -> {
                if (!feedDetails.resourceArr.isNullOrEmpty()) {
                    callDiduKnowBottomSheet(feedDetails.resourceArr)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()


        setBackgrounds()

//        mViewBinding?.loadingGoldenCards?.visibility = View.VISIBLE
//        mViewBinding?.loadingAmountMynts?.visibility = View.VISIBLE
//        mViewBinding?.loadingAmountHdp?.visibility = View.VISIBLE

        mViewBinding?.loadingMynts?.visibility = View.VISIBLE
        mViewBinding?.loadingTickets?.visibility = View.VISIBLE
        mViewBinding?.loadingCash?.visibility = View.VISIBLE

        setRecyclerView()
        mViewmodel?.let { observeInput(it) }

        mViewBinding?.transactionHistoryAiv?.setOnClickListener {
            findNavController().navigate(R.id.navigation_rewards_history)
        }

        mViewBinding?.chipCashView?.setOnClickListener {
            val intent = Intent(requireContext(), CashBackWonHistoryActivity::class.java)
            startActivity(intent)
        }

        mViewBinding?.chipMyntsView?.setOnClickListener {
            findNavController().navigate(R.id.navigation_rewards_history, null, navOptions {
                anim {
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_righ
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                }
            })
        }

        mViewBinding?.chipTicketsView?.setOnClickListener {
            findNavController().navigate(R.id.navigation_notification_settings_fragment, null, navOptions {
                anim {
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_righ
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                }
            })
        }

        mViewBinding?.chipCouponActiveView?.setOnClickListener {
            findNavController().navigate(R.id.navigation_active_coupon_fragment, null, navOptions {
                anim {
                    popEnter = R.anim.slide_in_left
                    popExit = R.anim.slide_out_righ
                    enter = R.anim.slide_in_right
                    exit = R.anim.slide_out_left
                }
            })
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("arcade_is_played")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                if (result) {
                    mViewmodel?.callTotalJackpotCards()
                    mViewmodel?.callRewardSummary()
                    mViewmodel?.callTotalRewardsEarnings()
                }
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

        val arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        val exploreClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(
                position: Int,
                sectionContentItem: SectionContentItem,
                exploreContentResponse: ExploreContentResponse?
            ) {
                trackr {
                    it.name = TrackrEvent.home_explore_click
                    it.add(TrackrField.explore_content_id, sectionContentItem.id)
                }
                openExploreFeatures(
                    sectionContentItem.redirectionType,
                    sectionContentItem.redirectionResource,
                    sectionContentItem,
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
                val directions = RewardsOverviewFragmentDirections.actionExploreSectionExplore(
                    sectionExploreItem = sectionContentItem,
                    sectionExploreName = exploreContentResponse?.sectionDisplayText
                )
                directions.let { it1 -> findNavController().navigate(it1) }
            }
            AppConstants.EXPLORE_IN_APP -> {

                redirectionResource?.let { uri ->

                    val redirectionResources = uri.split(",")[0]
                    when (redirectionResources) {
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
                            findNavController().navigate(
                                Uri.parse("fypmoney://creategiftcard/${redirectionResource}"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }
                        AppConstants.ARCADE -> {
                            findNavController().navigate(
                                Uri.parse("https://www.fypmoney.in/leaderboard/${redirectionResource}"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
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
                        AppConstants.PREPAID_RECHARGE_REDIRECTION->{
                            findNavController().navigate(Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.PREPAID}"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }

                        AppConstants.POSTPAID_RECHARGE_REDIRECTION->{
                            findNavController().navigate(Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.POSTPAID}"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }

                        AppConstants.DTH_RECHARGE_REDIRECTION->{
                            findNavController().navigate(Uri.parse("https://www.fypmoney.in/recharge/dth"),
                                navOptions {
                                    anim {
                                        popEnter = R.anim.slide_in_left
                                        popExit = R.anim.slide_out_righ
                                        enter = R.anim.slide_in_right
                                        exit = R.anim.slide_out_left
                                    }
                                })
                        }
                        else -> {
                            redirectionResources.let { it1 ->
                                Utility.deeplinkRedirection(
                                    it1,
                                    requireContext()
                                )
                            }
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

            AppConstants.LEADERBOARD -> {
                findNavController().navigate(
                    Uri.parse("https://www.fypmoney.in/leaderboard/${redirectionResource}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
            AppConstants.GIFT_VOUCHER -> {
                findNavController().navigate(
                    Uri.parse("fypmoney://creategiftcard/${redirectionResource}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
            "ARCADE" -> {
                val type = sectionContentItem.rfu1?.let {
                    redirectionResource?.let { it1 ->
                        checkTheArcadeType(
                            arcadeType = it,
                            productCode = it1
                        )
                    }
                }
                when (type) {
                    ArcadeType.NOTypeFound -> {

                    }
                    is ArcadeType.SCRATCH_CARD -> {Utility.showToast("Scratch card")}

                    is ArcadeType.SLOT -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/slot_machine/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    is ArcadeType.SPIN_WHEEL -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/spinwheel/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    is ArcadeType.TREASURE_BOX -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/rotating_treasure/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }

                    is ArcadeType.BRANDED_COUPONS -> {
                        findNavController().navigate(
                            Uri.parse("https://www.fypmoney.in/branded_coupons/${type.productCode}/${null}"),
                            navOptions {
                                anim {
                                    popEnter = R.anim.slide_in_left
                                    popExit = R.anim.slide_out_righ
                                    enter = R.anim.slide_in_right
                                    exit = R.anim.slide_out_left
                                }
                            })
                    }
                    null -> {

                    }
                }
            }

            AppConstants.PREPAID_RECHARGE_REDIRECTION->{
                findNavController().navigate(Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.PREPAID}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }

            AppConstants.POSTPAID_RECHARGE_REDIRECTION->{
                findNavController().navigate(Uri.parse("https://www.fypmoney.in/recharge/${AppConstants.POSTPAID}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }

            AppConstants.DTH_RECHARGE_REDIRECTION->{
                findNavController().navigate(Uri.parse("https://www.fypmoney.in/recharge/dth"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
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

//        viewModel.totalMynts.observe(viewLifecycleOwner){
//            mViewBinding?.tvRewardsMyntsCount?.text = it.toString()
//        }

        viewModel.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->

            mViewBinding?.let { setRecyclerView(it, list) }
        }

        viewModel.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->

            if (list.isNotEmpty()) {
                callOfferDetailsSheeet(list[0])
            }
        }

        viewModel.feedDetail.observe(
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

        viewModel.totalRewardsResponse.observe(
            viewLifecycleOwner
        ) { list ->
//            mViewBinding?.loadingAmountHdp?.clearAnimation()
//            mViewBinding?.loadingAmountHdp?.visibility = View.GONE

            mViewBinding?.loadingCash?.clearAnimation()
            mViewBinding?.loadingCash?.visibility = View.INVISIBLE

            mViewBinding?.tvRewardCashCount?.text =
                getString(R.string.rupee_symbol) + Utility.convertToRs("${list.amount}")
//            mViewBinding?.totalRefralWonValueTv?.text =
//                getString(R.string.rupee_symbol) + Utility.convertToRs("${list.amount}")

        }

        viewModel.rewardSummaryStatus.observe(
            viewLifecycleOwner
        ) { list ->
//            mViewBinding?.loadingAmountMynts?.clearAnimation()
//            mViewBinding?.loadingAmountMynts?.visibility = View.GONE

            mViewBinding?.loadingMynts?.clearAnimation()
            mViewBinding?.loadingMynts?.visibility = View.INVISIBLE

            if (list.totalPoints != null) {
//                mViewBinding?.totalMyntsWonValueTv?.text =
//                    String.format("%.0f", list.remainingPoints)

                mViewBinding?.tvRewardsMyntsCount?.text =
                    String.format("%.0f", list.remainingPoints)
            }


        }

//        viewModel.totalJackpotAmount.observe(
//            viewLifecycleOwner
//        ) { list ->
//
//            mViewBinding?.loadingTickets?.clearAnimation()
//            mViewBinding?.loadingTickets?.visibility = View.INVISIBLE
//
//            if (list.count != null) {
//                mViewBinding?.tvRewardsTicketsCount?.text =
//                    "${list.count}"
//            }
//
//        }

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is RewardsAndVM.RewardsTicket.Error -> {

                }
                is RewardsAndVM.RewardsTicket.Success -> {
                    mViewBinding?.loadingTickets?.clearAnimation()
                    mViewBinding?.loadingTickets?.visibility = View.INVISIBLE

                    mViewBinding?.tvRewardsTicketsCount?.text = "${it.totalTickets}"
                }
                is RewardsAndVM.RewardsTicket.Loading -> {

                }
            }
        }

        viewModel.stateCouponCount.observe(viewLifecycleOwner) {
            handleCouponCountState(it)
        }

    }

    private fun handleCouponCountState(it: RewardsAndVM.CouponCountState?) {
        when (it) {

            is RewardsAndVM.CouponCountState.CouponCountSuccess -> {
                mViewBinding?.loadingActiveBrandedCoupon?.clearAnimation()
                mViewBinding?.loadingActiveBrandedCoupon?.toGone()

                mViewBinding?.tvBrandedActiveCouponsCount?.text = it.amount?.toString()

            }
            is RewardsAndVM.CouponCountState.Error -> {}

            RewardsAndVM.CouponCountState.Loading -> {}

            null -> {}
        }
    }

    private fun setBackgrounds() {
        mViewBinding?.let {
            setBackgroundDrawable(
                it.chipMyntsView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                68f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipTicketsView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                68f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipCashView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                68f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipCouponActiveView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                68f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

        }
    }

    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        val bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
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