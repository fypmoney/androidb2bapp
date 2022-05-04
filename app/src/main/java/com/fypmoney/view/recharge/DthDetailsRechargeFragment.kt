package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.connectivity.ApiConstant.API_Explore
import com.fypmoney.connectivity.ApiConstant.API_GET_WALLET_BALANCE
import com.fypmoney.databinding.DthDetailsRechargeFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.textview.ClickableSpanListener
import com.fypmoney.util.textview.MyStoreClickableSpan
import com.fypmoney.util.videoplayer.VideoActivity2
import com.fypmoney.util.videoplayer.VideoActivityWithExplore
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.activity.AddMoneyView
import com.fypmoney.view.activity.UserFeedsDetailView
import com.fypmoney.view.fragment.OfferDetailsBottomSheet
import com.fypmoney.view.fragment.TaskMessageInsuficientFuntBottomSheet
import com.fypmoney.view.fypstories.view.StoriesBottomSheet
import com.fypmoney.view.home.main.explore.ViewDetails.ExploreInAppWebview
import com.fypmoney.view.home.main.explore.`interface`.ExploreItemClickListener
import com.fypmoney.view.home.main.explore.adapters.ExploreBaseAdapter
import com.fypmoney.view.home.main.explore.model.ExploreContentResponse
import com.fypmoney.view.home.main.explore.model.SectionContentItem
import com.fypmoney.view.interfaces.AcceptRejectClickListener
import com.fypmoney.view.recharge.fragments.DthKnowMoreBottomSheet
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.DthDetailsRechargeFragmentVM
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*


class DthDetailsRechargeFragment : BaseFragment<DthDetailsRechargeFragmentBinding, DthDetailsRechargeFragmentVM>() {

    private val dthDetailsRechargeFragmentVM by viewModels<DthDetailsRechargeFragmentVM> { defaultViewModelProviderFactory }

    private val args: DthDetailsRechargeFragmentArgs by navArgs()

