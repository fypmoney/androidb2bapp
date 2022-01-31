package com.fypmoney.view.home.main.home.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentHomeBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.FyperScreen
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.deeplinkRedirection
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoActivityWithExplore
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.activity.ChooseInterestHomeView
import com.fypmoney.view.activity.ContactListView
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreAdapter
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.view.ExploreFragmentDirections
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.home.main.home.viewmodel.HomeFragmentVM
import com.fypmoney.view.home.main.homescreen.view.LoadMoneyBottomSheet
import com.fypmoney.view.register.adapters.OffersHomeAdapter
import com.fypmoney.view.storeoffers.ListOfferClickListener
import com.fypmoney.view.storeoffers.OffersScreen
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeFragmentVM>(),
    ExploreAdapter.OnFeedItemClickListener {

    private var typeAdapter: OffersHomeAdapter? = null
    private val homeFragmentVM by viewModels<HomeFragmentVM> {
        defaultViewModelProviderFactory
    }
    private lateinit var _binding: FragmentHomeBinding
    private var itemsArrayList: ArrayList<offerDetailResponse> = ArrayList()

    private val binding get() = _binding

    override fun onTryAgainClicked() {

    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.fragment_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): HomeFragmentVM  = homeFragmentVM

    override fun onStart() {
        super.onStart()
        homeFragmentVM.callgetOffer()
        homeFragmentVM.fetchBalance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
        setupRecyclerView()
        setRecyclerView(_binding)
        setObserver()
        setUpObserver()
        homeFragmentVM.callToAction()
        checkForErrorNotice()

    }

    private fun checkForErrorNotice() {
        PockketApplication.homeScreenErrorMsg?.let{
            if(it.isNotEmpty()){
                binding.noticeAlertFl.toVisible()
                binding.noticeTv.text = it
            }else{
                binding.noticeAlertFl.toGone()
            }

        }
    }


    private fun setupRecyclerView() {
        /*with(binding.quickActionRv) {
            adapter = QuickActionAdapter(viewLifecycleOwner, onQuickActionClicked = {
                when(it.id){
                    HomeFragmentVM.QuickActionEvent.AddAction -> {
                        val intent = Intent(requireActivity(), AddMoneyView::class.java)
                        startActivity(intent)
                    }
                    HomeFragmentVM.QuickActionEvent.OfferAction -> {
                        val intent = Intent(requireActivity(), OffersScreen::class.java)
                        startActivity(intent)
                    }
                    HomeFragmentVM.QuickActionEvent.PayAction -> {
                        val intent = Intent(requireActivity(), ContactListView::class.java)
                        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.PAY)
                        startActivity(intent)
                    }
                }
            })
        }*/
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
        homeFragmentVM.event.observe(viewLifecycleOwner,{
            handelEvents(it)
        })
        homeFragmentVM.state.observe(viewLifecycleOwner,{
            handelState(it)
        })
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
            HomeFragmentVM.HomeFragmentState.ShowLoadMoneySheetState -> {
                val loadMoneyBottomSheet = LoadMoneyBottomSheet()
                loadMoneyBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                loadMoneyBottomSheet.show(childFragmentManager, "LoadMoneySheet")
            }
            null -> TODO()
        }
    }

    private fun handelEvents(it: HomeFragmentVM.HomeFragmentEvent?) {
        when(it){
            HomeFragmentVM.HomeFragmentEvent.ViewCardDetails -> {
                findNavController().navigate(R.id.navigation_card)

            }
            HomeFragmentVM.HomeFragmentEvent.AddAction -> {
                val intent = Intent(requireActivity(), AddMoneyView::class.java)
                startActivity(intent)
            }
            HomeFragmentVM.HomeFragmentEvent.PayAction -> {
                val intent = Intent(requireActivity(), ContactListView::class.java)
                intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.PAY)
                startActivity(intent)
            }
            HomeFragmentVM.HomeFragmentEvent.UpiScanAction -> {
                val upiComingSoonBottomSheet = UpiComingSoonBottomSheet()
                upiComingSoonBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                upiComingSoonBottomSheet.show(childFragmentManager, "UpiComingSoonBottomSheet")
            }

        }
    }

    private fun setObserver() {
        homeFragmentVM?.rewardHistoryList.observe(
            viewLifecycleOwner,
            { list ->

                setRecyclerView(_binding, list)
            })
        _binding.toInterestScreen.setOnClickListener {
            var intent = Intent(requireContext(), ChooseInterestHomeView::class.java)

            startActivity(intent)
        }
        homeFragmentVM.offerList.observe(viewLifecycleOwner, {
            if (it != null) {
                itemsArrayList.clear()
                itemsArrayList.addAll(it)

                if (itemsArrayList.size > 0) {

                    itemsArrayList.add(offerDetailResponse())
                    //set Offers  for you title dynamic
                    _binding.shimmerLayoutLightening.visibility = View.GONE
                    if(!itemsArrayList[0].rfu2.isNullOrEmpty()){
                        _binding.lighteningDealsTitle.text = itemsArrayList[0].rfu2
                    }
                    _binding.lighteningDealsTitle.visibility = View.VISIBLE

                    _binding.lighteningDealsRv.visibility = View.VISIBLE
                    _binding.toInterestScreen.visibility = View.GONE
                } else {
                    _binding.shimmerLayoutLightening.visibility = View.GONE
                    _binding.toInterestScreen.visibility = View.VISIBLE
//                _binding.lighteningDealsTitle.visibility = View.VISIBLE
                    _binding.lighteningDealsRv.visibility = View.GONE
                }
                typeAdapter?.notifyDataSetChanged()
            } else {
                _binding.shimmerLayoutLightening.visibility = View.GONE
                _binding.toInterestScreen.visibility = View.VISIBLE
//                _binding.lighteningDealsTitle.visibility = View.VISIBLE
                _binding.lighteningDealsRv.visibility = View.GONE
            }
        })

        homeFragmentVM?.openBottomSheet.observe(
            viewLifecycleOwner,
            { list ->

                callOfferDetailsSheeet(list[0])
            })

        homeFragmentVM?.feedDetail.observe(
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

    }

    private fun setRecyclerView(root: FragmentHomeBinding) {
        var itemClickListener2 = object : ListOfferClickListener {
            override fun onItemClicked(pos: offerDetailResponse, position: String) {
                if (position == "middle") {
                    callOfferDetailsSheeet(pos)
                } else {
                    var intent = Intent(requireContext(), OffersScreen::class.java)
                    intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.OfferScreen)
                    startActivity(intent)
                }

            }
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        root.lighteningDealsRv.layoutManager = layoutManager

        typeAdapter = OffersHomeAdapter(itemsArrayList, requireContext(), itemClickListener2)
        root.lighteningDealsRv.adapter = typeAdapter

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

        var arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        var exploreClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(position: Int, it1: SectionContentItem,exploreContentResponse: ExploreContentResponse?) {
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
            Color.BLACK
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
                intent.putExtra(ARG_WEB_URL_TO_OPEN, redirectionResource)
                intent.putExtra(AppConstants.ACTIONFLAG, sectionContentItem.actionFlagCode)
                startActivity(intent)
            }
            AppConstants.EXPLORE_SECTION_EXPLORE -> {
                val directions = exploreContentResponse?.sectionDisplayText?.let { it1 ->
                    ExploreFragmentDirections.actionExploreSectionExplore(
                        sectionExploreItem = sectionContentItem,
                        sectionExploreName = it1
                    )
                }
                directions?.let { it1 -> findNavController().navigate(it1) }
            }
            AppConstants.EXPLORE_IN_APP -> {
                redirectionResource?.let { uri ->

                    val redirectionResources = uri?.split(",")?.get(0)
                    if (redirectionResources == FyperScreen) {
                        findNavController().navigate(R.id.navigation_fyper)
                    } else if (redirectionResources == AppConstants.JACKPOTTAB) {
                        findNavController().navigate(R.id.navigation_jackpot)
                    } else if (redirectionResources == AppConstants.CardScreen) {
                        findNavController().navigate(R.id.navigation_card)
                    }else if (redirectionResources == AppConstants.RewardHistory) {
                        findNavController().navigate(R.id.navigation_rewards_history)
                    }else if (redirectionResources == AppConstants.ARCADE) {
                        findNavController().navigate(R.id.navigation_arcade)
                    } else {
                        redirectionResources?.let { it1 ->
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
                    } else {
                        redirectionResources?.let { it1 ->
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
        }
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
        TODO("Not yet implemented")
    }


}