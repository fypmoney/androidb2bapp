package com.fypmoney.view.arcadegames.ui

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
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
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.toolbar.*

class SlotMachineFragment : BaseFragment<FragmentSlotMachineBinding, SlotMachineFragmentVM>(),
    SlotMachineImageEventEnd {

    private val slotMachineFragmentVM by viewModels<SlotMachineFragmentVM> { defaultViewModelProviderFactory }
    private var mViewBinding: FragmentSlotMachineBinding? = null
    private var dialogInsufficientMynts: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()

        mViewBinding?.loadingMynts?.toVisible()
        mViewBinding?.loadingTickets?.toVisible()
        mViewBinding?.loadingCash?.toVisible()

        setEvents()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        Glide.with(this).load(R.drawable.coin_updated).into(mViewBinding!!.ivSlotMachineMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(mViewBinding!!.ivSlotMachineTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(mViewBinding!!.ivSlotMachineCashAnim)

        dialogInsufficientMynts = Dialog(this.requireContext())

        setBackgrounds()

        setObserver()

        slotMachineFragmentVM.callGetProductDataApi("TREASURE_BOX_101")

        mViewBinding?.ivBtnPlayAnimation?.setOnClickListener {
            if (slotMachineFragmentVM.remainFrequency.value!! > 0) {
                mViewBinding!!.containerSlotMachineRewards.toInvisible()
                mViewBinding!!.containerSlotMachineDefaultBanner.toVisible()

                mViewBinding!!.ivBtnPlayAnimation.toInvisible()
                mViewBinding!!.progressBtnPlay.toVisible()

                vibrateDevice()

                setViewVisibility(
                    mViewBinding!!.ivSlotMachineMyntsAnim,
                    mViewBinding!!.ivSlotMachineMynts
                )

                //call mynts burn api

                mViewBinding?.image?.setValueRandom(
                    0, (20..28).random()
                )

                mViewBinding?.image1?.setValueRandom(
                    2, (20..28).random()
                )

                mViewBinding?.image2?.setValueRandom(
                    1, (20..28).random()
                )
            } else {
                val limitOverBottomSheet = LimitOverBottomSheet()
                limitOverBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                limitOverBottomSheet.show(childFragmentManager, "LimitOverBottomSheet")
            }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun setEvents() {
        mViewBinding?.image?.setEventEnd(this@SlotMachineFragment)
        mViewBinding?.image1?.setEventEnd(this@SlotMachineFragment)
        mViewBinding?.image2?.setEventEnd(this@SlotMachineFragment)
    }

    private fun setBackgrounds() {
        mViewBinding?.let {

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

    private fun vibrateDevice() {
        val vibrationEffect: VibrationEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = this.context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }

    }

    private fun setObserver() {

        slotMachineFragmentVM.remainFrequency.observe(
            viewLifecycleOwner
        ) {
            mViewBinding!!.tvSlotMachineAttemptsLeft.text = String.format(
                getString(R.string.attempts_left),
                slotMachineFragmentVM.remainFrequency.value, slotMachineFragmentVM.frequency
            )
        }

        slotMachineFragmentVM.state.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(it: SlotMachineFragmentVM.SlotMachineState?) {
        when (it) {

            is SlotMachineFragmentVM.SlotMachineState.MyntsSuccess -> {
                if (it.remainingMynts != null) {
                    mViewBinding?.loadingMynts?.clearAnimation()
                    mViewBinding?.loadingMynts?.visibility = View.INVISIBLE

                    slotMachineFragmentVM.myntsCount = it.remainingMynts

                    mViewBinding?.tvSlotMachineMyntsCount?.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }

            is SlotMachineFragmentVM.SlotMachineState.TicketSuccess -> {
                if (it.totalTickets != null) {
                    mViewBinding?.loadingTickets?.clearAnimation()
                    mViewBinding?.loadingTickets?.visibility = View.INVISIBLE

                    mViewBinding?.tvSlotMachineTicketsCount?.text = "${it.totalTickets}"
                }
            }

            is SlotMachineFragmentVM.SlotMachineState.CashSuccess -> {
                mViewBinding?.loadingCash?.clearAnimation()
                mViewBinding?.loadingCash?.visibility = View.INVISIBLE

                mViewBinding?.tvSlotMachineCashCount?.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash.toString())
                )
            }

            is SlotMachineFragmentVM.SlotMachineState.Success -> {

                mViewBinding?.loadingBurnMynts?.clearAnimation()
                mViewBinding?.loadingBurnMynts?.visibility = View.INVISIBLE

                slotMachineFragmentVM.myntsDisplay = it.treasureBoxItem.appDisplayText?.toInt()

                Glide.with(this).load(R.drawable.play_button)
                    .into(mViewBinding!!.ivBtnPlayAnimation)

                mViewBinding?.shimmerLayoutSpinWheel?.toInvisible()
                mViewBinding?.frameBtnContainer?.toVisible()
                mViewBinding?.luckyLayout?.toVisible()
                mViewBinding?.tvSlotMachineAttemptsLeft?.toVisible()
                mViewBinding?.ivSlotMachineEllipse?.toVisible()

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.treasureBoxItem.successResourceId,
                    mViewBinding!!.ivBannerSlotMachine
                )

                mViewBinding!!.tvSlotMachineBurnMyntsCount.text = it.treasureBoxItem.appDisplayText
                slotMachineFragmentVM.myntsCount = it.treasureBoxItem.appDisplayText?.let { it1 ->
                    slotMachineFragmentVM.myntsCount?.plus(
                        it1.toFloat()
                    )
                }

                slotMachineFragmentVM.frequency = it.treasureBoxItem.frequency

                slotMachineFragmentVM.remainFrequency.value =
                    it.treasureBoxItem.frequencyPlayed?.let { it1 ->
                        it.treasureBoxItem.frequency?.minus(
                            it1
                        )
                    }
            }

            is SlotMachineFragmentVM.SlotMachineState.MyntsBurnSuccess -> {

                slotMachineFragmentVM.remainFrequency.value =
                    slotMachineFragmentVM.remainFrequency.value?.minus(1)

                decreaseCountAnimation(mViewBinding!!.tvSlotMachineMyntsCount, slotMachineFragmentVM.myntsDisplay!!)

                slotMachineFragmentVM.callPlayOrderApi(it.coinsBurnedResponse.orderNo)

            }

            is SlotMachineFragmentVM.SlotMachineState.PlayOrderSuccess -> {

                slotMachineFragmentVM.spinWheelRotateResponseDetails = it.spinWheelResponseDetails

                val sApiShowData = if (it.spinWheelResponseDetails.sectionCode == null){
                    "121".toCharArray()

                }else{
                    it.spinWheelResponseDetails.sectionCode?.toCharArray()
                }

                if (sApiShowData != null) {
                    sApiShowData[0].let { it1 ->
                        it1.let { it2 ->
                            mViewBinding?.image?.setValueRandom(
                                it2.toInt(), (20..28).random()
                            )
                        }
                    }

                    sApiShowData[1].let { it1 ->
                        it1.let { it2 ->
                            mViewBinding?.image1?.setValueRandom(
                                it2.toInt(), (20..28).random()
                            )
                        }
                    }

                    sApiShowData[2].let { it1 ->
                        it1.let { it2 ->
                            mViewBinding?.image2?.setValueRandom(
                                it2.toInt(), (20..28).random()
                            )
                        }
                    }
                }

            }

            SlotMachineFragmentVM.SlotMachineState.Error -> {
//                if (list.errorCode == "PKT_2051") {
//                    callInsufficientDialog(list.msg)
//                    mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
//                    mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
//                } else {
//                    mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
//                    mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
//                }
                setViewVisibility(
                    mViewBinding!!.ivSlotMachineMynts,
                    mViewBinding!!.ivSlotMachineMyntsAnim
                )
                setViewVisibility(
                    mViewBinding!!.ivSlotMachineCash,
                    mViewBinding!!.ivSlotMachineCashAnim
                )
                setViewVisibility(
                    mViewBinding!!.ivSlotMachineTicket,
                    mViewBinding!!.ivSlotMachineTicketAnim
                )
            }
            SlotMachineFragmentVM.SlotMachineState.Loading -> {}
            null -> {}
        }
    }

    private fun arcadeSounds(from: String) {
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
            "SPINNER" -> {
                slotMachineFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_spinner
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

    private fun callInsufficientDialog(msg: String) {
        dialogInsufficientMynts?.setCancelable(false)
        dialogInsufficientMynts?.setCanceledOnTouchOutside(false)
        dialogInsufficientMynts?.setContentView(R.layout.dialog_rewards_insufficient)

        val wlp = dialogInsufficientMynts?.window?.attributes

        wlp?.width = ViewGroup.LayoutParams.MATCH_PARENT
        dialogInsufficientMynts?.setCanceledOnTouchOutside(false)
        dialogInsufficientMynts?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogInsufficientMynts?.window?.attributes = wlp
        dialogInsufficientMynts?.error_msg?.text = msg

        dialogInsufficientMynts?.clicked?.setOnClickListener {
            trackr {
                it.name = TrackrEvent.insufficient_mynts
            }
            dialogInsufficientMynts?.dismiss()
        }
        dialogInsufficientMynts?.show()
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

    override fun onTryAgainClicked() {
    }

    override fun eventEnd(result: Int, imageInter: ImageView) {

        if (slotMachineFragmentVM.countDown < 2) {
            slotMachineFragmentVM.countDown++
            imageInter.toInvisible()
        } else {
            slotMachineFragmentVM.countDown = 0
            imageInter.toInvisible()

            setSelectionOnCard(slotMachineFragmentVM.spinWheelRotateResponseDetails)
        }

    }

    private fun setSelectionOnCard(spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails?) {

        mViewBinding!!.containerSlotMachineRewards.visibility = View.VISIBLE
        mViewBinding!!.containerSlotMachineDefaultBanner.visibility = View.INVISIBLE

        mViewBinding!!.lottieRewardConfetti.visibility = View.VISIBLE
        mViewBinding!!.lottieRewardConfetti.playAnimation()

        if (spinWheelRotateResponseDetails?.sectionValue == "0") {
            if (spinWheelRotateResponseDetails.noOfJackpotTicket == null) {
                mViewBinding!!.tvWinRewardsValue.text = String.format(
                    getString(R.string.arcade_mynts),
                    spinWheelRotateResponseDetails.myntsWon
                )

                mViewBinding!!.ivSlotRewards.setImageResource(R.drawable.ic_mynt_rewards)

                mViewBinding!!.ivSlotMachineMynts.visibility = View.INVISIBLE
                mViewBinding!!.ivSlotMachineMyntsAnim.visibility = View.VISIBLE

                arcadeSounds("MYNTS")

                if (spinWheelRotateResponseDetails.myntsWon != null) {
                    slotMachineFragmentVM.myntsCount =
                        spinWheelRotateResponseDetails.myntsWon?.toFloat()
                    increaseCountAnimation(
                        mViewBinding!!.tvSlotMachineMyntsCount,
                        1500,
                        spinWheelRotateResponseDetails.myntsWon!!,
                        "Ticket"
                    )
                }
            } else {
                mViewBinding!!.tvWinRewardsValue.text = String.format(
                    getString(R.string.arcade_golden_tickets),
                    spinWheelRotateResponseDetails.noOfJackpotTicket
                )

                mViewBinding!!.ivSlotMachineTicket.visibility = View.INVISIBLE
                mViewBinding!!.ivSlotMachineTicketAnim.visibility = View.VISIBLE

                arcadeSounds("TICKETS")

                mViewBinding!!.ivSlotRewards.setImageResource(R.drawable.ticket_reward)

                if (spinWheelRotateResponseDetails.noOfJackpotTicket != null) {
                    increaseCountAnimation(
                        mViewBinding!!.tvSlotMachineTicketsCount,
                        500,
                        spinWheelRotateResponseDetails.noOfJackpotTicket!!,
                        "Ticket"
                    )
                }
            }


        } else {
            Glide.with(this).load(R.drawable.ic_wallet_rewards).into(mViewBinding!!.ivSlotRewards)
            mViewBinding!!.tvWinRewardsValue.text = String.format(
                getString(R.string.arcade_cashback),
                Utility.convertToRs(spinWheelRotateResponseDetails?.sectionValue)
            )

            arcadeSounds("CASHBACK")

            increaseCountAnimation(
                mViewBinding!!.tvSlotMachineCashCount, 1000,
                Utility.convertToRs(spinWheelRotateResponseDetails?.sectionValue)?.toInt()!!,
                "Cash"
            )
        }

        if (slotMachineFragmentVM.productId == null || slotMachineFragmentVM.productId == "null") {
            mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
                slotMachineFragmentVM.isArcadeIsPlayed = true
                findNavController().navigateUp()
            }, 1500)
        }
    }

    private fun decreaseCountAnimation(textScore: TextView, finalCount: Int) {
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
            setViewVisibility(mViewBinding!!.ivSlotMachineMynts, mViewBinding!!.ivSlotMachineMyntsAnim)
        }, 1500)
    }

    private fun increaseCountAnimation(
        textScore: TextView,
        animDuration: Long,
        finalCount: Int,
        via: String
    ) {
        val animator: ValueAnimator = if (via == "Cash") {
            ValueAnimator.ofInt(
                Integer.parseInt(textScore.text.toString().split("₹")[1]),
                Integer.parseInt(textScore.text.toString().split("₹")[1]) + (finalCount)
            )
        } else {
            ValueAnimator.ofInt(
                Integer.parseInt(textScore.text.toString()),
                Integer.parseInt(textScore.text.toString()) + (finalCount)
            )
        }
        animator.duration = animDuration

        animator.addUpdateListener { animation ->
            if (via == "Cash")
                textScore.text = String.format(
                    getString(R.string.arcade_cash_value),
                    animation.animatedValue.toString()
                )
            else
                textScore.text = animation.animatedValue.toString()
        }
        animator.start()

        Handler(Looper.getMainLooper()).postDelayed({
            setViewVisibility(mViewBinding!!.ivSlotMachineMynts, mViewBinding!!.ivSlotMachineMyntsAnim)
            setViewVisibility(mViewBinding!!.ivSlotMachineTicket, mViewBinding!!.ivSlotMachineTicketAnim)
            setViewVisibility(mViewBinding!!.ivSlotMachineCash, mViewBinding!!.ivSlotMachineCashAnim)
            mViewBinding!!.lottieRewardConfetti.visibility = View.INVISIBLE
        }, animDuration)
    }

    override fun onStop() {
        super.onStop()
        slotMachineFragmentVM.mp?.stop()
    }

}