    private lateinit var binding: DthDetailsRechargeFragmentBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.dth_details_recharge_fragment
    }

    override fun getViewModel(): DthDetailsRechargeFragmentVM {
        return dthDetailsRechargeFragmentVM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()

        dthDetailsRechargeFragmentVM.selectedDthOperator = args.storeDataModel
        args.storeDataModel?.subscriberId?.let {
            binding.dthNumberEt.setText(it)

        }
        args.storeDataModel?.amount?.let {
            binding.amount.setText(it)
        }

        dthDetailsRechargeFragmentVM.oper = OperatorResponse().apply {
            Icon = dthDetailsRechargeFragmentVM.selectedDthOperator?.Icon
            operatorId = dthDetailsRechargeFragmentVM.selectedDthOperator?.operator_id
            name = dthDetailsRechargeFragmentVM.selectedDthOperator?.title
            displayName = dthDetailsRechargeFragmentVM.selectedDthOperator?.title
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "${dthDetailsRechargeFragmentVM.selectedDthOperator?.title} Dth Recharge"
        )
        showPrivacyPolicyAndTermsAndConditions()
        setBindings()
        setExploreListners()
        setUpObserver()

    }

    private fun setBindings() {
        binding.dthNumberEt.doOnTextChanged { text, start, before, count ->
            binding.continueBtn.isEnabled = !text.isNullOrEmpty()
        }
        binding.amount.doOnTextChanged { text, start, before, count ->
            binding.continueBtn.isEnabled = !text.isNullOrEmpty() && text.toString().toInt()>0

        }

    }

    override fun onTryAgainClicked() {

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

    fun setUpObserver() {
        dthDetailsRechargeFragmentVM.state.observe(viewLifecycleOwner) {
            handelState(it)
        }
        dthDetailsRechargeFragmentVM.event.observe(viewLifecycleOwner) {
            handelEvent(it)
        }
    }

    private fun handelEvent(it: DthDetailsRechargeFragmentVM.DthDetailsEvent?) {
        when(it){
            is DthDetailsRechargeFragmentVM.DthDetailsEvent.ShowLowBalanceAlert -> {
                callInsufficientFundMessageSheet(it.amount)
            }
            is DthDetailsRechargeFragmentVM.DthDetailsEvent.ShowPaymentProcessingScreen ->{
                val directions = DthDetailsRechargeFragmentDirections.actionGoToDthSuccess(
                    payAndRechargeRequest = it.payAndRechargeRequest)
                findNavController().navigate(directions)
            }
            null -> TODO()
            DthDetailsRechargeFragmentVM.DthDetailsEvent.OnPayClickEvent -> {
                askForDevicePassword()
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        dthDetailsRechargeFragmentVM.fetchBalance()
                    }

                }
            }
        }
    }

    private fun handelState(it: DthDetailsRechargeFragmentVM.DthDetailsState?) {
        when (it) {
            is DthDetailsRechargeFragmentVM.DthDetailsState.Error -> {
                when (it.errorFromApi) {
                    API_Explore -> {
                        binding.rvBanners.toGone()
                        binding.shimmerLayout.toGone()
                    }
                    API_GET_WALLET_BALANCE->{
                        binding.continueBtn.setBusy(false)
                    }

                }
            }
            is DthDetailsRechargeFragmentVM.DthDetailsState.ExploreSuccess -> {
                binding.rvBanners.toVisible()
                binding.shimmerLayout.toGone()
                setRecyclerView(binding, it.explore)
            }
            null -> TODO()
            is DthDetailsRechargeFragmentVM.DthDetailsState.BalanceSuccess -> {
                binding.continueBtn.setBusy(false)
            }
            is DthDetailsRechargeFragmentVM.DthDetailsState.Loading -> {
                when(it.api){
                    API_Explore->{
                        binding.shimmerLayout.toVisible()
                    }
                    ApiConstant.API_GET_WALLET_BALANCE ->{
                        binding.continueBtn.setBusy(true)
                    }
                }
            }
        }
    }

    private fun setRecyclerView(
        root: DthDetailsRechargeFragmentBinding,
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
                val intent = Intent(requireActivity(), VideoActivityWithExplore::class.java)
                intent.putExtra(ARG_WEB_URL_TO_OPEN, redirectionResource)
                intent.putExtra(AppConstants.ACTIONFLAG, sectionContentItem.actionFlagCode)
                startActivity(intent)
            }
            AppConstants.EXPLORE_SECTION_EXPLORE -> {
                val directions = exploreContentResponse?.sectionDisplayText?.let { it1 ->
                    EnterMobileNumberRechargeFragmentDirections.actionEnterMobileNumberRechargeToSectionExplore(
                        sectionExploreItem = sectionContentItem,
                        sectionExploreName = it1
                    )
                }
                directions?.let { it1 -> findNavController().navigate(it1) }
            }
            AppConstants.EXPLORE_IN_APP -> {
                redirectionResource?.let { uri ->

                    val redirectionResources = uri.split(",").get(0)
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
                        redirectionResources.let { it1 ->
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
                dthDetailsRechargeFragmentVM.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                dthDetailsRechargeFragmentVM.callFetchFeedsApi(redirectionResource)

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
                    dthDetailsRechargeFragmentVM.callFetchFeedsApi(redirectionResource)

                }

            }
        }
    }

    private fun setExploreListners() {
        dthDetailsRechargeFragmentVM.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        dthDetailsRechargeFragmentVM.feedDetail.observe(
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


    private fun showPrivacyPolicyAndTermsAndConditions() {
        val text = resources.getString(
            R.string.enter_10_digit_customer_id_starting_with_3_to_locate_the_customer_id_press_the_menu_button_on_your_remote,
            resources.getString(R.string.know_more)
        )
        val privacyPolicyText = resources.getString(R.string.know_more)
        val privacyPolicyStarIndex = text.indexOf(privacyPolicyText)
        val privacyPolicyEndIndex = privacyPolicyStarIndex + privacyPolicyText.length
        val ss = SpannableString(text)
        ss.setSpan(
            MyStoreClickableSpan(
                color = resources.getColor(R.color.add_money_amount_color),
                pos = 1,
                clickableSpanListener = object : ClickableSpanListener {
                    override fun onPositionClicked(pos: Int) {
                        var title:String? = null
                        var subTitle:String? = null
                        var res:Int? = null
                        when(dthDetailsRechargeFragmentVM.selectedDthOperator?.title){
                            "Airtel"->{
                                title = getString(R.string.whats_my_customer_id)
                                subTitle = getString(R.string.airtel_know_more_body)
                                res = R.drawable.ic_dth_airtel
                            }
                            "Dish TV"->{
                                title = getString(R.string.dish_tv_title)
                                subTitle = getString(R.string.dish_tv_body)
                                res = R.drawable.ic_dth_dishtv
                            }
                            "Tata Sky"->{
                                title = getString(R.string.tata_sky_title)
                                subTitle = "\n" +
                                        getString(R.string.tata_sky_body)
                                res = R.drawable.ic_dth_tata
                            }
                            "Videocon D2H"->{
                                title = getString(R.string.d2h_title)
                                subTitle = getString(R.string.d2h_body)
                                res = R.drawable.ic_dth_d2h
                            }
                        }
                        title?.let { subTitle?.let { it1 ->
                            res?.let { it2 ->
                                showKnowMoreAccordingOpeartor(it,
                                    it1, it2
                                )
                            }
                        } }
                    }
                }),
            privacyPolicyStarIndex,
            privacyPolicyEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.customerIdDetailsDescTv.text = ss
        binding.customerIdDetailsDescTv.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun showKnowMoreAccordingOpeartor(title:String,subTitle:String,res:Int) {
        val dthKnowMoreBottomSheet = DthKnowMoreBottomSheet(title,subTitle,res)
        dthKnowMoreBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        dthKnowMoreBottomSheet.show(childFragmentManager, "DthKnowMoreBottomSheet")
    }

    private fun callInsufficientFundMessageSheet(amount: String?) {
        var bottomSheetInsufficient: TaskMessageInsuficientFuntBottomSheet? = null
        val itemClickListener2 = object : AcceptRejectClickListener {
            override fun onAcceptClicked(pos: Int, str: String) {
                bottomSheetInsufficient?.dismiss()
            }

            override fun onRejectClicked(pos: Int) {
                bottomSheetInsufficient?.dismiss()
                val intent = Intent(requireActivity(), AddMoneyView::class.java)
                intent.putExtra("amountshouldbeadded", Utility.convertToRs(amount))
                startActivity(intent)
            }

            override fun ondimiss() {
                bottomSheetInsufficient?.dismiss()
            }
        }
        bottomSheetInsufficient = TaskMessageInsuficientFuntBottomSheet(itemClickListener2,
            title = resources.getString(R.string.insufficient_bank_balance),
            subTitle =  resources.getString(R.string.insufficient_bank_body),
            amount = resources.getString(R.string.add_money_title1)+resources.getString(R.string.Rs)+Utility.convertToRs(amount),
            background = "#2d3039",
            titleColor  = "#ffffff",
            moneyTextColor  = "#ffffff",
            buttonColor  = "#8ECC9A",
        )

        bottomSheetInsufficient.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheetInsufficient.show(requireActivity().supportFragmentManager, "Insufficient Fund")
    }


}
