package com.fypmoney.view.arcadegames.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.ActivitySpinWheelHistoryViewBinding
import com.fypmoney.view.arcadegames.model.SectionListItem
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.viewmodel.FragmentSpinWheelVM
import kotlinx.android.synthetic.main.fragment_spin_wheel.*
import kotlinx.android.synthetic.main.toolbar.*

class SpinWheelHistoryView :
    BaseActivity<ActivitySpinWheelHistoryViewBinding, FragmentSpinWheelVM>() {

    private val spinWheelFragmentVM by viewModels<FragmentSpinWheelVM> { defaultViewModelProviderFactory }
    private var sectionId: Int? = null
    private var mp: MediaPlayer? = null
    private var orderId: String? = null
    private var productCode: String? = null
    private var mViewBinding: ActivitySpinWheelHistoryViewBinding? = null

    companion object {
        var sectionArrayList: List<SectionListItem> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewDataBinding()

        val intent = intent
        orderId = intent.getStringExtra(AppConstants.ORDER_NUM)
        productCode = intent.getStringExtra(AppConstants.PRODUCT_CODE)
        sectionId = intent.getIntExtra(AppConstants.SECTION_ID, -1)

        setToolbarAndTitle(
            context = this@SpinWheelHistoryView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        spinWheelFragmentVM.productCode = productCode.toString()
        spinWheelFragmentVM.callSingleProductApi(spinWheelFragmentVM.productCode)

        setBackgrounds()

        setUpObserver(spinWheelFragmentVM)

        Glide.with(this).load(R.drawable.coin_updated).into(mViewBinding!!.ivSpinWheelMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(mViewBinding!!.ivSpinWheelTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(mViewBinding!!.ivSpinWheelCashAnim)

        mViewBinding!!.ivBtnPlayAnimation.setOnClickListener {
            mViewBinding!!.containerSpinWheelRewards.visibility = View.INVISIBLE
            mViewBinding!!.containerSpinWheelDefaultBanner.visibility = View.VISIBLE

            mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE

            spinWheelFragmentVM.callProductsDetailsApi(orderId)

        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_spin_wheel_history_view
    }

    override fun getViewModel(): FragmentSpinWheelVM = spinWheelFragmentVM

    private fun setBackgrounds() {
        mViewBinding?.let {
            setBackgroundDrawable(
                it.chipMyntsView,
                ContextCompat.getColor(this, R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this, R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipTicketView,
                ContextCompat.getColor(this, R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this, R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipCashView,
                ContextCompat.getColor(this, R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this, R.color.back_surface_color),
                0f,
                false
            )
        }
    }

    private fun vibrateDevice() {
        val vibrationEffect: VibrationEffect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.cancel()
            vibrator.vibrate(vibrationEffect)
        }
    }

    private fun setWheel(
        sectionList: List<SectionListItem>,
        rewardTypeOnFailure: String?
    ) {
        luckySpinWheelView.setRound(4)
        spinWheelFragmentVM.setDataInSpinWheel(sectionList, rewardTypeOnFailure)
        luckySpinWheelView.setData(spinWheelFragmentVM.luckyItemList)
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

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.visibility = View.VISIBLE
        invisibleImage.visibility = View.INVISIBLE
    }

    private fun setUpObserver(viewModel: FragmentSpinWheelVM) {
        viewModel.error.observe(
            this
        ) {
            mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
        }

        viewModel.state.observe(this) {
            handleState(it)
        }

        viewModel.totalRewardsResponse.observe(
            this
        ) { list ->
            mViewBinding?.tvSpinWheelCashCount?.text = String.format(
                getString(R.string.arcade_cash_value),
                Utility.convertToRs(list.amount.toString())
            )
        }

        viewModel.rewardSummaryStatus.observe(
            this
        ) { list ->
            if (list.totalPoints != null) {
                spinWheelFragmentVM.myntsCount = list.remainingPoints
                mViewBinding?.tvSpinWheelMyntsCount?.text =
                    String.format("%.0f", spinWheelFragmentVM.myntsCount)
            }
        }

        viewModel.stateMJ.observe(this) {
            when (it) {
                is FragmentSpinWheelVM.SpinWheelStateTicket.Error -> {

                }
                is FragmentSpinWheelVM.SpinWheelStateTicket.Success -> {
                    mViewBinding?.tvSpinWheelTicketsCount?.text = "${it.totalTickets}"
                }
                is FragmentSpinWheelVM.SpinWheelStateTicket.Loading -> {

                }
            }
        }

        viewModel.spinWheelResponseList.observe(this) { spinWheelRotation ->
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
                Toast.makeText(this, "Ex: $e", Toast.LENGTH_SHORT).show()
                Log.d("Spin", "Ex: $e")
            }


        }

        viewModel.redeemCallBackResponse.observe(this) {
            sectionId = it.sectionId

            vibrateDevice()

            arcadeSounds("SPINNER")

            luckySpinWheelView.startLuckyWheelWithTargetIndex(sectionId!! - 1)
            spinWheelFragmentVM.callSpinWheelApi(orderId)

        }
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

                Glide.with(this).load(R.drawable.play_button)
                    .into(mViewBinding!!.ivBtnPlayAnimation)

                Glide.with(this).load(it.spinWheelData.successResourceId)
                    .into(mViewBinding!!.ivBannerSpinWheel)

                sectionArrayList = ArrayList()

                sectionArrayList =
                    it.spinWheelData.sectionList as List<SectionListItem>

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

                if (spinWheelRotateResponseDetails.myntsWon != null) {
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

        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 1000)
    }

    private fun arcadeSounds(from: String) {
        when (from) {
            "MYNTS" -> {
                mp = MediaPlayer.create(
                    this,
                    R.raw.arcade_mynts
                )
            }
            "TICKETS" -> {
                mp = MediaPlayer.create(
                    this,
                    R.raw.arcade_golden_ticket
                )
            }
            "SPINNER" -> {
                mp = MediaPlayer.create(
                    this,
                    R.raw.arcade_spinner
                )
            }
            else -> {
                mp = MediaPlayer.create(
                    this,
                    R.raw.arcade_cashback
                )
            }
        }
        mp?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mp?.stop()
    }

}