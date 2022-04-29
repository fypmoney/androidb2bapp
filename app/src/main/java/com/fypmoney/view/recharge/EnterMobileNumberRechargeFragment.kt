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
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.fypmoney.util.AppConstants.PREPAID
import com.fypmoney.util.Utility
import com.fypmoney.util.Utility.getPhoneNumberFromContact
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
import com.fypmoney.view.recharge.model.MobileNumberInfoUiModel
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
        setExploreListners()
        setListeners()
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
            null -> TODO()
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeState.HLRSuccess -> {
//                showOperatorListScreen(it.hlrInfo?.circle,it.hlrInfo?.operator)
                binding.continueBtn.setBusy(false)
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
            null -> TODO()
            is EnterMobileNumberRechargeFragmentVM.EnterMobileNumberRechargeEvent.ShowNextScreenWithHlrInfo -> {
                showEnterMobileNumberInfoRechargeScreen(it.hlrInfo?.circle,it.hlrInfo?.operator)
            }
        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setListeners() {
        binding.etNumber.doOnTextChanged { text, start, before, count ->
            if(text.isNullOrEmpty() && text!!.length < 10){
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
        }
    }

    private fun selectContactFromPhoneContactList(requestCode:Int){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }
}
