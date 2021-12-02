package com.fypmoney.view.home.main.explore.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentExploreBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.activity.UserFeedsInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreAdapter
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.viewmodel.ExploreFragmentVM

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
    private var itemsArrayList: ArrayList<ExploreContentResponse> = ArrayList()

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
    }

    private fun setRecyclerView(
        root: FragmentExploreBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
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
            override fun onItemClicked(position: Int, it: SectionContentItem) {

                when (it.redirectionType) {
                    AppConstants.EXPLORE_IN_APP -> {
                        it.contentResourceUri?.let {

                            val screen = it.split(",")[0]
                            if (screen == AppConstants.StoreScreen || screen == AppConstants.CardScreen || screen == AppConstants.FEEDSCREEN || screen == AppConstants.FyperScreen) {
//                                tabchangeListner.tabchange(0, screen)
                            } else {
                                Utility.deeplinkRedirection(screen, requireContext())
                            }


                        }

                    }
                    AppConstants.EXPLORE_IN_APP_WEBVIEW -> {
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
                                    Uri.parse(it.redirectionResource)
                                ), getString(R.string.browse_with)
                            )
                        )

                    }
                    AppConstants.FEED_TYPE_EXTWEBVIEW2 -> {
                        startActivity(
                            Intent.createChooser(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(it.redirectionResource)
                                ), getString(R.string.browse_with)
                            )
                        )

                    }
                    AppConstants.FEED_TYPE_STORIES -> {
                        if (!it.redirectionResource.isNullOrEmpty()) {
//                            callDiduKnowBottomSheet(it.resourceArr)
                        }

                    }
                }

            }
        }

        var typeAdapter = ExploreBaseAdapter(
            arrayList,
            requireContext(),
            exploreClickListener2,
            exploreFragmentVM
        )
        root.rvExplore.adapter = typeAdapter
    }


    private fun intentToActivity(
        aClass: Class<*>,
        feedDetails: SectionContentItem,
        type: String? = null
    ) {
        val intent = Intent(context, aClass)
//        intent.putExtra(AppConstants.EXPLORE_RESPONSE, feedDetails)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        intent.putExtra(AppConstants.CUSTOMER_INFO_RESPONSE, CustomerInfoResponseDetails())
        startActivity(intent)
    }

    override fun onFeedClick(position: Int, feedDetails: SectionContentItem) {

    }

}