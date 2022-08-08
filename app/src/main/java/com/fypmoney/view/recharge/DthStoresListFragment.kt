package com.fypmoney.view.recharge


import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.DthStoresListFragmentBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.model.CustomerInfoResponseDetails
import com.fypmoney.model.StoreDataModel
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
import com.fypmoney.view.recharge.adapter.RecentRechargeAdapter
import com.fypmoney.view.recharge.adapter.RecentRechargeUiModel
import com.fypmoney.view.recharge.viewmodel.DthStoresListFragmentVM
import com.fypmoney.view.storeoffers.model.offerDetailResponse
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class DthStoresListFragment : BaseFragment<DthStoresListFragmentBinding, DthStoresListFragmentVM>() {

    private  val dthStoresListFragmentVM by viewModels<DthStoresListFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var binding: DthStoresListFragmentBinding



    override fun onTryAgainClicked() {

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.dth_stores_list_fragment
    }

    override fun getViewModel(): DthStoresListFragmentVM {
        return dthStoresListFragmentVM
    }

    override fun onStart() {
        super.onStart()
        dthStoresListFragmentVM.callRecentRecharge()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        binding.viewModel = dthStoresListFragmentVM
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "DTH Recharge",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        setUpRecentRecylerview()
        dthStoresListFragmentVM.storeAdapter.setList(
            getListOfApps(
                R.raw.dth_json,
                arrayListOf(
                    R.drawable.ic_dth_airtel,
                    R.drawable.ic_dth_dishtv,
                    R.drawable.ic_dth_tata,
                    R.drawable.ic_dth_d2h
                )
            )
        )

        setExploreListners()
        setUpObserver()
        dthStoresListFragmentVM.callExplporeContent("DTH")
    }





    private fun getListOfApps(stores: Int, iconList: List<Int>): ArrayList<StoreDataModel> {
        val upiList = ArrayList<StoreDataModel>()
        try {
            val obj = JSONObject(loadJSONFromAsset(stores))
            val m_jArry = obj.getJSONArray("stores")

            for (i in 0 until m_jArry.length()) {
                val jo_inside = m_jArry.getJSONObject(i)

                val formula_value = jo_inside.getString("title")
                val url_value = jo_inside.getString("url")

                val operator_id = jo_inside.getString("operator_id")
                val model = StoreDataModel()
                model.title = formula_value
                model.url = url_value
                model.operator_id = operator_id
                model.Icon = iconList[i]

                upiList.add(model)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return upiList


    }

    private fun loadJSONFromAsset(stores: Int): String? {
        var json: String? = null
        json = try {

            val `is`: InputStream? = resources.openRawResource(stores)  // your file name
            val size: Int? = `is`?.available()
            val buffer = size?.let { ByteArray(it) }
            `is`?.read(buffer)
            `is`?.close()
            buffer?.let { String(it, charset("UTF-8")) }
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    private fun setUpRecentRecylerview() {
        val recentAdapter = RecentRechargeAdapter(
            this,
            onCheckStatusClick = {

            },
            onRepeatRechargeClick = {
                val storeDataModel = StoreDataModel().apply {
                    Icon = it.operatorLogo
                    title = it.operatorName
                    operator_id = it.requestOperatorId
                    subscriberId = it.mobileNumber
                    amount = Utility.convertToRs(it.amount)
                }
                val directions =
                    DthStoresListFragmentDirections.actionDthRechargeScreen( storeDataModel = storeDataModel)
                directions.let { it1 -> findNavController().navigate(it1) }
            }
        )
        with(binding.rvRecentsRecharges) {
            adapter = recentAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }
    fun setUpObserver(){
        dthStoresListFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        dthStoresListFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelEvent(it: DthStoresListFragmentVM.DthStoreListEvent?) {
        when(it){
            is DthStoresListFragmentVM.DthStoreListEvent.ShowDTHDetailsScreen -> {
                val directions =
                    DthStoresListFragmentDirections.actionDthRechargeScreen( storeDataModel = it.model)
                directions.let { it1 -> findNavController().navigate(it1) }
            }
            null -> {}
        }
    }

    private fun handelState(it: DthStoresListFragmentVM.DthStoresListState?) {
        when(it){
            is DthStoresListFragmentVM.DthStoresListState.Error -> {
                when(it.errorFromApi){
                    ApiConstant.API_Explore->{
                        binding.rvBanners.toGone()
                        binding.shimmerLayout.toGone()
                    }
                }
            }
            is DthStoresListFragmentVM.DthStoresListState.ExploreSuccess -> {
                binding.rvBanners.toVisible()
                binding.shimmerLayout.toGone()
                setRecyclerView(binding, it.explore)
            }
            DthStoresListFragmentVM.DthStoresListState.Loading -> {
                binding.shimmerLayout.toVisible()
            }
            null -> {}
            DthStoresListFragmentVM.DthStoresListState.RecentRechargeLoading -> {
                binding.shimmerLayoutRecent.toVisible()
                binding.noRecentRechargesTv.toGone()
                binding.rvRecentsRecharges.toGone()
            }
            is DthStoresListFragmentVM.DthStoresListState.RecentRechargeSuccess -> {
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

    private fun setRecyclerView(
        root: DthStoresListFragmentBinding,
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
                val directions =
                    DthDetailsRechargeFragmentDirections.actionExploreSectionExplore(
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
                dthStoresListFragmentVM.callFetchOfferApi(redirectionResource)

            }


            AppConstants.FEED_TYPE_BLOG -> {
                dthStoresListFragmentVM.callFetchFeedsApi(redirectionResource)

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
                    dthStoresListFragmentVM.callFetchFeedsApi(redirectionResource)

                }

            }
        }
    }

    private fun setExploreListners() {
        dthStoresListFragmentVM.openBottomSheet.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.size > 0) {
                callOfferDetailsSheeet(list[0])
            }
        }

        dthStoresListFragmentVM.feedDetail.observe(
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




}