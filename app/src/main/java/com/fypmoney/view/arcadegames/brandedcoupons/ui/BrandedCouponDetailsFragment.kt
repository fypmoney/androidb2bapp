package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
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
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponDetailsFragmentVM
import com.fypmoney.view.webview.ARG_WEB_URL_TO_OPEN
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray

class BrandedCouponDetailsFragment :
    BaseFragment<FragmentBrandedCouponDetailsBinding, BrandedCouponDetailsFragmentVM>() {

    private val brandedCouponDetailsFragmentVM by viewModels<BrandedCouponDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentBrandedCouponDetailsBinding
    private var listOfCouponDetailsTitle = arrayListOf<CouponDetailsTitleUiModel>()

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

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_navigation_branded_coupons_details_to_navigation_rewards)
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

//                val tncArray: List<String>? = it.couponDetailsData.tnc?.split("\",\"")
//                val howToRedeemArray: List<String>? =
//                    it.couponDetailsData.howToRedeem?.split("\",\"")
//                val offerDetailsArray: List<String>? =
//                    it.couponDetailsData.description?.split("\",\"")

                listOfCouponDetailsTitle.add(
                    CouponDetailsTitleUiModel(
                        "How to redeem?",
                        R.drawable.icon_discount
                    )
                )
                listOfCouponDetailsTitle.add(
                    CouponDetailsTitleUiModel(
                        "Offer details",
                        R.drawable.ic_receipt_search
                    )
                )
                listOfCouponDetailsTitle.add(
                    CouponDetailsTitleUiModel(
                        "Terms & Condition",
                        R.drawable.ic_receipt_item
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

        val adapter = CouponDetailsTitleAdapter(array, howToRedeemArray, offerDetailsArray)
        adapter.submitList(listOfCouponDetailsTitle)
        binding.rvCouponDetailsTitle.adapter = adapter
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