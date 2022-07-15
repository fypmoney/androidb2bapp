package com.fypmoney.view.arcadegames.ui

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import com.fypmoney.databinding.FragmentSpinWheelBinding
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.model.SectionListItem
import com.fypmoney.view.arcadegames.viewmodel.FragmentSpinWheelVM
import com.fypmoney.view.rewardsAndWinnings.CashBackWonHistoryActivity
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.fragment_spin_wheel.*
import kotlinx.android.synthetic.main.toolbar.*


class SpinWheelFragment : BaseFragment<FragmentSpinWheelBinding, FragmentSpinWheelVM>() {

    private var mp: MediaPlayer? = null
    private var mViewBinding: FragmentSpinWheelBinding? = null
    private var sectionId: Int? = null
    private var orderId: String? = null
    private var myntsDisplay: Int? = null
    private val spinWheelFragmentVM by viewModels<FragmentSpinWheelVM> { defaultViewModelProviderFactory }
    private var dialogInsufficientMynts: Dialog? = null
    private var code: String? = null

    companion object {
        var sectionArrayList: List<SectionListItem> = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        code = "SPIN_WHEEL_1000"

        setBackgrounds()

        setUpObserver(spinWheelFragmentVM)

        Glide.with(this).load(R.drawable.coin).into(mViewBinding!!.ivSpinWheelMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(mViewBinding!!.ivSpinWheelTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(mViewBinding!!.ivSpinWheelCashAnim)

        mViewBinding!!.ivBtnPlayAnimation.setOnClickListener {
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

                spinWheelFragmentVM.callMyntsBurnApi(code)

//            spinWheelFragmentVM.enableSpin.value = false

            } else {
                val limitOverBottomSheet = LimitOverBottomSheet()
                limitOverBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                limitOverBottomSheet.show(childFragmentManager, "LimitOverBottomSheet")
            }

        }

        mViewBinding?.chipCashView?.setOnClickListener {
            val intent = Intent(requireContext(), CashBackWonHistoryActivity::class.java)
            startActivity(intent)
        }

        mViewBinding?.chipMyntsView?.setOnClickListener {
            findNavController().navigate(R.id.navigation_rewards_history)
        }

        mViewBinding?.chipTicketView?.setOnClickListener {
            findNavController().navigate(R.id.navigation_multiple_jackpots)
        }

        dialogInsufficientMynts = Dialog(this.requireContext())

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

        // this is the only type of the vibration which requires system version Oreo (API 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // this effect creates the vibration of default amplitude for 1000ms(1 sec)

            val vibrator = this.context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
//            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
//            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
//            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
//            vibrationEffect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)


            // it is safe to cancel other vibrations currently taking place
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }
    }

    private fun setSelectionOnCard(spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails) {

        mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
        mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE

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

                increaseCountAnimation(
                    mViewBinding!!.tvSpinWheelMyntsCount,
                    1500,
                    spinWheelRotateResponseDetails.myntsWon!!,
                    "Ticket"
                )
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
    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.visibility = View.VISIBLE
        invisibleImage.visibility = View.INVISIBLE
    }

    private fun setUpObserver(viewModel: FragmentSpinWheelVM) {
        viewModel.error.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.errorCode == "PKT_2051") {
                callInsufficientDialog(list.msg)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

        viewModel.totalRewardsResponse.observe(
            viewLifecycleOwner
        ) { list ->
//            viewModel.myntsCount = list.amount
            mViewBinding?.tvSpinWheelCashCount?.text = String.format(
                getString(R.string.arcade_cash_value),
                Utility.convertToRs(list.amount.toString())
            )
        }

        viewModel.rewardSummaryStatus.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.totalPoints != null) {
                mViewBinding?.tvSpinWheelMyntsCount?.text =
                    String.format("%.0f", list.remainingPoints)
            }
        }

        viewModel.totalJackpotAmount.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.count != null) {
                mViewBinding?.tvSpinWheelTicketsCount?.text = "${list.count}"
            }

        }

//        viewModel.onPlayClicked.observe(
//            viewLifecycleOwner
//        )
//        {
//            try {
//                when (sectionId) {
//                    1 -> {
//                        luckySpinWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)
//                    }
//                    else -> {
//                        luckySpinWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)
//                    }
//
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

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

//            spinWheelFragmentVM.onPlayClicked()

            if (list != null) {

                viewModel.coinsBurned.postValue(null)

                arcadeSounds("SPINNER")

                spinWheelFragmentVM.remainFrequency.value =
                    spinWheelFragmentVM.remainFrequency.value?.minus(1)

                decreaseCountAnimation(mViewBinding!!.tvSpinWheelMyntsCount, 1500, myntsDisplay!!)

                sectionId = list.sectionId
                orderId = list.orderNo
                spinWheelFragmentVM.noOfJackpotTickets = list.noOfJackpotTicket

                spinWheelFragmentVM.callSpinWheelApi(orderId)

            }

            luckySpinWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)

        }

        viewModel.spinWheelResponseList.observe(viewLifecycleOwner) { spinWheelRotation ->
            try {
                luckySpinWheelView.setLuckyRoundItemSelectedListener {
                    sectionArrayList.forEach { item ->
                        if (item.id == sectionId.toString()) {
                            setSelectionOnCard(spinWheelRotation)
                            return@forEach
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this.requireContext(), "Ex: $e", Toast.LENGTH_SHORT).show()
                Log.d("Spin", "Ex: $e")
            }

            viewModel.enableSpin.value = false

            //Update mynts and ticket values on finish
//            setResult(23)
//            finish()

        }
    }

    private fun arcadeSounds(from: String) {
        when (from) {
            "MYNTS" -> {
                mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_mynts
                )
            }
            "TICKETS" -> {
                mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_golden_ticket
                )
            }
            "SPINNER" -> {
                mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_spinner
                )
            }
            else -> {
                mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_cashback
                )
            }
        }
        mp?.start()
    }

    private fun handleState(it: FragmentSpinWheelVM.SpinWheelState?) {
        when (it) {
            FragmentSpinWheelVM.SpinWheelState.Error -> {

            }
            FragmentSpinWheelVM.SpinWheelState.Loading -> {
                mViewBinding!!.spinWheelContainer.visibility = View.INVISIBLE
            }
            is FragmentSpinWheelVM.SpinWheelState.Success -> {
                mViewBinding!!.spinWheelContainer.visibility = View.VISIBLE

                spinWheelFragmentVM.noOfJackpotTickets = it.spinWheelData.noOfJackpotTicket
                myntsDisplay = it.spinWheelData.appDisplayText?.toInt()

                Glide.with(this).load(R.drawable.play_button)
                    .into(mViewBinding!!.ivBtnPlayAnimation)

                Glide.with(this).load(it.spinWheelData.successResourceId)
                    .into(mViewBinding!!.ivBannerSpinWheel)

                mViewBinding!!.tvSpinWheelBurnMyntsCount.text = it.spinWheelData.appDisplayText

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

    override fun getViewModel(): FragmentSpinWheelVM = spinWheelFragmentVM

    private fun setBackgrounds() {
        mViewBinding?.let {
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

    private fun decreaseCountAnimation(textScore: TextView, animDuration: Long, finalCount: Int) {
        val animator = ValueAnimator.ofInt(
            Integer.parseInt(textScore.text.toString()),
            Integer.parseInt(textScore.text.toString()) - (finalCount)
        )
        animator.duration = animDuration
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
        }, animDuration)
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

}

