package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentBrandedCouponsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponsFragmentVM
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.feeds_row_layout_vertical.*
import kotlinx.android.synthetic.main.toolbar.*

class BrandedCouponsFragment :
    BaseFragment<FragmentBrandedCouponsBinding, BrandedCouponsFragmentVM>() {

    private val brandedCouponsFragmentVM by viewModels<BrandedCouponsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentBrandedCouponsBinding
    private val navArgs by navArgs<BrandedCouponsFragmentArgs>()

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

        brandedCouponsFragmentVM.productCode = navArgs.productCode
        brandedCouponsFragmentVM.productId = navArgs.orderId.toString()

        Glide.with(this).load(R.drawable.coin_updated).into(binding.ivBrandedCouponsMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(binding.ivBrandedCouponsTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(binding.ivBrandedCouponsCashAnim)

        setBackgrounds()

        setObserver()

        brandedCouponsFragmentVM.callGetBrandedDataApi(brandedCouponsFragmentVM.productCode)

        binding.tvBrandedCouponsAttemptsLeft.toGone()

        binding.ivBtnPlayAnimation.setOnClickListener {

            brandedCouponsFragmentVM.isBrandedCouponStarted = true

            binding.containerBrandedCouponsRewards.toInvisible()
            binding.containerBrandedCouponsDefaultBanner.toVisible()

            binding.ivBtnPlayAnimation.toInvisible()
            binding.progressBtnPlay.toVisible()

            setViewVisibility(binding.ivBrandedCouponsMyntsAnim, binding.ivBrandedCouponsMynts)

            brandedCouponsFragmentVM.callMyntsBurnApi(brandedCouponsFragmentVM.productCode)

        }

    }

    private fun setBackgrounds() {
        binding.let {

            setBackgroundDrawable(
                it.viewMiddleContent,
                ContextCompat.getColor(this.requireContext(), R.color.editTextStrokeColor),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.editTextStrokeColor),
                0f,
                false
            )

            setBackgroundDrawable(
                it.btnPlay,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

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
                it.chipMyntsBurnView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )
        }
    }

    private fun setObserver() {

        brandedCouponsFragmentVM.remainFrequency.observe(viewLifecycleOwner) {
            binding.tvBrandedCouponsAttemptsLeft.text = String.format(
                getString(R.string.attempts_left),
                brandedCouponsFragmentVM.remainFrequency.value, brandedCouponsFragmentVM.frequency
            )
        }

        brandedCouponsFragmentVM.stateMynts.observe(viewLifecycleOwner) {
            handleMyntsState(it)
        }

        brandedCouponsFragmentVM.stateTickets.observe(viewLifecycleOwner) {
            handleTicketsState(it)
        }

        brandedCouponsFragmentVM.stateCash.observe(viewLifecycleOwner) {
            handleCashState(it)
        }

        brandedCouponsFragmentVM.stateBrandedProduct.observe(viewLifecycleOwner) {
            handleBrandedProductState(it)
        }

        brandedCouponsFragmentVM.stateMyntsBurn.observe(viewLifecycleOwner) {
            handleMyntsBurnState(it)
        }

        brandedCouponsFragmentVM.stateProductDetails.observe(viewLifecycleOwner) {
            handleProductState(it)
        }

        brandedCouponsFragmentVM.statePlayOrder.observe(viewLifecycleOwner) {
            handlePlayOrderState(it)
        }

    }

    private fun handleMyntsState(it: BrandedCouponsFragmentVM.MyntsState?) {

        when (it) {
            is BrandedCouponsFragmentVM.MyntsState.MyntsSuccess -> {
                if (it.remainingMynts != null) {

                    binding.loadingMynts.clearAnimation()
                    binding.loadingMynts.toInvisible()

                    binding.tvBrandedCouponsMyntsCount.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }
            is BrandedCouponsFragmentVM.MyntsState.Error -> {
                errorCode()
            }
            BrandedCouponsFragmentVM.MyntsState.Loading -> {}
        }

    }

    private fun handleTicketsState(it: BrandedCouponsFragmentVM.TicketState?) {
        when (it) {
            is BrandedCouponsFragmentVM.TicketState.Error -> {
                errorCode()
            }
            BrandedCouponsFragmentVM.TicketState.Loading -> {}
            is BrandedCouponsFragmentVM.TicketState.TicketSuccess -> {
                if (it.totalTickets != null) {
                    binding.loadingTickets.clearAnimation()
                    binding.loadingTickets.toInvisible()

                    binding.tvBrandedCouponsTicketsCount.text = it.totalTickets.toString()
                }
            }
        }
    }

    private fun handleCashState(it: BrandedCouponsFragmentVM.CashState?) {
        when (it) {
            is BrandedCouponsFragmentVM.CashState.CashSuccess -> {
                binding.loadingCash.clearAnimation()
                binding.loadingCash.toInvisible()

                binding.tvBrandedCouponsCashCount.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash?.toString())
                )
            }
            is BrandedCouponsFragmentVM.CashState.Error -> {
                errorCode()
            }
            BrandedCouponsFragmentVM.CashState.Loading -> {}
        }
    }

    private fun handleBrandedProductState(it: BrandedCouponsFragmentVM.BrandedCouponDataState?) {
        when (it) {
            is BrandedCouponsFragmentVM.BrandedCouponDataState.BrandedCouponSuccess -> {

                binding.loadingBurnMynts.clearAnimation()
                binding.loadingBurnMynts.visibility = View.INVISIBLE

                brandedCouponsFragmentVM.myntsDisplay = it.couponItem.appDisplayText?.toInt()

                binding.motionLayoutCoupons.toVisible()

                Glide.with(this).load(R.drawable.play_button)
                    .into(binding.ivBtnPlayAnimation)

                Glide.with(this).load(it.couponItem.detailResource)
                    .into(binding.ivBrandedLogo)

                binding.shimmerLayoutBrandedCoupons.toInvisible()

                binding.frameBtnContainer.toVisible()

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.couponItem.successResourceId,
                    binding.ivBannerBrandedCoupons
                )

                binding.tvBrandedCouponsBurnMyntsCount.text = it.couponItem.appDisplayText

            }
            is BrandedCouponsFragmentVM.BrandedCouponDataState.Error -> {
                errorCode()
            }
            BrandedCouponsFragmentVM.BrandedCouponDataState.Loading -> {}
        }
    }

    private fun handleMyntsBurnState(it: BrandedCouponsFragmentVM.MyntsBurnState?) {
        when (it) {
            is BrandedCouponsFragmentVM.MyntsBurnState.Error -> {

                errorCode()

                if (it.errorResponseInfo.errorCode == "PKT_2051") {
                    brandedCouponsFragmentVM.callInsufficientDialog(
                        it.errorResponseInfo.msg,
                        this.requireContext()
                    )
                } else if (it.errorResponseInfo.errorCode == "PKT_8100") {
                    brandedCouponsFragmentVM.callNoCouponsDialog(this.requireContext())
                }
            }
            BrandedCouponsFragmentVM.MyntsBurnState.Loading -> {}
            is BrandedCouponsFragmentVM.MyntsBurnState.MyntsBurnSuccess -> {

                brandedCouponsFragmentVM.callPlayOrderApi(it.coinsBurnedResponse.orderNo)

                arcadeSounds("SLOT")

                decreaseCountAnimation(
                    binding.tvBrandedCouponsMyntsCount,
                    brandedCouponsFragmentVM.myntsDisplay!!
                )

                binding.motionLayoutCoupons.transitionToEnd()

            }
        }
    }

    private fun handleProductState(it: BrandedCouponsFragmentVM.BrandedProductResponseState?) {
        when (it) {
            is BrandedCouponsFragmentVM.BrandedProductResponseState.Error -> {
                errorCode()
            }
            BrandedCouponsFragmentVM.BrandedProductResponseState.Loading -> {}
            is BrandedCouponsFragmentVM.BrandedProductResponseState.Success -> {

            }
        }
    }

    private fun handlePlayOrderState(it: BrandedCouponsFragmentVM.PlayOrderState?) {
        when (it) {
            is BrandedCouponsFragmentVM.PlayOrderState.Error -> {
                errorCode()
            }
            BrandedCouponsFragmentVM.PlayOrderState.Loading -> {}
            is BrandedCouponsFragmentVM.PlayOrderState.PlayOrderSuccess -> {

                findNavController().navigate(
                    R.id.navigation_branded_coupons_details
                )

                binding.ivBtnPlayAnimation.toVisible()
                binding.progressBtnPlay.toInvisible()

                Utility.showToast("CouponCode: " + it.spinWheelResponseDetails.couponCode)
            }
        }
    }

    private fun errorCode() {
        binding.ivBtnPlayAnimation.toVisible()
        binding.progressBtnPlay.toInvisible()

        brandedCouponsFragmentVM.isBrandedCouponStarted = false

        setViewVisibility(
            binding.ivBrandedCouponsMynts,
            binding.ivBrandedCouponsMyntsAnim
        )
        setViewVisibility(
            binding.ivBrandedCouponsCash,
            binding.ivBrandedCouponsCashAnim
        )
        setViewVisibility(
            binding.ivBrandedCouponsTicket,
            binding.ivBrandedCouponsTicketAnim
        )
    }

    private fun arcadeSounds(from: String) {

        brandedCouponsFragmentVM.mp?.stop()

        when (from) {
            "MYNTS" -> {
                brandedCouponsFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_mynts
                )
            }
            "TICKETS" -> {
                brandedCouponsFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_golden_ticket
                )
            }
            "SLOT" -> {
                brandedCouponsFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_slot_machine
                )
            }
            else -> {
                brandedCouponsFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_cashback
                )
            }
        }
        brandedCouponsFragmentVM.mp?.start()
    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.toVisible()
        invisibleImage.toInvisible()
    }

    private fun decreaseCountAnimation(textScore: TextView, finalCount: Int) {
        if (!textScore.text.isNullOrEmpty()) {
            val animator = ValueAnimator.ofInt(
                Integer.parseInt(textScore.text.toString()),
                Integer.parseInt(textScore.text.toString()) - (finalCount)
            )
            animator.duration = 1500
            animator.addUpdateListener { animation ->
                textScore.text = animation.animatedValue.toString()
            }
            animator.start()

            Handler(Looper.getMainLooper()).postDelayed({
                setViewVisibility(
                    binding.ivBrandedCouponsMynts,
                    binding.ivBrandedCouponsMyntsAnim
                )
            }, 1500)
        } else {
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
            Utility.showToast("Please check history")
        }
    }

