package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentActiveCouponBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.ActiveBrandedCouponAdapter
import com.fypmoney.view.arcadegames.brandedcoupons.model.ActiveCouponsListItem
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponDetailsUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.model.CouponDetails
import com.fypmoney.view.arcadegames.brandedcoupons.utils.GridPaginationListener
import com.fypmoney.view.arcadegames.brandedcoupons.utils.findLocationOfCenterOnTheScreen
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedActiveCouponsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray

class ActiveCouponFragment :
    BaseFragment<FragmentActiveCouponBinding, BrandedActiveCouponsFragmentVM>() {

    private lateinit var binding: FragmentActiveCouponBinding
    private val activeCouponsFragmentVM by viewModels<BrandedActiveCouponsFragmentVM> { defaultViewModelProviderFactory }
    private var isLoading = false
    private var itemsArrayList: ArrayList<ActiveCouponsListItem> = ArrayList()
    private var activeCouponsAdapter: ActiveBrandedCouponAdapter? = null

    companion object {
        var page = 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()


        activeCouponsFragmentVM.callBrandedActiveCouponData(page)

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        setBackgrounds()

        setObserver()

        setUpRecentRecyclerView()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (arguments?.getString("via").equals("Coupon"))
                        findNavController().navigate(R.id.action_navigation_active_coupon_fragment_to_navigation_rewards)
                    else
                        findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setObserver() {

        page = 0

        activeCouponsFragmentVM.stateMynts.observe(viewLifecycleOwner) {
            handleMyntsState(it)
        }

        activeCouponsFragmentVM.stateTickets.observe(viewLifecycleOwner) {
            handleTicketsState(it)
        }

        activeCouponsFragmentVM.stateCash.observe(viewLifecycleOwner) {
            handleCashState(it)
        }

        activeCouponsFragmentVM.stateCouponCount.observe(viewLifecycleOwner) {
            handleCouponCountState(it)
        }

        activeCouponsFragmentVM.stateBrandedActiveCoupons.observe(viewLifecycleOwner) {
            binding.progressActiveCoupons.toGone()
            handleActiveCouponState(it)
        }

        activeCouponsFragmentVM.stateBrandedCoupon.observe(viewLifecycleOwner) {
            handleCouponDetailsState(it)
        }

    }

    private fun handleMyntsState(it: BrandedActiveCouponsFragmentVM.MyntsState?) {

        when (it) {
            is BrandedActiveCouponsFragmentVM.MyntsState.MyntsSuccess -> {
                if (it.remainingMynts != null) {

                    binding.loadingMynts.clearAnimation()
                    binding.loadingMynts.toGone()

                    binding.tvBrandedCouponsMyntsCount.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }
            is BrandedActiveCouponsFragmentVM.MyntsState.Error -> {}
            BrandedActiveCouponsFragmentVM.MyntsState.Loading -> {}
        }

    }

    private fun handleTicketsState(it: BrandedActiveCouponsFragmentVM.TicketState?) {
        when (it) {
            is BrandedActiveCouponsFragmentVM.TicketState.Error -> {}
            BrandedActiveCouponsFragmentVM.TicketState.Loading -> {}
            is BrandedActiveCouponsFragmentVM.TicketState.TicketSuccess -> {
                if (it.totalTickets != null) {
                    binding.loadingTickets.clearAnimation()
                    binding.loadingTickets.toGone()

                    binding.tvBrandedCouponsTicketsCount.text = it.totalTickets.toString()
                }
            }
        }
    }

    private fun handleCashState(it: BrandedActiveCouponsFragmentVM.CashState?) {
        when (it) {
            is BrandedActiveCouponsFragmentVM.CashState.CashSuccess -> {
                binding.loadingCash.clearAnimation()
                binding.loadingCash.toGone()

                binding.tvBrandedCouponsCashCount.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash?.toString())
                )
            }
            is BrandedActiveCouponsFragmentVM.CashState.Error -> {}
            BrandedActiveCouponsFragmentVM.CashState.Loading -> {}
        }
    }

    private fun handleCouponCountState(it: BrandedActiveCouponsFragmentVM.CouponCountState?) {
        when (it) {

            is BrandedActiveCouponsFragmentVM.CouponCountState.CouponCountSuccess -> {
                binding.loadingActiveBrandedCoupon.clearAnimation()
                binding.loadingActiveBrandedCoupon.toGone()

                binding.tvBrandedActiveCouponsCount.text = it.amount?.toString()

            }
            is BrandedActiveCouponsFragmentVM.CouponCountState.Error -> {}

            BrandedActiveCouponsFragmentVM.CouponCountState.Loading -> {}

            null -> {}
        }
    }

    private fun handleActiveCouponState(it: BrandedActiveCouponsFragmentVM.BrandedActiveCouponDataState?) {

        when (it) {
            is BrandedActiveCouponsFragmentVM.BrandedActiveCouponDataState.BrandedCouponSuccess -> {

                if (page == 0)
                    itemsArrayList.clear()

                it.activeCouponsListItem?.forEach { it1 ->
                    if (it1 != null) {
                        itemsArrayList.add(it1)
                    }
                }

                isLoading = false
                activeCouponsAdapter?.notifyDataSetChanged()

                if (itemsArrayList.isEmpty()) {
                    binding.emptyActiveCoupon.toVisible()
                    binding.rvBrandedActiveCoupons.toInvisible()
                } else {
                    binding.emptyActiveCoupon.toInvisible()
                    binding.rvBrandedActiveCoupons.toVisible()
                }

                page += 1

//                (binding.rvBrandedActiveCoupons.adapter as BrandedActiveCouponsAdapter).submitList(
//                    it.activeCouponsListItem?.map {
//                        it.let { it1 ->
//                            it1?.let { it2 ->
//                                BrandedActiveCouponsUiModel.fromBrandedActiveCouponItem(
//                                    this.requireContext(), it2
//                                )
//                            }
//                        }
//                    }
//                )
                binding.shimmerLayoutActiveBrandedCoupons.toGone()

            }
            is BrandedActiveCouponsFragmentVM.BrandedActiveCouponDataState.Error -> {}
            BrandedActiveCouponsFragmentVM.BrandedActiveCouponDataState.Loading -> {}
            null -> {}
        }
    }

    private fun handleCouponDetailsState(it: BrandedActiveCouponsFragmentVM.BrandedCouponDetailsState?) {
        when (it) {
            is BrandedActiveCouponsFragmentVM.BrandedCouponDetailsState.Error -> {}

            BrandedActiveCouponsFragmentVM.BrandedCouponDetailsState.Loading -> {

            }

            is BrandedActiveCouponsFragmentVM.BrandedCouponDetailsState.BrandedCouponDetailsSuccess -> {

                activeCouponsFragmentVM.couponDetailsData = it.couponDetailsListData

                setCouponDetailsData(it.couponDetailsListData)
            }
        }
    }

    private fun setUpRecentRecyclerView() {
        activeCouponsAdapter = ActiveBrandedCouponAdapter(onActiveCouponClick = {
            activeCouponsFragmentVM.callRewardCouponsApi(it)
        }, itemsArrayList, this.requireContext())

        with(binding.rvBrandedActiveCoupons) {
            adapter = activeCouponsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)

            addOnScrollListener(object :
                GridPaginationListener(layoutManager as GridLayoutManager) {
                override fun loadMoreItems() {
                    loadMore()
                }

                override fun loadMoreTopItems() {}

                override fun isLoading(): Boolean {
                    return isLoading
                }

            })
        }
    }

    private fun loadMore() {
        activeCouponsFragmentVM.callBrandedActiveCouponData(page)
        binding.progressActiveCoupons.toVisible()
        isLoading = true
    }

    private fun setBackgrounds() {
        binding.let {

            setBackgroundDrawable(
                it.chipMyntsView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipTicketView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipCashView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipCouponActiveView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )
        }
    }

    private fun setCouponDetailsData(couponDetailsData: CouponDetails) {

        val location =
            binding.rvBrandedActiveCoupons.findLocationOfCenterOnTheScreen()

        val jsonArr =
            JSONArray(couponDetailsData.tnc)
        val tnc: ArrayList<String> = ArrayList()
        for (i in 0 until jsonArr.length()) {
            tnc.add(jsonArr[i] as String)
        }

        val jsonArr1 =
            JSONArray(couponDetailsData.howToRedeem)
        val howToRedeem: ArrayList<String> = ArrayList()
        for (i in 0 until jsonArr1.length()) {
            howToRedeem.add(jsonArr1[i] as String)
        }

        val jsonArr2 =
            JSONArray(couponDetailsData.description)
        val description: ArrayList<String> = ArrayList()
        for (i in 0 until jsonArr2.length()) {
            description.add(jsonArr2[i] as String)
        }

        val arr = Utility.splitStringByDelimiters(
            couponDetailsData.rewardColor,
            ","
        )

        var startColor: String? = null
        var endColor: String? = null

        if (arr?.size!! > 0)
            startColor = arr[0]

        if (arr.size > 1)
            endColor = arr[1]

        val directions =
            ActiveCouponFragmentDirections.actionNavigationActiveCouponFragmentToNavigationBrandedCouponsDetails(
                BrandedCouponDetailsUiModel(
                    howToRedeem = howToRedeem,
                    tnc = tnc,
                    description = description,
                    couponCode = couponDetailsData.code,
                    brandLogo = couponDetailsData.brandLogo,
                    couponTitle = couponDetailsData.title,
                    startColor = startColor,
                    endColor = endColor,
                    redeemLink = couponDetailsData.redeemLink,
                    brandType = couponDetailsData.type,
                    revealX = location[0],
                    revealY = location[1],
                    via = "Active"
                )
            )

        directions.let {
            findNavController().navigate(it)
        }
    }

    override fun onStart() {
        super.onStart()

        page = 0
    }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_active_coupon
    }

    override fun getViewModel(): BrandedActiveCouponsFragmentVM = activeCouponsFragmentVM

    override fun onTryAgainClicked() {}

}