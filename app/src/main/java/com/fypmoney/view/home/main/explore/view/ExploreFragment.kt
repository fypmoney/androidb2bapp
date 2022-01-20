package com.fypmoney.view.home.main.explore.view

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
import com.fypmoney.databinding.FragmentExploreBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.EXPLORE_IN_APP_WEBVIEW
import com.fypmoney.util.AppConstants.IN_APP_WITH_CARD
import com.fypmoney.util.AppConstants.OFFER_REDIRECTION
import com.fypmoney.util.Utility
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreAdapter
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN

class ExploreFragment : BaseFragment<FragmentExploreBinding, ExploreFragmentVM>(),
    ExploreAdapter.OnFeedItemClickListener {

    private val exploreFragmentVM by viewModels<ExploreFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var _binding: FragmentExploreBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
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
    override fun getLayoutId(): Int  = R.layout.fragment_explore

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): ExploreFragmentVM = exploreFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = getViewDataBinding()


        setObserver()

    }

    private fun setObserver() {
        exploreFragmentVM?.rewardHistoryList.observe(
            viewLifecycleOwner,
            { list ->

                setRecyclerView(_binding, list)
            })
        exploreFragmentVM?.openBottomSheet.observe(
            viewLifecycleOwner,
            { list ->

                callOfferDetailsSheeet(list[0])
            })

        exploreFragmentVM?.feedDetail.observe(
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

    private fun setRecyclerView(
        root: FragmentExploreBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.shimmerLayout.visibility = View.GONE
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rvExplore.layoutManager = layoutManager

        var arrayList: ArrayList<ExploreContentResponse> = ArrayList()
        list.forEach { item ->
            if (item.sectionContent?.size!! > 0) {
                arrayList.add(item)
            }
        }
        var exploreClickListener2 = object : ExploreItemClickListener {
            override fun onItemClicked(position: Int, it: SectionContentItem,exploreContentResponse:ExploreContentResponse?) {

                when (it.redirectionType) {
                    AppConstants.EXPLORE_IN_APP -> {
                        it.redirectionResource?.let { uri ->

                            val redirectionResources = uri.split(",")[0]
                            if (redirectionResources == AppConstants.FyperScreen) {
                                findNavController().navigate(R.id.navigation_fyper)
                            } else if (redirectionResources == AppConstants.JACKPOTTAB) {
                                findNavController().navigate(R.id.navigation_rewards)
                            } else if (redirectionResources == AppConstants.CardScreen) {
                                findNavController().navigate(R.id.navigation_card)
                            } else if (redirectionResources == AppConstants.RewardHistory) {
                                findNavController().navigate(R.id.navigation_rewards_history)
                            }else if (redirectionResources == AppConstants.ARCADE) {
                                findNavController().navigate(R.id.navigation_arcade)
                            }else {
                                Utility.deeplinkRedirection(redirectionResources, requireContext())
                            }


                        }

                    }
                    EXPLORE_IN_APP_WEBVIEW -> {

                        val intent = Intent(requireContext(), ExploreInAppWebview::class.java)
                        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, EXPLORE_IN_APP_WEBVIEW)
                        intent.putExtra(AppConstants.IN_APP_URL, it.redirectionResource)

                        startActivity(intent)
                    }
                    IN_APP_WITH_CARD -> {
                        val intent = Intent(requireContext(), StoreWebpageOpener2::class.java)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, it.redirectionResource)
                        startActivity(intent)
                    }
                    OFFER_REDIRECTION -> {
                        exploreFragmentVM.callFetchOfferApi(it.redirectionResource)
                    }


                    AppConstants.FEED_TYPE_BLOG -> {
                        exploreFragmentVM.callFetchFeedsApi(it.redirectionResource)
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
                            exploreFragmentVM.callFetchFeedsApi(it.redirectionResource)

                        }

                    }
                    AppConstants.EXPLORE_SECTION_EXPLORE->{
                        val directions = exploreContentResponse?.sectionDisplayText?.let { it1 ->
                            ExploreFragmentDirections.actionExploreSectionExplore(sectionExploreItem = it,
                                sectionExploreName= it1
                            )
                        }
                        directions?.let { it1 -> findNavController().navigate(it1) }
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
        root.rvExplore.adapter = typeAdapter
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