//    private fun increaseCountAnimation(
//        textScore: TextView,
//        animDuration: Long,
//        finalCount: Int,
//        via: String
//    ) {
//
//        if (!textScore.text.isNullOrEmpty()) {
//            if (via == "Cash") {
//                val startPosition = (textScore.text.toString().split("₹")[1]).toIntOrNull()
//                val endPosition = (textScore.text.toString().split("₹")[1]).toIntOrNull()
//                if (startPosition == null || endPosition == null) {
//                    textScore.text = String.format(
//                        getString(R.string.arcade_cash_value),
//                        (textScore.text.toString().split("₹")[1]).toDouble() + finalCount
//                    )
//                } else {
//                    val animator: ValueAnimator =
//                        ValueAnimator.ofInt(
//                            (textScore.text.toString().split("₹")[1]).toInt(),
//                            (textScore.text.toString().split("₹")[1]).toInt() + (finalCount)
//                        )
//
//                    animator.duration = animDuration
//                    animator.addUpdateListener { animation ->
//                        textScore.text = String.format(
//                            getString(R.string.arcade_cash_value),
//                            animation.animatedValue.toString()
//                        )
//                    }
//                    animator.start()
//                }
//            } else {
//                val animator: ValueAnimator = ValueAnimator.ofInt(
//                    Integer.parseInt(textScore.text.toString()),
//                    Integer.parseInt(textScore.text.toString()) + (finalCount)
//                )
//
//                animator.duration = animDuration
//                animator.addUpdateListener { animation ->
//                    textScore.text = animation.animatedValue.toString()
//                }
//                animator.start()
//            }
//
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                setViewVisibility(
//                    binding.ivBrandedCouponsMynts,
//                    binding.ivBrandedCouponsMyntsAnim
//                )
//                setViewVisibility(
//                    binding.ivBrandedCouponsTicket,
//                    binding.ivBrandedCouponsTicketAnim
//                )
//                setViewVisibility(
//                    binding.ivBrandedCouponsCash,
//                    binding.ivBrandedCouponsCashAnim
//                )
//                binding.lottieRewardConfetti.visibility = View.INVISIBLE
//            }, animDuration)
//        } else {
//            FirebaseCrashlytics.getInstance()
//                .recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
//            Utility.showToast("Please check history")
//        }
//    }

    override fun onTryAgainClicked() {}

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_branded_coupons
    }

    override fun getViewModel(): BrandedCouponsFragmentVM = brandedCouponsFragmentVM

    override fun onStop() {
        super.onStop()
        brandedCouponsFragmentVM.mp?.stop()
    }
}