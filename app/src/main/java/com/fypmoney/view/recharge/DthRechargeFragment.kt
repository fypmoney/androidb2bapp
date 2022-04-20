package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityDthRechargeBinding
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoActivityWithExplore
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.home.main.explore.view.ExploreFragmentDirections
import com.fypmoney.view.recharge.viewmodel.DthViewModel
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class DthRechargeFragment :
    BaseFragment<ActivityDthRechargeBinding, DthViewModel>() {
    private val args: DthRechargeFragmentArgs by navArgs()
    private lateinit var mViewModel: DthViewModel
    private lateinit var mViewBinding: ActivityDthRechargeBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_dth_recharge
    }

    override fun getViewModel(): DthViewModel {
        mViewModel = ViewModelProvider(this).get(DthViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()


        mViewModel.Selectedoperator.value = args.operator

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Dth Recharge"
        )

        setBindings()

        setObserver()
        setExploreListners()


    }

    private fun setBindings() {
        mViewBinding.fetchdetails.setOnClickListener {
            if (!mViewBinding.dthNumber.text.isNullOrEmpty() && mViewBinding.dthNumber.text.length >= 8) {
                mViewModel.callGetDthInfo(mViewBinding.dthNumber.text.toString())
            } else {
                Utility.showToast("Enter correct number")
            }
        }


        mViewBinding.dthNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewBinding.continueBtn.isEnabled = !s.isNullOrEmpty() && s.length <= 10
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        mViewBinding.continueBtn.setOnClickListener {
            if (!mViewBinding.amount.text.isNullOrEmpty()) {
                mViewModel.callMobileRecharge(
                    mViewModel.Selectedoperator.value,
                    mViewModel.opertaorList.value,
                    mViewBinding.dthNumber.text.toString(),
                    mViewBinding.amount.text.toString()
                )
            } else {
                Utility.showToast("Enter bill amount")

            }


        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        mViewModel.paymentResponse.observe(viewLifecycleOwner) {
            var directions = DthRechargeFragmentDirections.actionGoToDthSuccess(
                successDth = it,
                selectedOperator = mViewModel.operatorResponse.get()
            )
            findNavController().navigate(directions)
        }

        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)
            mViewBinding.amount.setText(it.amount?.toDoubleOrNull()?.toInt().toString())


        }
    }

    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "DidUKnowSheet")
    }

    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        var bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(childFragmentManager, "TASKMESSAGE")
    }

    private fun setExploreListners() {
        mViewModel?.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        mViewModel?.feedDetail.observe(
            viewLifecycleOwner
        ) { list ->

            when (list.displayCard) {

                AppConstants.FEED_TYPE_BLOG -> {
                    val intent = Intent(context, UserFeedsDetailView::class.java)
                    intent.putExtra(AppConstants.FEED_RESPONSE, list)
                    intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.FEED_TYPE_BLOG)
                    intent.putExtra(
                        AppConstants.CUSTOMER_INFO_RESPONSE,
                        CustomerInfoResponseDetails()
                    )
                    startActivity(intent)

                }
                AppConstants.FEED_TYPE_STORIES -> {

                    callDiduKnowBottomSheet(list.resourceArr)
                }

            }


        }
        mViewModel?.rewardHistoryList.observe(
            viewLifecycleOwner
        ) { list ->

            setRecyclerView(mViewBinding, list)
        }
    }

    private fun setRecyclerView(
        root: ActivityDthRechargeBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.rvBanners.visibility = View.GONE
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        root.rvBanners.layoutManager = layoutManager

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
            Color.BLACK
        )
        root.rvBanners.adapter = typeAdapter
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
                    if (redirectionResources == AppConstants.FyperScreen) {
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
                mViewModel.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                mViewModel.callFetchFeedsApi(redirectionResource)

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
                    mViewModel.callFetchFeedsApi(redirectionResource)

                }

            }
        }
    }

}
