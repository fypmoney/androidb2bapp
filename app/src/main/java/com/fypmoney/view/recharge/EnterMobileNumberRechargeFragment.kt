package com.fypmoney.view.recharge


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.EnterMobileNumberRechargeFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.POSTPAID
import com.fypmoney.util.AppConstants.PREPAID
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.getPhoneNumberFromContact
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoWithExploreFragment
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.storeoffers.OfferDetailsBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.recharge.adapter.RecentRechargeAdapter
import com.fypmoney.view.recharge.adapter.RecentRechargeUiModel
import com.fypmoney.view.recharge.model.MobileNumberInfoUiModel
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.EnterMobileNumberRechargeFragmentVM
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*


private const val PICK_CONTACT = 1
class EnterMobileNumberRechargeFragment : BaseFragment<EnterMobileNumberRechargeFragmentBinding, EnterMobileNumberRechargeFragmentVM>() {

    private lateinit var  enterMobileNumberRechargeFragmentVM:EnterMobileNumberRechargeFragmentVM

    private lateinit var binding: EnterMobileNumberRechargeFragmentBinding

    private val args: EnterMobileNumberRechargeFragmentArgs by navArgs()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.enter_mobile_number_recharge_fragment
    }

    override fun getViewModel(): EnterMobileNumberRechargeFragmentVM {
        enterMobileNumberRechargeFragmentVM = ViewModelProvider(this).get(EnterMobileNumberRechargeFragmentVM::class.java)
        return enterMobileNumberRechargeFragmentVM
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        enterMobileNumberRechargeFragmentVM.rechargeType = args.rechargeType!!
        enterMobileNumberRechargeFragmentVM.callExplporeContent(enterMobileNumberRechargeFragmentVM.rechargeType)
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true, toolbarTitle = if(enterMobileNumberRechargeFragmentVM.rechargeType == PREPAID) getString(R.string.prepaid_recharge) else getString(R.string.postpaid_recharge)
        )
        setUpObserver()
        setUpRecentRecylerview()
        setExploreListners()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
        enterMobileNumberRechargeFragmentVM.callRecentRecharge()
    }

    private fun setUpRecentRecylerview() {
        val recentAdapter = RecentRechargeAdapter(
            this,
            onCheckStatusClick = {

            },
            onRepeatRechargeClick = {
                if(it.cardType.equals(PREPAID)){
                    val direction  = EnterMobileNumberRechargeFragmentDirections.actionFromRecentToPlans(
                        selectedOperator = OperatorResponse(name = it.operatorName,operatorId = it.requestOperatorId),
                        selectedCircle = it.circle!!,
                        mobile = it.mobileNumber,
                        rechargeTye = it.cardType
                    )
                    findNavController().navigate(direction)
                }else if(it.cardType.equals(POSTPAID)){
                    val directions  = EnterMobileNumberRechargeFragmentDirections.actionEnterMobileNoToPostpaidBillDetails(
                        selectedOperator = OperatorResponse(name = it.operatorName, operatorId = it.requestOperatorId),
                        selectedCircle = it.circle,
                        operator =  it.operatorName,
                        mobile = it.mobileNumber,
                        rechargeType = it.cardType
                    )
                    findNavController().navigate(directions)

                }

            }
        )
        with(binding.rvRecentsRecharges) {
            adapter = recentAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpObserver(){
        enterMobileNumberRechargeFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        enterMobileNumberRechargeFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState?) {
        when(it){
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.Error -> {
                when(it.errorFromApi){
                    ApiConstant.API_Explore->{
                        binding.rvBanners.toGone()
                        binding.shimmerLayout.toGone()
                    }
                    ApiConstant.API_GET_HLR_CHECK->{
                        binding.continueBtn.setBusy(false)
                    }
                    ApiConstant.API_RECENT_RECHARGE->{
                        binding.shimmerLayoutRecent.toGone()
                        binding.noRecentRechargesTv.toVisible()
                        binding.rvRecentsRecharges.toGone()
                    }
                }
            }
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.ExploreSuccess -> {
                binding.rvBanners.toVisible()
                binding.shimmerLayout.toGone()
                setRecyclerView(binding, it.explore)
            }
            EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.Loading -> {
                binding.shimmerLayout.toVisible()
            }
            null -> {}
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.HLRSuccess -> {
                binding.continueBtn.setBusy(false)
            }
            EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.RecentRechargeLoading -> {
                binding.shimmerLayoutRecent.toVisible()
                binding.noRecentRechargesTv.toGone()
                binding.rvRecentsRecharges.toGone()
            }
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.RecentRechargeSuccess -> {
                binding.shimmerLayoutRecent.toGone()
                if(it.recentItem.isNullOrEmpty()){
                    binding.noRecentRechargesTv.toVisible()
                    binding.rvRecentsRecharges.toGone()
                }else{
                    binding.noRecentRechargesTv.toGone()
                    binding.rvRecentsRecharges.toVisible()
                    (binding.rvRecentsRecharges.adapter as RecentRechargeAdapter).submitList(it.recentItem.map {
                        RecentRechargeUiModel.fromRechargeItem(requireContext(),it)
                    })


                }
            }
        }
    }

    private fun showEnterMobileNumberInfoRechargeScreen(circle:String?, operator:String?) {
        val directions = EnterMobileNumberRechargeFragmentDirections.actionEnterMobileToMobileNoInfoRecharge(
            MobileNumberInfoUiModel(
                mobile = binding.etNumber.text.toString(),
                operator = operator,
                circle= circle,
                rechargeType= enterMobileNumberRechargeFragmentVM.rechargeType,
            ))

        directions.let { it1 -> findNavController().navigate(it1) }
    }


    private fun handelEvent(it: EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeEvent?) {
        when(it){
            EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeEvent.OnContinueEvent -> {
                enterMobileNumberRechargeFragmentVM.callGetMobileHrl(binding.etNumber.text.toString())
                binding.continueBtn.setBusy(true)
            }
            EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeEvent.PickContactFromContactBookEvent -> {
                selectContactFromPhoneContactList(PICK_CONTACT)
            }
            null -> {}
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeEvent.ShowNextScreenWithHlrInfo -> {
                showEnterMobileNumberInfoRechargeScreen(it.hlrInfo?.circle,it.hlrInfo?.operator)
            }
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setListeners() {
        binding.etNumber.doOnTextChanged { text, start, before, count ->
            if(!text.isNullOrEmpty() && text.length < 10){
                binding.continueBtn.isEnabled = false
                binding.errorTv.toVisible()
            }else{
                binding.continueBtn.isEnabled = true
                binding.errorTv.toGone()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PICK_CONTACT->{
                if(resultCode==Activity.RESULT_OK){
                    when(val result = getPhoneNumberFromContact(requireActivity(),data)){
                        is Utility.MobileNumberFromPhoneBook.MobileNumberFound -> {
                            binding.etNumber.setText(result.phoneNumber)
                        }
                        is Utility.MobileNumberFromPhoneBook.UnableToFindMobileNumber -> {
                            Utility.showToast(result.errorMsg)
                        }
                    }
                }
            }
        }


    }



    private fun callDiduKnowBottomSheet(list: List<String>) {
        val bottomSheet =
            StoriesBottomSheet(list)
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(childFragmentManager, "DidUKnowSheet")
    }

    private fun callOfferDetailsSheeet(redeemDetails: offerDetailResponse) {

        val bottomSheetMessage = OfferDetailsBottomSheet(redeemDetails)
        bottomSheetMessage.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetMessage.show(childFragmentManager, "TASKMESSAGE")
    }

    private fun setExploreListners() {
        enterMobileNumberRechargeFragmentVM.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        enterMobileNumberRechargeFragmentVM.feedDetail.observe(
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
    }


    private fun setRecyclerView(
        root: EnterMobileNumberRechargeFragmentBinding,
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
                val directions = EnterMobileNumberRechargeFragmentDirections.actionEnterMobileNumberRechargeToSectionExplore(
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
                enterMobileNumberRechargeFragmentVM.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                enterMobileNumberRechargeFragmentVM.callFetchFeedsApi(redirectionResource)

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
                    enterMobileNumberRechargeFragmentVM.callFetchFeedsApi(redirectionResource)

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

    private fun selectContactFromPhoneContactList(requestCode:Int){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }
}
