package com.fypmoney.view.arcadegames.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentSlotMachineBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.SlotMachineImageEventEnd
import com.fypmoney.view.arcadegames.viewmodel.SlotMachineFragmentVM
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_slot_machine_image_scrolling.view.*

class SlotMachineFragment : BaseFragment<FragmentSlotMachineBinding, SlotMachineFragmentVM>(),
    SlotMachineImageEventEnd {

    private val slotMachineFragmentVM by viewModels<SlotMachineFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentSlotMachineBinding
    private val navArgs by navArgs<SlotMachineFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getViewDataBinding()

        binding.loadingMynts.toVisible()
        binding.loadingTickets.toVisible()
        binding.loadingCash.toVisible()

        setEvents()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        slotMachineFragmentVM.productCode = navArgs.productCode
        slotMachineFragmentVM.productId = navArgs.orderId.toString()

        Glide.with(this).load(R.drawable.coin_updated).into(binding.ivSlotMachineMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(binding.ivSlotMachineTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(binding.ivSlotMachineCashAnim)

        setBackgrounds()

        setObserver()

        slotMachineFragmentVM.callGetProductDataApi(slotMachineFragmentVM.productCode)

        binding.ivBtnPlayAnimation.setOnClickListener {

            if (slotMachineFragmentVM.productId == null || slotMachineFragmentVM.productId == "null") {

                if (slotMachineFragmentVM.remainFrequency.value!! > 0) {

                    slotMachineFragmentVM.isSlotMachineStarted = true

                    binding.containerSlotMachineRewards.toInvisible()
                    binding.containerSlotMachineDefaultBanner.toVisible()

                    binding.ivBtnPlayAnimation.toInvisible()
                    binding.progressBtnPlay.toVisible()

                    setViewVisibility(binding.ivSlotMachineMyntsAnim, binding.ivSlotMachineMynts)

                    slotMachineFragmentVM.callMyntsBurnApi(slotMachineFragmentVM.productCode)

                } else {
                    val limitOverBottomSheet = LimitOverBottomSheet()
                    limitOverBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                    limitOverBottomSheet.show(childFragmentManager, "LimitOverBottomSheet")
                }
            } else {

                slotMachineFragmentVM.isSlotMachineStarted = true

                binding.containerSlotMachineRewards.toInvisible()
                binding.containerSlotMachineDefaultBanner.toVisible()

                binding.ivBtnPlayAnimation.toInvisible()
                binding.progressBtnPlay.toVisible()

                binding.tvSlotMachineAttemptsLeft.toInvisible()

                slotMachineFragmentVM.callProductsDetailsApi(slotMachineFragmentVM.productId)
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (slotMachineFragmentVM.isSlotMachineStarted) {
                        Utility.showToast("Your reward is being processed")
                    } else {
                        if (slotMachineFragmentVM.isArcadeIsPlayed) {
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

    private fun setEvents() {
        binding.image.setEventEnd(this@SlotMachineFragment)
        binding.image1.setEventEnd(this@SlotMachineFragment)
        binding.image2.setEventEnd(this@SlotMachineFragment)
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

        slotMachineFragmentVM.remainFrequency.observe(viewLifecycleOwner) {
            binding.tvSlotMachineAttemptsLeft.text = String.format(
                getString(R.string.attempts_left),
                slotMachineFragmentVM.remainFrequency.value, slotMachineFragmentVM.frequency
            )
        }

        slotMachineFragmentVM.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

        slotMachineFragmentVM.stateProductDetails.observe(viewLifecycleOwner) {
            handleProductState(it)
        }

        slotMachineFragmentVM.stateMynts.observe(viewLifecycleOwner) {
            handleMyntsState(it)
        }

        slotMachineFragmentVM.stateCash.observe(viewLifecycleOwner) {
            handleCashState(it)
        }

        slotMachineFragmentVM.stateTickets.observe(viewLifecycleOwner) {
            handleTicketsState(it)
        }

        slotMachineFragmentVM.stateMyntsBurn.observe(viewLifecycleOwner) {
            handleMyntsBurnState(it)
        }

        slotMachineFragmentVM.statePlayOrder.observe(viewLifecycleOwner) {
            handlePlayOrderState(it)
        }

    }

    private fun handleProductState(it: SlotMachineFragmentVM.SlotMachineProductResponseState?) {
        when (it) {
            is SlotMachineFragmentVM.SlotMachineProductResponseState.Success -> {

                val sApiShowData = it.aRewardProductResponse.sectionCode?.toCharArray()

                if (sApiShowData != null) {

                    arcadeSounds("SLOT")

                    slotMachineFragmentVM.callPlayOrderApi(slotMachineFragmentVM.productId)

                    binding.image.currentImage.toVisible()
                    binding.image1.currentImage.toVisible()
                    binding.image2.currentImage.toVisible()

                    binding.image.setValueRandom(
                        sApiShowData[0].toInt(), (20..23).random()
                    )
                    binding.image1.setValueRandom(
                        sApiShowData[1].toInt(), (23..27).random()
                    )
                    binding.image2.setValueRandom(
                        sApiShowData[2].toInt(), (27..30).random()
                    )
                } else {
                    binding.ivBtnPlayAnimation.toVisible()
                    binding.progressBtnPlay.toInvisible()
                    Utility.showToast("Please try after sometime")
                }

            }

            SlotMachineFragmentVM.SlotMachineProductResponseState.Loading -> {}

            is SlotMachineFragmentVM.SlotMachineProductResponseState.Error -> {

                binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                binding.progressBtnPlay.visibility = View.INVISIBLE

                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }

        }
    }

    private fun handleState(it: SlotMachineFragmentVM.SlotMachineState?) {
        when (it) {
            is SlotMachineFragmentVM.SlotMachineState.Success -> {

                binding.loadingBurnMynts.clearAnimation()
                binding.loadingBurnMynts.visibility = View.INVISIBLE

                slotMachineFragmentVM.myntsDisplay = it.slotItem.appDisplayText?.toInt()

                Glide.with(this).load(R.drawable.play_button)
                    .into(binding.ivBtnPlayAnimation)

                binding.shimmerLayoutSpinWheel.toInvisible()
                binding.frameBtnContainer.toVisible()
                binding.luckyLayout.toVisible()

                if (slotMachineFragmentVM.productId == "null")
                    binding.tvSlotMachineAttemptsLeft.toVisible()
                else
                    binding.tvSlotMachineAttemptsLeft.toInvisible()

                binding.ivSlotMachineEllipse.toVisible()

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.slotItem.successResourceId,
                    binding.ivBannerSlotMachine
                )

                binding.tvSlotMachineBurnMyntsCount.text = it.slotItem.appDisplayText
                slotMachineFragmentVM.myntsCount = it.slotItem.appDisplayText?.let { it1 ->
                    slotMachineFragmentVM.myntsCount?.plus(
                        it1.toFloat()
                    )
                }

                slotMachineFragmentVM.frequency = it.slotItem.frequency

                slotMachineFragmentVM.remainFrequency.value =
                    it.slotItem.frequencyPlayed?.let { it1 ->
                        it.slotItem.frequency?.minus(
                            it1
                        )
                    }
            }

            is SlotMachineFragmentVM.SlotMachineState.Error -> {
                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }

            SlotMachineFragmentVM.SlotMachineState.Loading -> {}
        }
    }

    private fun handleMyntsBurnState(it: SlotMachineFragmentVM.MyntsBurnSuccessState?) {
        when (it) {
            SlotMachineFragmentVM.MyntsBurnSuccessState.Loading -> {}

            is SlotMachineFragmentVM.MyntsBurnSuccessState.Error -> {
                if (it.errorResponseInfo.errorCode == "PKT_2051") {
                    slotMachineFragmentVM.callInsufficientDialog(
                        it.errorResponseInfo.msg,
                        this.requireContext()
                    )
                    binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                    binding.progressBtnPlay.visibility = View.INVISIBLE
                } else {
                    binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                    binding.progressBtnPlay.visibility = View.INVISIBLE
                }

                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }

            is SlotMachineFragmentVM.MyntsBurnSuccessState.MyntsBurnSuccess -> {

                val sApiShowData = it.coinsBurnedResponse.sectionCode?.toCharArray()

                if (sApiShowData != null) {

                    arcadeSounds("SLOT")

                    slotMachineFragmentVM.remainFrequency.value =
                        slotMachineFragmentVM.remainFrequency.value?.minus(1)

                    decreaseCountAnimation(
                        binding.tvSlotMachineMyntsCount,
                        slotMachineFragmentVM.myntsDisplay!!
                    )

                    slotMachineFragmentVM.callPlayOrderApi(it.coinsBurnedResponse.orderNo)

                    binding.image.currentImage.toVisible()
                    binding.image1.currentImage.toVisible()
                    binding.image2.currentImage.toVisible()

                    binding.image.setValueRandom(
                        sApiShowData[0].toInt(), (20..23).random()
                    )
                    binding.image1.setValueRandom(
                        sApiShowData[1].toInt(), (23..27).random()
                    )
                    binding.image2.setValueRandom(
                        sApiShowData[2].toInt(), (27..30).random()
                    )
                } else {
                    binding.ivBtnPlayAnimation.toVisible()
                    binding.progressBtnPlay.toInvisible()
                    Utility.showToast("Please try after sometime")
                }

            }
        }
    }

    private fun handlePlayOrderState(it: SlotMachineFragmentVM.PlayOrderSuccessState?) {
        when (it) {
            SlotMachineFragmentVM.PlayOrderSuccessState.Loading -> {}

            is SlotMachineFragmentVM.PlayOrderSuccessState.Error -> {
                binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                binding.progressBtnPlay.visibility = View.INVISIBLE

                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }

            is SlotMachineFragmentVM.PlayOrderSuccessState.PlayOrderSuccess -> {
                slotMachineFragmentVM.spinWheelRotateResponseDetails = it.spinWheelResponseDetails
            }
        }
    }

    private fun handleMyntsState(it: SlotMachineFragmentVM.MyntsSuccessState?) {
        when (it) {
            is SlotMachineFragmentVM.MyntsSuccessState.MyntsSuccess -> {
                if (it.remainingMynts != null) {

                    binding.loadingMynts.clearAnimation()
                    binding.loadingMynts.toInvisible()

                    binding.tvSlotMachineMyntsCount.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }

            is SlotMachineFragmentVM.MyntsSuccessState.Error -> {
                binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                binding.progressBtnPlay.visibility = View.INVISIBLE

                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }
            SlotMachineFragmentVM.MyntsSuccessState.Loading -> {
            }
        }
    }

    private fun handleCashState(it: SlotMachineFragmentVM.CashSuccessState?) {
        when (it) {
            is SlotMachineFragmentVM.CashSuccessState.CashSuccess -> {
                binding.loadingCash.clearAnimation()
                binding.loadingCash.toInvisible()

                binding.tvSlotMachineCashCount.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash?.toString())
                )
            }

            is SlotMachineFragmentVM.CashSuccessState.Error -> {
                binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                binding.progressBtnPlay.visibility = View.INVISIBLE

                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }
            SlotMachineFragmentVM.CashSuccessState.Loading -> {
            }
        }
    }

    private fun handleTicketsState(it: SlotMachineFragmentVM.TicketSuccessState?) {
        when (it) {
            is SlotMachineFragmentVM.TicketSuccessState.TicketSuccess -> {
                if (it.totalTickets != null) {
                    binding.loadingTickets.clearAnimation()
                    binding.loadingTickets.toInvisible()

                    binding.tvSlotMachineTicketsCount.text = it.totalTickets.toString()
                }
            }

            is SlotMachineFragmentVM.TicketSuccessState.Error -> {
                binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                binding.progressBtnPlay.visibility = View.INVISIBLE

                slotMachineFragmentVM.isSlotMachineStarted = false

                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
            }

            SlotMachineFragmentVM.TicketSuccessState.Loading -> {
            }

        }
    }

    private fun arcadeSounds(from: String) {

        slotMachineFragmentVM.mp?.stop()

        when (from) {
            "MYNTS" -> {
                slotMachineFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_mynts
                )
            }
            "TICKETS" -> {
                slotMachineFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_golden_ticket
                )
            }
            "SLOT" -> {
                slotMachineFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_slot_machine
                )
            }
            else -> {
                slotMachineFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_cashback
                )
            }
        }
        slotMachineFragmentVM.mp?.start()
    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.toVisible()
        invisibleImage.toInvisible()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_slot_machine
    }

    override fun getViewModel(): SlotMachineFragmentVM = slotMachineFragmentVM

    override fun onTryAgainClicked() {}

    override fun eventEnd(result: Int, imageInter: ImageView) {

        if (slotMachineFragmentVM.countDown < 2) {
            slotMachineFragmentVM.countDown++
            imageInter.toInvisible()
        } else {
            slotMachineFragmentVM.countDown = 0
            imageInter.currentImage.toInvisible()

            setSelectionOnCard(slotMachineFragmentVM.spinWheelRotateResponseDetails)
        }

    }

    private fun setSelectionOnCard(spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails?) {

        if (spinWheelRotateResponseDetails != null) {
            binding.containerSlotMachineRewards.visibility = View.VISIBLE
            binding.containerSlotMachineDefaultBanner.visibility = View.INVISIBLE

            binding.lottieRewardConfetti.visibility = View.VISIBLE
            binding.lottieRewardConfetti.playAnimation()

            if (spinWheelRotateResponseDetails.sectionValue == "0") {
                if (spinWheelRotateResponseDetails.noOfJackpotTicket == null) {
                    binding.tvWinRewardsValue.text = String.format(
                        getString(R.string.arcade_mynts),
                        spinWheelRotateResponseDetails.myntsWon
                    )

                    binding.ivSlotRewards.setImageResource(R.drawable.ic_mynt_rewards)

                    binding.ivSlotMachineMynts.visibility = View.INVISIBLE
                    binding.ivSlotMachineMyntsAnim.visibility = View.VISIBLE

                    arcadeSounds("MYNTS")

                    if (spinWheelRotateResponseDetails.myntsWon != null) {
                        slotMachineFragmentVM.myntsCount =
                            spinWheelRotateResponseDetails.myntsWon?.toFloat()
                        increaseCountAnimation(
                            binding.tvSlotMachineMyntsCount,
                            1500,
                            spinWheelRotateResponseDetails.myntsWon!!,
                            "Ticket"
                        )
                    }
                } else {
                    binding.tvWinRewardsValue.text = String.format(
                        getString(R.string.arcade_golden_tickets),
                        spinWheelRotateResponseDetails.noOfJackpotTicket
                    )

                    binding.ivSlotMachineTicket.visibility = View.INVISIBLE
                    binding.ivSlotMachineTicketAnim.visibility = View.VISIBLE

                    arcadeSounds("TICKETS")

                    binding.ivSlotRewards.setImageResource(R.drawable.ticket_reward)

                    if (spinWheelRotateResponseDetails.noOfJackpotTicket != null) {
                        increaseCountAnimation(
                            binding.tvSlotMachineTicketsCount,
                            500,
                            spinWheelRotateResponseDetails.noOfJackpotTicket!!,
                            "Ticket"
                        )
                    }
                }


            } else {
                Glide.with(this).load(R.drawable.ic_wallet_rewards)
                    .into(binding.ivSlotRewards)
                binding.tvWinRewardsValue.text = String.format(
                    getString(R.string.arcade_cashback),
                    Utility.convertToRs(spinWheelRotateResponseDetails.sectionValue)
                )

                arcadeSounds("CASHBACK")

                if (spinWheelRotateResponseDetails.sectionValue != null) {
                    increaseCountAnimation(
                        binding.tvSlotMachineCashCount, 1000,
                        Utility.convertToRs(spinWheelRotateResponseDetails.sectionValue)?.toInt()!!,
                        "Cash"
                    )
                }
            }

        } else {
            Toast.makeText(context, "Please check history", Toast.LENGTH_SHORT).show()
        }

        slotMachineFragmentVM.isSlotMachineStarted = false

        if (slotMachineFragmentVM.productId == null || slotMachineFragmentVM.productId == "null") {
            binding.ivBtnPlayAnimation.visibility = View.VISIBLE
            binding.progressBtnPlay.visibility = View.INVISIBLE
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.ivBtnPlayAnimation.visibility = View.VISIBLE
                binding.progressBtnPlay.visibility = View.INVISIBLE
                slotMachineFragmentVM.isArcadeIsPlayed = true
                findNavController().navigateUp()
            }, 1500)
        }
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
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
            }, 1500)
        } else {
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
            Utility.showToast("Please check history")
        }
    }

    private fun increaseCountAnimation(
        textScore: TextView,
        animDuration: Long,
        finalCount: Int,
        via: String
    ) {

        if (!textScore.text.isNullOrEmpty()) {
            if (via == "Cash") {
                val startPosition = (textScore.text.toString().split("₹")[1]).toIntOrNull()
                val endPosition = (textScore.text.toString().split("₹")[1]).toIntOrNull()
                if (startPosition == null || endPosition == null) {
                    textScore.text = String.format(
                        getString(R.string.arcade_cash_value),
                        (textScore.text.toString().split("₹")[1]).toDouble() + finalCount
                    )
                } else {
                    val animator: ValueAnimator =
                        ValueAnimator.ofInt(
                            (textScore.text.toString().split("₹")[1]).toInt(),
                            (textScore.text.toString().split("₹")[1]).toInt() + (finalCount)
                        )

                    animator.duration = animDuration
                    animator.addUpdateListener { animation ->
                        textScore.text = String.format(
                            getString(R.string.arcade_cash_value),
                            animation.animatedValue.toString()
                        )
                    }
                    animator.start()
                }
            } else {
                val animator: ValueAnimator = ValueAnimator.ofInt(
                    Integer.parseInt(textScore.text.toString()),
                    Integer.parseInt(textScore.text.toString()) + (finalCount)
                )

                animator.duration = animDuration
                animator.addUpdateListener { animation ->
                    textScore.text = animation.animatedValue.toString()
                }
                animator.start()
            }


            Handler(Looper.getMainLooper()).postDelayed({
                setViewVisibility(
                    binding.ivSlotMachineMynts,
                    binding.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineTicket,
                    binding.ivSlotMachineTicketAnim
                )
                setViewVisibility(
                    binding.ivSlotMachineCash,
                    binding.ivSlotMachineCashAnim
                )
                binding.lottieRewardConfetti.visibility = View.INVISIBLE
            }, animDuration)
        } else {
            FirebaseCrashlytics.getInstance()
                .recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
            Utility.showToast("Please check history")
        }
    }

    override fun onStop() {
        super.onStop()
        slotMachineFragmentVM.mp?.stop()
    }

}