package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentBrandedCouponDetailsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.COUPON_DETAILS_NOT_FOUND
import com.fypmoney.util.Utility
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.CouponDetailsTitleAdapter
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.CouponDetailsTitleUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.OnDetailsClicked
import com.fypmoney.view.arcadegames.brandedcoupons.model.BrandedCouponDetailsUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.utils.startCircularReveal
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponDetailsFragmentVM
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.toolbar.*

class BrandedCouponDetailsFragment :
    BaseFragment<FragmentBrandedCouponDetailsBinding, BrandedCouponDetailsFragmentVM>(),
    OnDetailsClicked {

    private val brandedCouponDetailsFragmentVM by viewModels<BrandedCouponDetailsFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var binding: FragmentBrandedCouponDetailsBinding

    private var revealX: Int = 0
    private var revealY: Int = 0

    private val args: BrandedCouponDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        args.brandedCouponDetailsData?.let {
            brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel = it
        }?: kotlin.run {
            FirebaseCrashlytics.getInstance().recordException(Exception(COUPON_DETAILS_NOT_FOUND))
            findNavController().popBackStack()
        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel.via == "Ticket")
                        findNavController().navigate(R.id.action_navigation_branded_coupons_details_to_navigation_rewards)
                    else
                        findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        revealX = brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel.revealX!!
        revealY = brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel.revealY!!

        if (revealX > 0 && revealY > 0) {
            view.startCircularReveal(revealX, revealY)
        }


        setRecyclerView()
        setupObserver()
        brandedCouponDetailsFragmentVM.showDetails()
    }

    private fun setupObserver() {
        brandedCouponDetailsFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        brandedCouponDetailsFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState?) {
        when(it){
            BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.ErrorWhileShowingCouponDetails -> {

            }
            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.PopulateCouponDetails -> {
                handleCouponDetailsState(it.brandedCouponDetailsUiModel)
                (binding.rvCouponDetailsTitle.adapter as CouponDetailsTitleAdapter).submitList(it.listOfCouponDetails)

            }
            else->{

            }
        }
    }

    private fun handelEvent(it: BrandedCouponDetailsFragmentVM.BrandedCouponEvent?) {
        when(it){
            BrandedCouponDetailsFragmentVM.BrandedCouponEvent.CopyCouponCode -> {
                Utility.copyTextToClipBoard(
                    requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager,
                    brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel.couponCode
                )
            }
            BrandedCouponDetailsFragmentVM.BrandedCouponEvent.RedeemNow -> {
                    if (!brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel.redeemLink.isNullOrEmpty()) {
                        val intent = Intent(context, StoreWebpageOpener2::class.java)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, brandedCouponDetailsFragmentVM.brandedCouponDetailsUiModel.redeemLink)
                        startActivity(intent)
                    }
                }

            null -> {

            }
        }
    }

    private fun handleCouponDetailsState(it: BrandedCouponDetailsUiModel) {

        setBackColor(it.startColor, it.endColor)
        binding.nestedBrandedCouponContainer.toVisible()
        binding.clBrandedCouponBottom.toVisible()
        binding.shimmerCouponDetails.toGone()

        Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
            this.context,
            it.brandLogo,
            binding.ivBrandLogo
        )

        binding.tvBrandedContent.text = it.couponTitle

        binding.tvBrandedCouponCode.text = it.couponCode

        if (it.brandType.equals("ADVERTISEMENT")) {
            binding.tvBrandedCouponCodeText.toGone()
            binding.clCouponCode.toGone()
        } else {
            binding.tvBrandedCouponCodeText.toVisible()
            binding.clCouponCode.toVisible()
        }

    }

    private fun setRecyclerView(
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCouponDetailsTitle.layoutManager = layoutManager

        val adapter = CouponDetailsTitleAdapter(this@BrandedCouponDetailsFragment)
        binding.rvCouponDetailsTitle.adapter = adapter
    }

    private fun updatedList(couponDetailsTitleUiModel: CouponDetailsTitleUiModel) {
        val list = (binding.rvCouponDetailsTitle.adapter as CouponDetailsTitleAdapter).currentList

        val newList = mutableListOf<CouponDetailsTitleUiModel>()
         list.forEach { it ->
           newList.add(
               CouponDetailsTitleUiModel(
                   couponDetailsTitle = it.couponDetailsTitle,
                   couponDetailsIcon = it.couponDetailsIcon,
                   arrayCouponDetails = it.arrayCouponDetails,
                   isExpended = if(it.couponDetailsTitle==couponDetailsTitleUiModel.couponDetailsTitle)  couponDetailsTitleUiModel.isExpended else false
               )
           )
        }
        (binding.rvCouponDetailsTitle.adapter as CouponDetailsTitleAdapter).submitList(
            newList
        )
    }

    private fun setBackColor(startColor: String?, endColor: String?) {

        backgroundGradientDrawable(
            Color.parseColor(startColor),
            Color.parseColor(endColor)
        )
        activity?.window?.statusBarColor =
            Color.parseColor(startColor)

    }

    private fun backgroundGradientDrawable(
        @ColorInt startColor: Int,
        @ColorInt endColor: Int
    ) {

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                startColor,
                endColor
            )
        )
        gradientDrawable.cornerRadius = 0f

        binding.clCouponsDetailedContainer.background = gradientDrawable

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_branded_coupon_details
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.window?.statusBarColor =
            ContextCompat.getColor(this.requireContext(), R.color.black)
    }

    override fun getViewModel(): BrandedCouponDetailsFragmentVM = brandedCouponDetailsFragmentVM

    override fun onTryAgainClicked() {}

    override fun expendDetails(copounDetailsItem: CouponDetailsTitleUiModel) {
        updatedList(copounDetailsItem)
    }

}