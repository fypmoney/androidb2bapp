package com.fypmoney.view.home.main.home.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.FragmentHomeBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.BROADBAND_RECHARGE_URL
import com.fypmoney.util.AppConstants.FyperScreen
import com.fypmoney.util.AppConstants.NO
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_HOME_SCREEN_BG
import com.fypmoney.util.SharedPrefUtils.Companion.SF_MESSAGE_ON_RECHARGE
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.deeplinkRedirection
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoWithExploreFragment
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.addmoney.NewAddMoneyActivity
import com.fypmoney.view.arcadegames.model.ArcadeType
import com.fypmoney.view.arcadegames.model.checkTheArcadeType
import com.fypmoney.view.contacts.model.CONTACT_ACTIVITY_UI_MODEL
import com.fypmoney.view.contacts.model.ContactActivityActionEvent
import com.fypmoney.view.contacts.model.ContactsActivityUiModel
import com.fypmoney.view.contacts.view.PayToContactsActivity
import com.fypmoney.view.storeoffers.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreAdapter
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.home.main.home.viewmodel.HomeFragmentVM
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.register.fragments.CompleteKYCBottomSheet
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi

@FlowPreview
@ObsoleteCoroutinesApi
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeFragmentVM>(),
    ExploreAdapter.OnFeedItemClickListener {

    private val homeFragmentVM by viewModels<HomeFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var _binding: FragmentHomeBinding

    private val binding get() = _binding

    override fun onTryAgainClicked() {

    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.fragment_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): HomeFragmentVM = homeFragmentVM

    override fun onStart() {
        super.onStart()
        homeFragmentVM.fetchBalance()
        checkForRechargeCashback()
    }

    override fun onResume() {
        super.onResume()
        loadProfile(homeFragmentVM.userProfileUrl)
        showNewMessage()
    }

    private fun loadProfile(url: String?) {
        url?.let {
            loadImage(
                binding.myProfileIv,
                it,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile_img),
                true
            )
        }
    }

    private fun checkForRechargeCashback() {
        SharedPrefUtils.getString(requireContext(), SF_MESSAGE_ON_RECHARGE)?.let {
            if(it.isEmpty()){
                binding.cashbackAmountTv.toInvisible()
            }else{
                binding.cashbackAmountTv.text = it
                binding.cashbackAmountTv.toVisible()
            }
        }
    }
    private fun checkForHomeScreenBackground(){
        SharedPrefUtils.getString(requireContext(), SF_HOME_SCREEN_BG)?.let {
            if(it.isEmpty()){
                binding.clMainLayout.setBackgroundColor(resources.getColor(R.color.white))
            }else{
                Glide.with(requireContext()).load(it)
                    .into(binding.ivBackgroundImage)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
        checkForHomeScreenBackground()
        setupRecyclerView()
        //setRecyclerView(_binding)
        setObserver()
        setUpObserver()
        homeFragmentVM.callToAction()
        checkForErrorNotice()
        rechargeVisbility()
        binding.help.setOnClickListener {
            callFreshChat(requireContext())
        }

    }

    private fun rechargeVisbility() {
        SharedPrefUtils.getString(requireContext(),SharedPrefUtils.SF_SHOW_RECHARGE_IN_HOME_SCREEN)?.let {
            if(it==YES){
                binding.rechargeSectionCl.toVisible()
            }else if(it==NO){
                binding.rechargeSectionCl.toGone()
            }
        }
    }

    private fun showNewMessage() {
        if (homeFragmentVM.isUnreadNotificationAvailable.isNullOrEmpty()) {
            binding.newNotification.toGone()
        } else {
            binding.newNotification.toVisible()
        }
    }

    private fun checkForErrorNotice() {
        PockketApplication.homeScreenErrorMsg?.let {
            if (it.isNotEmpty()) {
                binding.noticeAlertFl.toVisible()
                binding.noticeTv.text = it
            } else {
                binding.noticeAlertFl.toGone()
            }

        }
    }


    private fun setupRecyclerView() {
        with(binding.callToActionRv) {
            adapter = CallToActionAdapter(viewLifecycleOwner, onCallToActionClicked = {
                trackr { it1 ->
                    it1.name = TrackrEvent.home_action_click
                    it1.add(TrackrField.action_content_id, it.id)
                }
                openExploreFeaturesCallToAction(
                    it.redirectionType,
                    it.redirectionResource,
                )

            })
        }
    }

    private fun setUpObserver() {
        homeFragmentVM.event.observe(viewLifecycleOwner) {
            handelEvents(it)
        }
        homeFragmentVM.state.observe(viewLifecycleOwner) {
            handelState(it)
        }
    }

    private fun handelState(it: HomeFragmentVM.HomeFragmentState?) {
        when(it){
            HomeFragmentVM.HomeFragmentState.ErrorBalanceState -> {
                //binding.loadingBalanceHdp.toGone()

            }
            HomeFragmentVM.HomeFragmentState.LoadingBalanceState -> {
                binding.loadingBalanceHdp.clearAnimation()
                binding.walletBalanceTv.toGone()
                binding.lowBalanceTv.toGone()
                binding.loadingBalanceHdp.toVisible()
            }
            is HomeFragmentVM.HomeFragmentState.SuccessBalanceState -> {
                binding.loadingBalanceHdp.clearAnimation()
                binding.loadingBalanceHdp.toGone()
                binding.walletBalanceTv.toVisible()
                binding.walletBalanceTv.text = """${getString(R.string.Rs)}${Utility.convertToRs(it.balance.toString())}"""
            }
            is HomeFragmentVM.HomeFragmentState.LowBalanceAlertState -> {
                if(it.balanceIsLow){
                    binding.lowBalanceTv.toVisible()
                }else{
                    binding.lowBalanceTv.toGone()
                }
            }
            is HomeFragmentVM.HomeFragmentState.SuccessCallToActionState -> {
                binding.shimmerLayout.toGone()
                if (it.callToActionList.isNotEmpty()) {
                    binding.callToActionTv.text = it.sectionTitle
                    binding.callToActionRv.toVisible()
                    binding.callToActionCl.toVisible()
                }else{
                    binding.callToActionRv.toGone()
                    binding.callToActionCl.toGone()
                }

                (binding.callToActionRv.adapter as CallToActionAdapter).submitList(it.callToActionList)
            }
            HomeFragmentVM.HomeFragmentState.LoadingCallToActionState -> {

            }
            /*HomeFragmentVM.HomeFragmentState.ShowLoadMoneySheetState -> {
                val loadMoneyBottomSheet = LoadMoneyBottomSheet()
                loadMoneyBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                loadMoneyBottomSheet.show(childFragmentManager, "LoadMoneySheet")
            }*/
            HomeFragmentVM.HomeFragmentState.ShowLoadMoneySheetState -> {

            }
            null -> {}
        }
    }

    private fun handelEvents(it: HomeFragmentVM.HomeFragmentEvent?) {
        when(it){
            HomeFragmentVM.HomeFragmentEvent.ViewCardDetails -> {
                findNavController().navigate(R.id.navigation_card)
            }
            HomeFragmentVM.HomeFragmentEvent.AddAction -> {
                Utility.getCustomerDataFromPreference()?.let {
                    if(it.postKycScreenCode.isNullOrEmpty()){
                        val completeKYCBottomSheet = CompleteKYCBottomSheet(completeKycClicked = {
                            val intent = Intent(requireActivity(), PanAdhaarSelectionActivity::class.java)
                            startActivity(intent)
                        })
                        completeKYCBottomSheet.dialog?.window?.setBackgroundDrawable(
                            ColorDrawable(
                                Color.RED)
                        )
                        completeKYCBottomSheet.show(childFragmentManager, "Completekyc")
                    }else{
                        val intent = Intent(requireActivity(), NewAddMoneyActivity::class.java).apply {  }
                        startActivity(intent)
                    }
                }

            }
            HomeFragmentVM.HomeFragmentEvent.PayAction -> {
                Utility.getCustomerDataFromPreference()?.let {
                    if(it.postKycScreenCode.isNullOrEmpty()){
                        val completeKYCBottomSheet = CompleteKYCBottomSheet(completeKycClicked = {
                            val intent = Intent(requireActivity(), PanAdhaarSelectionActivity::class.java)
                            startActivity(intent)
                        })
                        completeKYCBottomSheet.dialog?.window?.setBackgroundDrawable(
                            ColorDrawable(
                                Color.RED)
                        )
                        completeKYCBottomSheet.show(childFragmentManager, "Completekyc")
                    }else{
                        val intent = Intent(requireActivity(), PayToContactsActivity::class.java)
                        intent.putExtra(CONTACT_ACTIVITY_UI_MODEL, ContactsActivityUiModel(toolBarTitle = getString(R.string.pay),
                            showLoadingBalance = true,contactClickAction = ContactActivityActionEvent.PayToContact))
                        startActivity(intent)
                    }
                }


            }
            HomeFragmentVM.HomeFragmentEvent.UpiScanAction -> {
                val upiComingSoonBottomSheet = UpiComingSoonBottomSheet()
                upiComingSoonBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                upiComingSoonBottomSheet.show(childFragmentManager, "UpiComingSoonBottomSheet")

            }
            HomeFragmentVM.HomeFragmentEvent.BroadbandRechargeEvent -> {
                val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, BROADBAND_RECHARGE_URL)
                startActivity(intent)
            }
            HomeFragmentVM.HomeFragmentEvent.DthRechargeEvent ->{
                /*val directions =
                    HomeFragmentDirections.actionRechargeHome()
                directions.let { it1 -> findNavController().navigate(it1) }*/
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
            is HomeFragmentVM.HomeFragmentEvent.PostpaidRechargeEvent -> {
                /*if(findNavController().currentDestination?.id == R.id.navigation_home){
                    val directions =
                        HomeFragmentDirections.actionRechargeScreen(rechargeType = AppConstants.POSTPAID)
                    directions.let { it1 -> findNavController().navigate(it1) }
                }*/
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
            is HomeFragmentVM.HomeFragmentEvent.PrepaidRechargeEvent -> {
                /*if(findNavController().currentDestination?.id == R.id.navigation_home){
                    val directions =
                        HomeFragmentDirections.actionRechargeScreen(rechargeType = AppConstants.PREPAID)
                    directions.let { it1 -> findNavController().navigate(it1) }
                }*/
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
            null -> {

            }
            HomeFragmentVM.HomeFragmentEvent.NotificationClicked -> {
                startActivity(Intent(requireActivity(), NotificationView::class.java))
            }
            HomeFragmentVM.HomeFragmentEvent.ProfileClicked -> {
                startActivity(Intent(requireActivity(), UserProfileView::class.java))

            }
        }
    }

    private fun setObserver() {
        homeFragmentVM.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->
            setRecyclerView(_binding, list)
        }


        homeFragmentVM.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        homeFragmentVM.feedDetail.observe(
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
        root: FragmentHomeBinding,
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
            override fun onItemClicked(position: Int, sectionContentItem: SectionContentItem,exploreContentResponse: ExploreContentResponse?) {
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
            Color.parseColor(homeFragmentVM.textColor.value)
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
                findNavController().navigate(Uri.parse("https://www.fypmoney.in/videowithexplore?videoUrl=${redirectionResource}&actionFlag=${sectionContentItem.actionFlagCode}"),
                    navOptions {
                        anim {
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_righ
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                        }
                    })
            }
            AppConstants.EXPLORE_SECTION_EXPLORE -> {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    sectionContentItem.redirectionResource?.let {
                        param(FirebaseAnalytics.Param.SCREEN_NAME,
                            it
                        )
                    }
                    param(FirebaseAnalytics.Param.SCREEN_CLASS, HomeFragment::class.java.simpleName)
                }
                if(findNavController().currentDestination?.id==R.id.navigation_home){
                    val directions = HomeFragmentDirections.actionHomeToSectionExplore(sectionExploreItem = sectionContentItem,
                        sectionExploreName= exploreContentResponse?.sectionDisplayText)
                    directions.let { it1 -> findNavController().navigate(it1) }
                }
            }
            AppConstants.EXPLORE_IN_APP -> {
                redirectionResource?.let { uri ->

                    when (val redirectionResources = uri.split(",")[0]) {
                        FyperScreen -> {
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
                            findNavController().navigate(Uri.parse("fypmoney://creategiftcard/${redirectionResources}"))
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
                                deeplinkRedirection(
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
                homeFragmentVM.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                homeFragmentVM.callFetchFeedsApi(redirectionResource)
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
                    homeFragmentVM.callFetchFeedsApi(redirectionResource)

                }

            }
            AppConstants.GIFT_VOUCHER -> {
                findNavController().navigate(Uri.parse("fypmoney://creategiftcard/${redirectionResource}"),
                    navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
            }

            AppConstants.LEADERBOARD -> {
                findNavController().navigate(Uri.parse("https://www.fypmoney.in/leaderboard/${redirectionResource}"),
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
                val type = sectionContentItem.rfu1?.let { rfu ->
                    sectionContentItem.redirectionResource?.let { it1 ->
                        checkTheArcadeType(
                            arcadeType = rfu,
                            productCode = it1
                        )
                    }
                }
                when (type) {
                    ArcadeType.NOTypeFound -> TODO()
                    is ArcadeType.SCRATCH_CARD -> TODO()
                    is ArcadeType.SLOT -> {
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/slot_machine/${type.productCode}/${null}"),
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
                        findNavController().navigate(Uri.parse("https://www.fypmoney.in/spinwheel/${type.productCode}/${null}"),
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
                    null -> {}
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

    private fun openExploreFeaturesCallToAction(
        redirectionType: String?,
        redirectionResource: String?
    ) {
        when (redirectionType) {
            AppConstants.EXPLORE_IN_APP -> {
                redirectionResource?.let { uri ->

                    val redirectionResources = uri?.split(",")?.get(0)
                    if (redirectionResources == FyperScreen) {
                        findNavController().navigate(R.id.navigation_fyper)
                    } else if (redirectionResources == AppConstants.JACKPOTTAB) {
                        findNavController().navigate(R.id.navigation_jackpot)
                    } else if (redirectionResources == AppConstants.CardScreen) {
                        findNavController().navigate(R.id.navigation_card)
                    } else if (redirectionResources == AppConstants.RewardHistory) {
                        findNavController().navigate(R.id.navigation_rewards_history)
                    } else if (redirectionResources == AppConstants.ARCADE) {
                        findNavController().navigate(R.id.navigation_arcade)
                    } else if (redirectionResources == AppConstants.RechargeHomeScreen) {
                        findNavController().navigate(R.id.navigation_enter_mobile_number_recharge)
                    } else {
                        redirectionResources.let { it1 ->
                            deeplinkRedirection(
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
                homeFragmentVM.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                homeFragmentVM.callFetchFeedsApi(redirectionResource)

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
                    homeFragmentVM.callFetchFeedsApi(redirectionResource)

                }

            }


            /*AppConstants.EXPLORE_SECTION_EXPLORE->{
                val directions = exploreContentResponse?.sectionDisplayText?.let { it1 ->
                    ExploreFragmentDirections.actionExploreSectionExplore(sectionExploreItem = sectionContentItem,
                        sectionExploreName= it1
                    )
                }
                directions?.let { it1 -> findNavController().navigate(it1) }
            }*/
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

    override fun onFeedClick(position: Int, feedDetails: SectionContentItem) {

    }


}