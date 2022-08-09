package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentBrandedCouponDetailsBinding
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponDetailsFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class BrandedCouponDetailsFragment :
    BaseFragment<FragmentBrandedCouponDetailsBinding, BrandedCouponDetailsFragmentVM>() {

    private val brandedCouponDetailsFragmentVM by viewModels<BrandedCouponDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentBrandedCouponDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.statusBarColor = Color.parseColor("#F4DE14")

        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

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

        brandedCouponDetailsFragmentVM.couponCode = arguments?.getString("Coupon Code").toString()

        brandedCouponDetailsFragmentVM.callRewardCouponsApi(brandedCouponDetailsFragmentVM.couponCode)

        setObserver()

    }

    private fun setObserver() {
        brandedCouponDetailsFragmentVM.stateBrandedCoupon.observe(viewLifecycleOwner) {
            handleCouponDetailsState(it)
        }
    }

    private fun handleCouponDetailsState(it: BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState?) {
        when (it) {
            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.Error -> {}

            BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.Loading -> {}

            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.BrandedCouponDetailsSuccess -> {

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.couponDetailsData.brandLogo,
                    binding.ivBrandLogo
                )

                binding.tvBrandedContent.text = it.couponDetailsData.title

                binding.tvBrandedCouponCode.text = it.couponDetailsData.code

                val array: Array<String>? =
                    it.couponDetailsData.tnc?.toCharArray()?.map { it.toString() }?.toTypedArray()

                if (array != null) {
                    binding.cvCouponTerms.toVisible()
                    for (item in array.indices) {
                        val textdata: String = java.lang.String.join(",", array[item])

                        binding.tvExpandCouponTerms.text = textdata

                        Utility.showToast("List: $textdata")

                    }

                }
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_branded_coupon_details
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.statusBarColor = context?.resources?.getColor(R.color.black)!!
    }

    override fun getViewModel(): BrandedCouponDetailsFragmentVM = brandedCouponDetailsFragmentVM

    override fun onTryAgainClicked() {}

}