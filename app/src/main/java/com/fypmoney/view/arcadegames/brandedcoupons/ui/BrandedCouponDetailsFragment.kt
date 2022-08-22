package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentBrandedCouponDetailsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.StoreWebpageOpener2
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.CouponDetailsTitleAdapter
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.CouponDetailsTitleUiModel
import com.fypmoney.view.arcadegames.brandedcoupons.adapter.OnDetailsClicked
import com.fypmoney.view.arcadegames.brandedcoupons.utils.startCircularReveal
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponDetailsFragmentVM
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray

class BrandedCouponDetailsFragment :
    BaseFragment<FragmentBrandedCouponDetailsBinding, BrandedCouponDetailsFragmentVM>(),
    OnDetailsClicked {

    private val brandedCouponDetailsFragmentVM by viewModels<BrandedCouponDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentBrandedCouponDetailsBinding
    private var listOfCouponDetailsTitle = arrayListOf<CouponDetailsTitleUiModel>()

    private var revealX: Int = 0
    private var revealY: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        if (arguments?.getString("startColor") != null && arguments?.getString("endColor") != null) {
            brandedCouponDetailsFragmentVM.startColor =
                arguments?.getString("startColor").toString()
            brandedCouponDetailsFragmentVM.endColor = arguments?.getString("endColor").toString()
            backgroundGradientDrawable(
                Color.parseColor(brandedCouponDetailsFragmentVM.startColor),
                Color.parseColor(brandedCouponDetailsFragmentVM.endColor)
            )
            activity?.window?.statusBarColor =
                Color.parseColor(brandedCouponDetailsFragmentVM.startColor)

        }

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        brandedCouponDetailsFragmentVM.couponCode = arguments?.getString("coupon_code").toString()

        revealX = arguments?.getInt("REVEAL_X")!!
        revealY = arguments?.getInt("REVEAL_Y")!!

        brandedCouponDetailsFragmentVM.callRewardCouponsApi(brandedCouponDetailsFragmentVM.couponCode)

        if (revealX > 0 && revealY > 0) {

            sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move)
            view.startCircularReveal(revealX, revealY)
        }

        setObserver()

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (revealX > 0 && revealY > 0)
                        findNavController().navigate(R.id.action_navigation_branded_coupons_details_to_navigation_rewards)
                    else
                        findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setObserver() {
        brandedCouponDetailsFragmentVM.stateBrandedCoupon.observe(viewLifecycleOwner) {
            handleCouponDetailsState(it)
        }
    }

    private fun handleCouponDetailsState(it: BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState?) {
        when (it) {
            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.Error -> {}

            BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.Loading -> {
                binding.shimmerCouponDetails.toVisible()
                binding.nestedBrandedCouponContainer.toGone()
                binding.clBrandedCouponBottom.toGone()
            }

            is BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.BrandedCouponDetailsSuccess -> {

                setBackColor(it)
                binding.nestedBrandedCouponContainer.toVisible()
                binding.clBrandedCouponBottom.toVisible()
                binding.shimmerCouponDetails.toGone()

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.couponDetailsData.brandLogo,
                    binding.ivBrandLogo
                )

                binding.tvBrandedContent.text = it.couponDetailsData.title

                binding.tvBrandedCouponCode.text = it.couponDetailsData.code

                val couponCode = it.couponDetailsData.code

                if (it.couponDetailsData.type.equals("ADVERTISEMENT")) {
                    binding.tvBrandedCouponCodeText.toGone()
                    binding.clCouponCode.toGone()
                } else {
                    binding.tvBrandedCouponCodeText.toVisible()
                    binding.clCouponCode.toVisible()
                }

                binding.ivCouponCodeCopy.setOnClickListener {
                    if (!couponCode.isNullOrEmpty()) {

                        val clipboardManager = getSystemService(
                            requireContext(),
                            ClipboardManager::class.java
                        ) as ClipboardManager

                        val clipData = ClipData.newPlainText(
                            "label",
                            binding.tvBrandedCouponCode.text.toString()
                        )
                        clipboardManager.setPrimaryClip(clipData)
                        Utility.showToast("Coupon code copied")
                    } else {
                        Utility.showToast("Some error...")
                    }
                }

                val redeemLink: String? = it.couponDetailsData.redeemLink
                binding.btnCouponRedeemNow.setOnClickListener {
                    if (!redeemLink.isNullOrEmpty()) {
                        val intent = Intent(context, StoreWebpageOpener2::class.java)
//                    intent.putExtra(ARG_WEB_PAGE_TITLE, it.title)
                        intent.putExtra(ARG_WEB_URL_TO_OPEN, redeemLink)
                        startActivity(intent)
                    } else {
                        Utility.showToast("Some error...")
                    }
                }

                val jsonArr = JSONArray(it.couponDetailsData.tnc)
                val tncArray: ArrayList<String> = ArrayList()
                for (i in 0 until jsonArr.length()) {
                    tncArray.add(jsonArr[i] as String)
                }

                val jsonArr1 = JSONArray(it.couponDetailsData.howToRedeem)
                val howToRedeemArray: ArrayList<String> = ArrayList()
                for (i in 0 until jsonArr1.length()) {
                    howToRedeemArray.add(jsonArr1[i] as String)
                }

                val jsonArr2 = JSONArray(it.couponDetailsData.description)
                val offerDetailsArray: ArrayList<String> = ArrayList()
                for (i in 0 until jsonArr2.length()) {
                    offerDetailsArray.add(jsonArr2[i] as String)
                }

                listOfCouponDetailsTitle.add(
                    CouponDetailsTitleUiModel(
                        "How to redeem?",
                        R.drawable.icon_discount,
                        howToRedeemArray,
                        false
                    )
                )
                listOfCouponDetailsTitle.add(
                    CouponDetailsTitleUiModel(
                        "Offer details",
                        R.drawable.ic_receipt_search,
                        offerDetailsArray,
                        false
                    )
                )
                listOfCouponDetailsTitle.add(
                    CouponDetailsTitleUiModel(
                        "Terms & Condition",
                        R.drawable.ic_receipt_item,
                        tncArray,
                        false
                    )
                )
                setRecyclerView(tncArray, howToRedeemArray, offerDetailsArray)
            }
        }
    }

    private fun setRecyclerView(
        array: List<String>?,
        howToRedeemArray: List<String>?,
        offerDetailsArray: List<String>?
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCouponDetailsTitle.layoutManager = layoutManager

        val adapter = CouponDetailsTitleAdapter(this@BrandedCouponDetailsFragment)
        binding.rvCouponDetailsTitle.adapter = adapter
        adapter.submitList(listOfCouponDetailsTitle)
    }

    private fun updatedList(it: CouponDetailsTitleUiModel) {
        listOfCouponDetailsTitle.map { it1 ->
            it1.isExpended = it1.couponDetailsTitle == it.couponDetailsTitle
        }
        (binding.rvCouponDetailsTitle.adapter as CouponDetailsTitleAdapter).submitList(
            null
        )
        (binding.rvCouponDetailsTitle.adapter as CouponDetailsTitleAdapter).submitList(
            listOfCouponDetailsTitle
        )
    }

    private fun setBackColor(brandedCouponDetailsState: BrandedCouponDetailsFragmentVM.BrandedCouponDetailsState.BrandedCouponDetailsSuccess) {
        val arr = Utility.splitStringByDelimiters(
            brandedCouponDetailsState.couponDetailsData.rewardColor,
            ","
        )

        if (arr?.size!! > 1) {
            backgroundGradientDrawable(
                Color.parseColor(arr[0]),
                Color.parseColor(arr[1])
            )
            activity?.window?.statusBarColor =
                Color.parseColor(arr[0])

        }
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