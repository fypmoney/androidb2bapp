package com.fypmoney.view.arcadegames.brandedcoupons.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.fypmoney.util.Utility.splitStringByDelimiters
import com.fypmoney.view.arcadegames.brandedcoupons.utils.findLocationOfCenterOnTheScreen
import com.fypmoney.view.arcadegames.brandedcoupons.viewmodel.BrandedCouponsFragmentVM
import com.google.firebase.crashlytics.FirebaseCrashlytics
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

            if (brandedCouponsFragmentVM.productId == "null") {

                setViewVisibility(binding.ivBrandedCouponsMyntsAnim, binding.ivBrandedCouponsMynts)
                brandedCouponsFragmentVM.callMyntsBurnApi(brandedCouponsFragmentVM.productCode)

            } else {
                brandedCouponsFragmentVM.callProductsDetailsApi(brandedCouponsFragmentVM.productId)

            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (brandedCouponsFragmentVM.isBrandedCouponStarted) {
                        Utility.showToast("Your reward is being processed")
                    } else {
                        if (brandedCouponsFragmentVM.isArcadeIsPlayed) {
                            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                                "arcade_is_played",
                                true
                            )
                            findNavController().popBackStack()
                        } else {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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

            setBackgroundDrawable(
                it.chipCouponCountView,
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

        brandedCouponsFragmentVM.stateCouponCount.observe(viewLifecycleOwner) {
            handleCouponCountState(it)
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

    private fun handleCouponCountState(it: BrandedCouponsFragmentVM.CouponCountState?) {
        when (it) {

            is BrandedCouponsFragmentVM.CouponCountState.CouponCountSuccess -> {
                binding.loadingActiveBrandedCoupon.clearAnimation()
                binding.loadingActiveBrandedCoupon.toGone()

                binding.tvBrandedActiveCouponsCount.text = it.amount?.toString()

            }
            is BrandedCouponsFragmentVM.CouponCountState.Error -> {}

            BrandedCouponsFragmentVM.CouponCountState.Loading -> {}

            null -> {}
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

                val arr = splitStringByDelimiters(it.couponItem.rewardColor, ",")

                binding.ivInitialCouponStage.imageTintList = ColorStateList.valueOf(
                    Color.parseColor(
                        arr?.get(0)
                    )
                )

                if (arr?.size!! > 0)
                    brandedCouponsFragmentVM.startColor = arr[0]

                if (arr.size > 1)
                    brandedCouponsFragmentVM.endColor = arr[1]

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

                couponSounds()

                decreaseCountAnimation(
                    binding.tvBrandedCouponsMyntsCount,
                    brandedCouponsFragmentVM.myntsDisplay!!
                )

                binding.motionLayoutCoupons.transitionToEnd()

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.lottieRewardConfetti.toVisible()
                    binding.lottieRewardConfetti.playAnimation()
                }, 1500)

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

                couponSounds()

                binding.motionLayoutCoupons.transitionToEnd()

                val arr = splitStringByDelimiters(it.aRewardProductResponse.rewardColor, ",")

                binding.ivInitialCouponStage.imageTintList = ColorStateList.valueOf(
                    Color.parseColor(
                        arr?.get(0)
                    )
                )

                if (arr?.size!! > 0)
                    brandedCouponsFragmentVM.startColor = arr[0]

                if (arr.size > 1)
                    brandedCouponsFragmentVM.endColor = arr[1]

                brandedCouponsFragmentVM.callPlayOrderApi(brandedCouponsFragmentVM.productId)

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.lottieRewardConfetti.toVisible()
                    binding.lottieRewardConfetti.playAnimation()
                }, 1500)
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

                increaseCountAnimation(
                    binding.tvBrandedActiveCouponsCount,
                    500,
                    1
                )

                binding.lottieRewardConfetti.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        binding.lottieRewardConfetti.toGone()
                        binding.lottieRewardConfetti.cancelAnimation()

                        val location =
                            binding.ivInitialCouponStage.findLocationOfCenterOnTheScreen()

                        val bundle =
                            bundleOf(
                                "coupon_code" to it.spinWheelResponseDetails.couponCode,
                                "REVEAL_X" to location[0], "REVEAL_Y" to location[1],
                                "startColor" to brandedCouponsFragmentVM.startColor,
                                "endColor" to brandedCouponsFragmentVM.endColor,
                            )

                        val transition =
                            FragmentNavigatorExtras(binding.ivInitialCouponStage to "detailsBackTransition")

                        findNavController().navigate(
                            R.id.navigation_branded_coupons_details, bundle, null, transition
                        )

                        binding.ivBtnPlayAnimation.toVisible()
                        binding.progressBtnPlay.toInvisible()

                        brandedCouponsFragmentVM.isBrandedCouponStarted = false

                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }

                })

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

    private fun couponSounds() {

        brandedCouponsFragmentVM.mp = MediaPlayer.create(
            this.requireContext(),
            R.raw.ticket_sound
        )

        brandedCouponsFragmentVM.mp?.start()
    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.toVisible()
        invisibleImage.toInvisible()
    }

    private fun decreaseCountAnimation(textScore: TextView, finalCount: Int) {
        if (!textScore.text.isNullOrEmpty()) {
            binding.tvPointsApiError.toGone()
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
            binding.tvPointsApiError.toVisible()
        }
    }

    private fun increaseCountAnimation(
        textScore: TextView,
        animDuration: Long,
        finalCount: Int
    ) {
        if (!textScore.text.isNullOrEmpty()) {
            binding.tvPointsApiError.toGone()
            val animator: ValueAnimator = ValueAnimator.ofInt(
                Integer.parseInt(textScore.text.toString()),
                Integer.parseInt(textScore.text.toString()) + (finalCount)
            )

            animator.duration = animDuration
            animator.addUpdateListener { animation ->
                textScore.text = animation.animatedValue.toString()
            }
            animator.start()

            Handler(Looper.getMainLooper()).postDelayed({
                binding.lottieRewardConfetti.visibility = View.INVISIBLE
            }, animDuration)
        } else {
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
            binding.tvPointsApiError.toVisible()
        }
    }

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