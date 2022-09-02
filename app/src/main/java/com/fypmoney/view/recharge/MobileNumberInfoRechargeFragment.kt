package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.MobileNumberInfoRechargeFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoWithExploreFragment
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.MobileNumberInfoRechargeFragmentVM
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*


class MobileNumberInfoRechargeFragment:BaseFragment<MobileNumberInfoRechargeFragmentBinding, MobileNumberInfoRechargeFragmentVM>() {

    private val mViewModel by viewModels<MobileNumberInfoRechargeFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var mViewBinding: MobileNumberInfoRechargeFragmentBinding
    private val args: MobileNumberInfoRechargeFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.mobile_number_info_recharge_fragment
    }

    override fun getViewModel(): MobileNumberInfoRechargeFragmentVM {
        return mViewModel
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()
        mViewModel.mobileNumberInfoModel = args.mobileInfoUiModel
        setObserver()
        setUpDataInUi()
        setUpObserver()
        mViewModel.callExplporeContent(mViewModel.mobileNumberInfoModel?.rechargeType)
        mViewModel.mobileNumberInfoModel?.rechargeType?.let { mViewModel.callGetOperatorList(it) }
    }



    private fun setUpDataInUi() {
        mViewModel.mobileNumberInfoModel?.let {
            setToolbarAndTitle(
                context = requireContext(),
                toolbar = toolbar, backArrowTint = Color.WHITE,
                titleColor = Color.WHITE,
                isBackArrowVisible = true,
                toolbarTitle = it.operator?.let{it1->it1+" "+Utility.toTitleCase(it.rechargeType)} ?: kotlin.run { Utility.toTitleCase(it.rechargeType) }
            )
            mViewBinding.tvUserNumber.text = it.mobile
            when (it.operator) {
                "Airtel" -> {
                    mViewBinding.continueBtn.isEnabled = true
                    mViewBinding.ivUser.setBackgroundResource(R.drawable.ic_airtel)
                    mViewBinding.opertorLisTv.text = it.circle?.let { it1->it.operator+"-"+it.circle } ?: kotlin.run { it.operator }
                }
                "Vodafone" -> {
                    mViewBinding.continueBtn.isEnabled = true
                    mViewBinding.ivUser.setBackgroundResource(R.drawable.ic_vodafone)
                    mViewBinding.opertorLisTv.text = it.circle?.let { it1->it.operator+"-"+it.circle } ?: kotlin.run { it.operator }
                }
                "JIO" -> {
                    mViewBinding.continueBtn.isEnabled = true
                    mViewBinding.ivUser.setBackgroundResource(R.drawable.ic_jio)
                    mViewBinding.opertorLisTv.text = it.circle?.let { it1->it.operator+"-"+it.circle } ?: kotlin.run { it.operator }
                }
                else -> {
                    mViewBinding.ivUser.setBackgroundResource(R.drawable.ic_user2)
                    mViewBinding.opertorLisTv.text = getString(R.string.select_operator)
                    mViewBinding.continueBtn.isEnabled = false

                }
            }
        }

    }



    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        mViewModel.operatorResponse = OperatorResponse(name = mViewModel.mobileNumberInfoModel?.operator)
        val navController = findNavController();
        // We use a String here, but any type that can be put in a Bundle is supported
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<OperatorResponse>("operator_selected")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                // Do something with the result.
                val operator = result as OperatorResponse
                mViewModel.operatorResponse = operator
                mViewModel.mobileNumberInfoModel?.operator =operator.name
                setUpDataInUi()
                mViewBinding.opertorLisTv.text = operator.name
                mViewBinding.continueBtn.isEnabled = true

            }
    }

    private fun setUpObserver() {
        mViewModel.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        mViewModel.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelEvent(it: MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeEvent?) {
        when(it){
            is MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeEvent.ShowOperatorListScreen -> {
                val directions =
                    MobileNumberInfoRechargeFragmentDirections.actionToOperatorList(
                        rechargeType = it.rechargeType,
                                mobileNo = it.mobileNo
                    )

                directions.let { it1 -> findNavController().navigate(it1) }
            }
            null -> {}
            is MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeEvent.ShowPlanScreen -> {
                val directions = mViewModel.mobileNumberInfoModel?.circle?.let { it1 ->
                    MobileNumberInfoRechargeFragmentDirections.actionSelectRechargePlans(
                        mViewModel.operatorResponse,
                        selectedCircle = it1,
                        mobile = mViewModel.mobileNumberInfoModel?.mobile,
                        rechargeTye = mViewModel.mobileNumberInfoModel?.rechargeType
                    )
                }
                directions?.let { it1 -> findNavController().navigate(it1) }
            }
            is MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeEvent.ShowPostpaidBilScreen -> {
                val directions = MobileNumberInfoRechargeFragmentDirections.actionPostpaidBill(
                        selectedOperator = mViewModel.operatorResponse,
                        selectedCircle = null,
                        operator =  mViewModel.mobileNumberInfoModel?.operator,
                        mobile = mViewModel.mobileNumberInfoModel?.mobile,
                        rechargeType = mViewModel.mobileNumberInfoModel?.rechargeType)
                directions.let { it1 -> findNavController().navigate(it1) }
            }
        }
    }

    private fun handelState(it: MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeState?) {
        when(it){
            is MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeState.Error -> {
                when(it.errorFromApi){
                    ApiConstant.API_Explore->{
                        mViewBinding.rvBanners.toGone()
                        mViewBinding.shimmerLayout.toGone()
                    }
                }
            }
            is MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeState.ExploreSuccess -> {
                mViewBinding.rvBanners.toVisible()
                mViewBinding.shimmerLayout.toGone()
                setRecyclerView(mViewBinding, it.explore)
            }
            MobileNumberInfoRechargeFragmentVM.EnterMobileNumberInfoRechargeState.Loading -> {
                mViewBinding.shimmerLayout.toVisible()
            }
            null -> {}
        }
    }

    private fun setRecyclerView(
        root: MobileNumberInfoRechargeFragmentBinding,
        list: ArrayList<ExploreContentResponse>
    ) {
        if (list.size > 0) {
            root.rvBanners.visibility = View.VISIBLE
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
            Color.WHITE
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
                val directions = MobileNumberInfoRechargeFragmentDirections.actionExploreSectionExplore(
                        sectionExploreItem = sectionContentItem,
                        sectionExploreName = exploreContentResponse?.sectionDisplayText
                    )
                directions.let { it1 -> findNavController().navigate(it1) }
            }
            AppConstants.EXPLORE_IN_APP -> {
                redirectionResource?.let { uri ->

                    val redirectionResources = uri.split(",").get(0)
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
                            findNavController().navigate(Uri.parse("fypmoney://creategiftcard/${redirectionResource}"))
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

                        AppConstants.TYPE_POCKET_MONEY_REMINDER -> {
                            findNavController().navigate(Uri.parse("https://www.fypmoney.in/pocketmoneyremainder"),
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


}
