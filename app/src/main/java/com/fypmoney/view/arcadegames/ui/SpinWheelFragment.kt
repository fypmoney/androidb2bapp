package com.fypmoney.view.arcadegames.ui

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentSpinWheelBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.model.SectionListItem
import com.fypmoney.view.arcadegames.viewmodel.SpinWheelFragmentVM
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.fragment_spin_wheel.*
import kotlinx.android.synthetic.main.toolbar.*

class SpinWheelFragment : BaseFragment<FragmentSpinWheelBinding, SpinWheelFragmentVM>() {

    private var mViewBinding: FragmentSpinWheelBinding? = null
    private val spinWheelFragmentVM by viewModels<SpinWheelFragmentVM> { defaultViewModelProviderFactory }
    private var dialogInsufficientMynts: Dialog? = null
    private val navArgs by navArgs<SpinWheelFragmentArgs>()

    companion object {
        var sectionArrayList: List<SectionListItem> = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()

        mViewBinding?.loadingMynts?.visibility = View.VISIBLE
        mViewBinding?.loadingTickets?.visibility = View.VISIBLE
        mViewBinding?.loadingCash?.visibility = View.VISIBLE

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        spinWheelFragmentVM.productCode = navArgs.productCode
        spinWheelFragmentVM.productId = navArgs.orderId.toString()

        spinWheelFragmentVM.callSingleProductApi(spinWheelFragmentVM.productCode)

        setBackgrounds()

        setUpObserver(spinWheelFragmentVM)

        Glide.with(this).load(R.drawable.coin_updated).into(mViewBinding!!.ivSpinWheelMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(mViewBinding!!.ivSpinWheelTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(mViewBinding!!.ivSpinWheelCashAnim)

        mViewBinding!!.ivBtnPlayAnimation.setOnClickListener {

            if (spinWheelFragmentVM.productId == null || spinWheelFragmentVM.productId == "null") {
                if (spinWheelFragmentVM.remainFrequency.value!! > 0) {
                    mViewBinding!!.containerSpinWheelRewards.visibility = View.INVISIBLE
                    mViewBinding!!.containerSpinWheelDefaultBanner.visibility = View.VISIBLE

                    mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
                    mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE

                    vibrateDevice()

                    setViewVisibility(
                        mViewBinding!!.ivSpinWheelMyntsAnim,
                        mViewBinding!!.ivSpinWheelMynts
                    )

                    spinWheelFragmentVM.callMyntsBurnApi(spinWheelFragmentVM.productCode)

                } else {
                    val limitOverBottomSheet = LimitOverBottomSheet()
                    limitOverBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                    limitOverBottomSheet.show(childFragmentManager, "LimitOverBottomSheet")
                }
            } else {

                mViewBinding!!.containerSpinWheelRewards.visibility = View.INVISIBLE
                mViewBinding!!.containerSpinWheelDefaultBanner.visibility = View.VISIBLE

                mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE

                spinWheelFragmentVM.callProductsDetailsApi(spinWheelFragmentVM.productId)

            }

        }

        dialogInsufficientMynts = Dialog(this.requireContext())


        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (spinWheelFragmentVM.isArcadeIsPlayed) {
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

    private fun vibrateDevice() {
        val vibrationEffect: VibrationEffect

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = this.context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }

    }

    private fun setSelectionOnCard(spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails) {

        mViewBinding!!.containerSpinWheelRewards.visibility = View.VISIBLE
        mViewBinding!!.containerSpinWheelDefaultBanner.visibility = View.INVISIBLE

        mViewBinding!!.lottieRewardConfetti.visibility = View.VISIBLE
        mViewBinding!!.lottieRewardConfetti.playAnimation()

        if (spinWheelRotateResponseDetails.sectionValue == "0") {
            if (spinWheelRotateResponseDetails.noOfJackpotTicket == null) {
                mViewBinding!!.tvWinRewardsValue.text = String.format(
                    getString(R.string.arcade_mynts),
                    spinWheelRotateResponseDetails.myntsWon
                )

                mViewBinding!!.ivSpinRewards.setImageResource(R.drawable.ic_mynt_rewards)

                mViewBinding!!.ivSpinWheelMynts.visibility = View.INVISIBLE
                mViewBinding!!.ivSpinWheelMyntsAnim.visibility = View.VISIBLE

                arcadeSounds("MYNTS")

                if (spinWheelRotateResponseDetails.myntsWon != null) {
                    spinWheelFragmentVM.myntsCount =
                        spinWheelRotateResponseDetails.myntsWon?.toFloat()
                    increaseCountAnimation(
                        mViewBinding!!.tvSpinWheelMyntsCount,
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

                mViewBinding!!.ivSpinWheelTicket.visibility = View.INVISIBLE
                mViewBinding!!.ivSpinWheelTicketAnim.visibility = View.VISIBLE

                arcadeSounds("TICKETS")

                mViewBinding!!.ivSpinRewards.setImageResource(R.drawable.ticket_reward)

                if (spinWheelRotateResponseDetails.noOfJackpotTicket != null) {
                    increaseCountAnimation(
                        mViewBinding!!.tvSpinWheelTicketsCount,
                        500,
                        spinWheelRotateResponseDetails.noOfJackpotTicket!!,
                        "Ticket"
                    )
                }
            }


        } else {
            Glide.with(this).load(R.drawable.ic_wallet_rewards).into(mViewBinding!!.ivSpinRewards)
            mViewBinding!!.tvWinRewardsValue.text = String.format(
                getString(R.string.arcade_cashback),
                Utility.convertToRs(spinWheelRotateResponseDetails.sectionValue)
            )

            arcadeSounds("CASHBACK")

            increaseCountAnimation(
                mViewBinding!!.tvSpinWheelCashCount, 1000,
                Utility.convertToRs(spinWheelRotateResponseDetails.sectionValue)?.toInt()!!,
                "Cash"
            )
        }

        if (spinWheelFragmentVM.productId == null || spinWheelFragmentVM.productId == "null") {
            mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
                spinWheelFragmentVM.isArcadeIsPlayed = true
                findNavController().navigateUp()
            }, 1500)
        }
    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.visibility = View.VISIBLE
        invisibleImage.visibility = View.INVISIBLE
    }

    private fun setUpObserver(viewModel: SpinWheelFragmentVM) {

        var sectionId: Int? = null

        viewModel.error.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.errorCode == "PKT_2051") {
                callInsufficientDialog(list.msg)
                mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
            } else {
                mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
            }
            setViewVisibility(
                mViewBinding!!.ivSpinWheelMynts,
                mViewBinding!!.ivSpinWheelMyntsAnim
            )
            setViewVisibility(
                mViewBinding!!.ivSpinWheelCash,
                mViewBinding!!.ivSpinWheelCashAnim
            )
            setViewVisibility(
                mViewBinding!!.ivSpinWheelTicket,
                mViewBinding!!.ivSpinWheelTicketAnim
            )
        }

        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

        viewModel.remainFrequency.observe(
            viewLifecycleOwner
        ) {
            mViewBinding!!.tvSpinWheelAttemptsLeft.text = String.format(
                getString(R.string.attempts_left),
                spinWheelFragmentVM.remainFrequency.value, spinWheelFragmentVM.frequency
            )
        }

        viewModel.coinsBurned.observe(
            viewLifecycleOwner
        ) { list ->

            if (list != null) {

                viewModel.coinsBurned.postValue(null)

                arcadeSounds("SPINNER")

                spinWheelFragmentVM.remainFrequency.value =
                    spinWheelFragmentVM.remainFrequency.value?.minus(1)

                decreaseCountAnimation(mViewBinding!!.tvSpinWheelMyntsCount, spinWheelFragmentVM.myntsDisplay!!)

                sectionId = list.sectionId

                spinWheelFragmentVM.callSpinWheelApi(list.orderNo)

            }

            luckySpinWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)

        }

        viewModel.spinWheelResponseList.observe(viewLifecycleOwner) { spinWheelRotation ->
            try {
                luckySpinWheelView.setLuckyRoundItemSelectedListener {
                    sectionArrayList.forEach { item ->
                        if (item.id == sectionId.toString()) {
                            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                                setSelectionOnCard(spinWheelRotation)
                            }
                            return@forEach
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this.requireContext(), "Ex: $e", Toast.LENGTH_SHORT).show()
                Log.d("Spin", "Ex: $e")
            }

        }

        viewModel.redeemCallBackResponse.observe(viewLifecycleOwner) {
            sectionId = it.sectionId

            vibrateDevice()

            arcadeSounds("SPINNER")

            luckySpinWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)
            spinWheelFragmentVM.callSpinWheelApi(spinWheelFragmentVM.productId)

        }
    }

