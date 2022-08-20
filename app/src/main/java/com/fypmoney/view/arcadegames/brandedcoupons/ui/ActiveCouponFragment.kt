package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
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
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.BrandedActiveCouponsAdapter
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.BrandedActiveCouponsUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedActiveCouponsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class ActiveCouponFragment :
    BaseFragment<FragmentActiveCouponBinding, BrandedActiveCouponsFragmentVM>() {

    private lateinit var binding: FragmentActiveCouponBinding
    private val activeCouponsFragmentVM by viewModels<BrandedActiveCouponsFragmentVM> { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

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
    }

    private fun setObserver() {

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
            handleActiveCouponState(it)
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

                if (it.activeCouponsListItem?.isEmpty() == true) {
                    binding.emptyActiveCoupon.toVisible()
                    binding.rvBrandedActiveCoupons.toInvisible()
                } else {
                    binding.emptyActiveCoupon.toInvisible()
                    binding.rvBrandedActiveCoupons.toVisible()
                }

                (binding.rvBrandedActiveCoupons.adapter as BrandedActiveCouponsAdapter).submitList(
                    it.activeCouponsListItem?.map {
                        it.let { it1 ->
                            it1?.let { it2 ->
                                BrandedActiveCouponsUiModel.fromBrandedActiveCouponItem(
                                    this.requireContext(), it2
                                )
                            }
                        }
                    }
                )
                binding.shimmerLayoutActiveBrandedCoupons.toGone()

            }
            is BrandedActiveCouponsFragmentVM.BrandedActiveCouponDataState.Error -> {}
            BrandedActiveCouponsFragmentVM.BrandedActiveCouponDataState.Loading -> {}
            null -> {}
        }
    }

    private fun setUpRecentRecyclerView() {

        val activeCouponsAdapter = BrandedActiveCouponsAdapter(onActiveCouponClick = {
            val bundle =
                bundleOf(
                    "Coupon Code" to it
                )
            findNavController().navigate(
                R.id.navigation_branded_coupons_details,
                bundle,
                navOptions {
                    anim {
                        popEnter = R.anim.slide_in_left
                        popExit = R.anim.slide_out_righ
                        enter = R.anim.slide_in_right
                        exit = R.anim.slide_out_left
                    }
                })
        })
        with(binding.rvBrandedActiveCoupons) {
            adapter = activeCouponsAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
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

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_active_coupon
    }

    override fun getViewModel(): BrandedActiveCouponsFragmentVM = activeCouponsFragmentVM

    override fun onTryAgainClicked() {}

}