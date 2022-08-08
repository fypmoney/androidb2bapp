package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentBrandedCouponDetailsBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponDetailsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class BrandedCouponDetailsFragment : BaseFragment<FragmentBrandedCouponDetailsBinding, BrandedCouponDetailsFragmentVM>() {

    private val brandedCouponDetailsFragmentVM by viewModels<BrandedCouponDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentBrandedCouponDetailsBinding
    private val navArgs by navArgs<BrandedCouponDetailsFragmentArgs>()

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

        brandedCouponDetailsFragmentVM.couponCode = navArgs.couponCode

        setObserver()

    }

    private fun setObserver() {
        brandedCouponDetailsFragmentVM.stateBrandedCoupon.observe(viewLifecycleOwner){
            handleCouponDetailsState(it)
        }
    }

    private fun handleCouponDetailsState(it: BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState?) {
        when(it){
            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.Error -> {}
            BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.Loading -> {}
            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.BrandedCouponDetailsSuccess -> {

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(this.context, it.couponDetailsData.brandLogo, binding.ivBrandLogo)

                binding.tvBrandedTitle.text = it.couponDetailsData.title

                binding.tvBrandedCouponCode.text = it.couponDetailsData.code



            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_branded_coupon_details
    }

    override fun getViewModel(): BrandedCouponDetailsFragmentVM = brandedCouponDetailsFragmentVM

    override fun onTryAgainClicked() {}

}