    private fun arcadeSounds(from: String) {
        when (from) {
            "MYNTS" -> {
                spinWheelFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_mynts
                )
            }
            "TICKETS" -> {
                spinWheelFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_golden_ticket
                )
            }
            "SPINNER" -> {
                spinWheelFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_spinner
                )
            }
            else -> {
                spinWheelFragmentVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_cashback
                )
            }
        }
        spinWheelFragmentVM.mp?.start()
    }

    private fun handleState(it: SpinWheelFragmentVM.SpinWheelState?) {
        when (it) {
            SpinWheelFragmentVM.SpinWheelState.Error -> {

            }
            SpinWheelFragmentVM.SpinWheelState.Loading -> {
//                mViewBinding!!.spinWheelContainer.visibility = View.INVISIBLE
            }

            is SpinWheelFragmentVM.SpinWheelState.MyntsSuccess -> {
                if (it.remainingMynts != null) {
                    mViewBinding?.loadingMynts?.clearAnimation()
                    mViewBinding?.loadingMynts?.visibility = View.INVISIBLE

                    spinWheelFragmentVM.myntsCount = it.remainingMynts

                    mViewBinding?.tvSpinWheelMyntsCount?.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }

            is SpinWheelFragmentVM.SpinWheelState.TicketSuccess -> {
                if (it.totalTickets != null) {
                    mViewBinding?.loadingTickets?.clearAnimation()
                    mViewBinding?.loadingTickets?.visibility = View.INVISIBLE

                    mViewBinding?.tvSpinWheelTicketsCount?.text = "${it.totalTickets}"
                }
            }

            is SpinWheelFragmentVM.SpinWheelState.CashSuccess -> {
                mViewBinding?.loadingCash?.clearAnimation()
                mViewBinding?.loadingCash?.visibility = View.INVISIBLE

                mViewBinding?.tvSpinWheelCashCount?.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash.toString())
                )
            }

            is SpinWheelFragmentVM.SpinWheelState.Success -> {
//                mViewBinding!!.spinWheelContainer.visibility = View.VISIBLE

                mViewBinding?.loadingBurnMynts?.clearAnimation()
                mViewBinding?.loadingBurnMynts?.visibility = View.INVISIBLE

                spinWheelFragmentVM.myntsDisplay = it.spinWheelData.appDisplayText?.toInt()

                Glide.with(this).load(R.drawable.play_button)
                    .into(mViewBinding!!.ivBtnPlayAnimation)

                mViewBinding!!.shimmerLayoutSpinWheel.toInvisible()
                mViewBinding!!.frameBtnContainer.toVisible()
                mViewBinding!!.luckyLayout.toVisible()
                mViewBinding!!.tvSpinWheelAttemptsLeft.toVisible()

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.spinWheelData.successResourceId,
                    mViewBinding!!.ivBannerSpinWheel
                )
//                Glide.with(this).load(it.spinWheelData.successResourceId)
//                    .into(mViewBinding!!.ivBannerSpinWheel)

                mViewBinding!!.tvSpinWheelBurnMyntsCount.text = it.spinWheelData.appDisplayText
                spinWheelFragmentVM.myntsCount = it.spinWheelData.appDisplayText?.let { it1 ->
                    spinWheelFragmentVM.myntsCount?.plus(
                        it1.toFloat()
                    )
                }


                sectionArrayList = emptyList()
                sectionArrayList = ArrayList()

                sectionArrayList = it.spinWheelData.sectionList as List<SectionListItem>

                spinWheelFragmentVM.frequency = it.spinWheelData.frequency

                spinWheelFragmentVM.remainFrequency.value =
                    it.spinWheelData.frequencyPlayed?.let { it1 ->
                        it.spinWheelData.frequency?.minus(
                            it1
                        )
                    }

                setWheel(sectionArrayList, it.spinWheelData.rewardTypeOnFailure)

            }
            null -> {

            }
        }
    }

    override fun onTryAgainClicked() {
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_spin_wheel
    }

    override fun getViewModel(): SpinWheelFragmentVM = spinWheelFragmentVM

    private fun setBackgrounds() {
        mViewBinding?.let {

            setBackgroundDrawable(
                it.viewMiddleContent,
                ContextCompat.getColor(this.requireContext(), R.color.editTextStrokeColor),
                98f,
                ContextCompat.getColor(this.requireContext(), R.color.editTextStrokeColor),
                0f,
                true
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

    private fun setWheel(sectionList: List<SectionListItem>, rewardTypeOnFailure: String?) {
        luckySpinWheelView.setRound(4)
        spinWheelFragmentVM.setDataInSpinWheel(sectionList, rewardTypeOnFailure)
        luckySpinWheelView.setData(spinWheelFragmentVM.luckyItemList)
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
            setViewVisibility(mViewBinding!!.ivSpinWheelMynts, mViewBinding!!.ivSpinWheelMyntsAnim)
            setViewVisibility(
                mViewBinding!!.ivSpinWheelTicket,
                mViewBinding!!.ivSpinWheelTicketAnim
            )
            setViewVisibility(mViewBinding!!.ivSpinWheelCash, mViewBinding!!.ivSpinWheelCashAnim)
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
            setViewVisibility(mViewBinding!!.ivSpinWheelMynts, mViewBinding!!.ivSpinWheelMyntsAnim)
            setViewVisibility(
                mViewBinding!!.ivSpinWheelTicket,
                mViewBinding!!.ivSpinWheelTicketAnim
            )
            setViewVisibility(mViewBinding!!.ivSpinWheelCash, mViewBinding!!.ivSpinWheelCashAnim)
            mViewBinding!!.lottieRewardConfetti.visibility = View.INVISIBLE
        }, animDuration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        spinWheelFragmentVM.mp?.stop()
    }


}

