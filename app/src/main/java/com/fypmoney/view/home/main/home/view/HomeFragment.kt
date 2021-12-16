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
import com.fypmoney.BR
import com.fypmoney.R
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
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.AddMoneyView
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
import com.fypmoney.view.home.main.home.adapter.CallToActionAdapter
import com.fypmoney.view.home.main.home.adapter.QuickActionAdapter
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
        homeFragmentVM.fetchBalance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()
        setupRecyclerView()
        setUpObserver()
        homeFragmentVM.prepareQuickActionList()
        setObserver()
        setRecyclerView(_binding)
    }



    private fun setupRecyclerView() {
        with(binding.quickActionRv) {
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
        }
        with(binding.callToActionRv) {
            adapter = CallToActionAdapter(viewLifecycleOwner, onCallToActionClicked = {
                val redirectionResources = it.redirectionResource?.split(",")?.get(0)
                if (redirectionResources == FyperScreen) {
                    findNavController().navigate(R.id.navigation_fyper)
                } else if (redirectionResources == AppConstants.JACKPOTTAB) {
                    findNavController().navigate(R.id.navigation_rewards)
                } else if (redirectionResources == AppConstants.CardScreen) {
                    findNavController().navigate(R.id.navigation_card)
                } else {
                    redirectionResources?.let { it1 -> deeplinkRedirection(it1, requireContext()) }
                }
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
                binding.callToActionRv.toVisible()
                (binding.callToActionRv.adapter as CallToActionAdapter).submitList(it.callToActionList)
            }
            HomeFragmentVM.HomeFragmentState.LoadingCallToActionState -> TODO()
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
            is HomeFragmentVM.HomeFragmentEvent.QuickActionListReady -> {
                (binding.quickActionRv.adapter as QuickActionAdapter).submitList(it.quickActionList)
                //homeFragmentVM.prepareCallToActionList()
                homeFragmentVM.callToAction()
            }
            HomeFragmentVM.HomeFragmentEvent.ViewCardDetails -> {
                findNavController().navigate(R.id.navigation_card)

            }
            null -> TODO()
        }
    }

    private fun setObserver() {
        homeFragmentVM?.rewardHistoryList.observe(
            viewLifecycleOwner,
            { list ->

                setRecyclerView(_binding, list)
            })
        homeFragmentVM.offerList.observe(viewLifecycleOwner, {
            itemsArrayList.clear()
            itemsArrayList.addAll(it)
            if (itemsArrayList.size > 0) {
                _binding.shimmerLayoutLightening.visibility = View.GONE
                _binding.lighteningDealsTitle.visibility = View.VISIBLE
                _binding.lighteningDealsRv.visibility = View.VISIBLE
            }
            typeAdapter?.notifyDataSetChanged()
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
                callOfferDetailsSheeet(pos)
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
            override fun onItemClicked(position: Int, it: SectionContentItem) {

                when (it.redirectionType) {
                    AppConstants.EXPLORE_IN_APP -> {
                        it.redirectionResource?.let { uri ->

                            val screen = uri.split(",")[0]
                            if (screen == AppConstants.StoreScreen || screen == AppConstants.CardScreen || screen == AppConstants.FEEDSCREEN || screen == AppConstants.FyperScreen) {
//                                tabchangeListner.tabchange(0, screen)
                            } else {

                                Utility.deeplinkRedirection(screen, requireContext())
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
                        intent.putExtra(AppConstants.IN_APP_URL, it.redirectionResource)

                        startActivity(intent)
                    }
                    AppConstants.IN_APP_WITH_CARD -> {

                        val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
//        intent.putExtra(AppConstants.EXPLORE_RESPONSE, feedDetails)

                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)
                        startActivity(intent)
                    }
                    AppConstants.OFFER_REDIRECTION -> {
                        homeFragmentVM.callFetchOfferApi(it.redirectionResource)

                    }


                    AppConstants.FEED_TYPE_BLOG -> {
                        homeFragmentVM.callFetchFeedsApi(it.redirectionResource)

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
//

                            homeFragmentVM.callFetchFeedsApi(it.redirectionResource)

                        }

                    }
                }


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