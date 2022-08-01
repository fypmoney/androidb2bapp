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
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentRotatingTreasureBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.model.SpinWheelRotateResponseDetails
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.adapter.TreasureAdapterUiModel
import com.fypmoney.view.arcadegames.adapter.TreasurePagerAdapter
import com.fypmoney.view.arcadegames.model.SectionListItem1
import com.fypmoney.view.arcadegames.viewmodel.RotatingTreasureFragmentVM
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.dialog_rewards_insufficient.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.abs

class RotatingTreasureFragment :
    BaseFragment<FragmentRotatingTreasureBinding, RotatingTreasureFragmentVM>() {

    private var mViewBinding: FragmentRotatingTreasureBinding? = null
    private val rotatingTreasureVM by viewModels<RotatingTreasureFragmentVM> { defaultViewModelProviderFactory }
    private val compositePageTransformer = CompositePageTransformer()
    private var dialogInsufficientMynts: Dialog? = null
    private var listOfBoxes = arrayListOf<TreasureAdapterUiModel>()
    var adapter: TreasurePagerAdapter? = null
    private val navArgs by navArgs<RotatingTreasureFragmentArgs>()

    companion object {
        var sectionArrayList: List<SectionListItem1> = ArrayList()
    }

    private val sliderHandler: Handler = Handler(Looper.getMainLooper())
    private val sliderRunnable =
        Runnable {
            mViewBinding?.vpTreasuryBox?.currentItem = mViewBinding?.vpTreasuryBox!!.currentItem + 1
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

        rotatingTreasureVM.productCode = navArgs.productCode
        rotatingTreasureVM.productId = navArgs.orderId.toString()

        rotatingTreasureVM.callSingleProductApi(rotatingTreasureVM.productCode)

        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page: View, position: Float ->
            val r = 1 - abs(position)
            page.scaleY = 0.7f + r * 0.3f
        }

        Glide.with(this).load(R.drawable.coin_updated).into(mViewBinding!!.ivRotatingMyntsAnim)
        Glide.with(this).load(R.drawable.ticket_g).into(mViewBinding!!.ivRotatingTicketAnim)
        Glide.with(this).load(R.drawable.cash_g).into(mViewBinding!!.ivRotatingCashAnim)

        Glide.with(this).load(R.drawable.play_button)
            .into(mViewBinding!!.ivBtnPlayAnimation)

        setUpObserver(rotatingTreasureVM)

        setTreasureInitialData()

        setBackgrounds()

        mViewBinding!!.ivBtnPlayAnimation.setOnClickListener {
            if (rotatingTreasureVM.productId == null || rotatingTreasureVM.productId == "null") {
                if (rotatingTreasureVM.remainFrequency.value!! > 0) {
                    mViewBinding!!.lottieRotatingVP.toInvisible()

                    mViewBinding!!.containerRotatingTreasureRewards.visibility = View.INVISIBLE
                    mViewBinding!!.containerRotatingDefaultBanner.visibility = View.VISIBLE

                    mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
                    mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE

                    setViewVisibility(
                        mViewBinding!!.ivRotatingMyntsAnim,
                        mViewBinding!!.ivRotatingMynts
                    )

                    vibrateDevice()

                    rotatingTreasureVM.callMyntsBurnApi(rotatingTreasureVM.productCode)
                } else {
                    val limitOverBottomSheet = LimitOverBottomSheet()
                    limitOverBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                    limitOverBottomSheet.show(childFragmentManager, "LimitOverBottomSheet")
                }
            } else {
                mViewBinding!!.lottieRotatingVP.toInvisible()

                mViewBinding!!.containerRotatingTreasureRewards.visibility = View.INVISIBLE
                mViewBinding!!.containerRotatingDefaultBanner.visibility = View.VISIBLE

                mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE

                vibrateDevice()

                rotatingTreasureVM.callProductsDetailsApi(rotatingTreasureVM.productId)

            }

        }

        dialogInsufficientMynts = Dialog(this.requireContext())

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (rotatingTreasureVM.isArcadeIsPlayed) {
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

        mViewBinding?.vpTreasuryBox?.registerOnPageChangeCallback(object :
            OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (adapter!!.imagesList[position].isSelected) {
                    sliderHandler.removeCallbacks(sliderRunnable)
                    mViewBinding!!.lottieRotatingVP.setAnimation(adapter!!.imagesList[position].boxImage)
                    mViewBinding!!.lottieRotatingVP.toVisible()
                    mViewBinding!!.lottieRotatingVP.playAnimation()
                    setTreasureViewPagerData(rotatingTreasureVM.spinWheelRotateResponseDetails)
                } else {
                    if (position != 0) {
                        sliderHandler.removeCallbacks(sliderRunnable)
                        sliderHandler.postDelayed(sliderRunnable, 200)
                    }
                }
            }
        })

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

    private fun setTreasureInitialData() {

        listOfBoxes.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
        listOfBoxes.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))

        adapter = TreasurePagerAdapter(listOfBoxes, mViewBinding?.vpTreasuryBox!!)
        mViewBinding?.vpTreasuryBox?.adapter = adapter
        mViewBinding?.vpTreasuryBox?.isUserInputEnabled = false
        mViewBinding?.vpTreasuryBox?.clipToPadding = false
        mViewBinding?.vpTreasuryBox?.clipChildren = false
        mViewBinding?.vpTreasuryBox?.offscreenPageLimit = 3

        mViewBinding?.vpTreasuryBox?.setPageTransformer(compositePageTransformer)

        Handler(Looper.getMainLooper()).postDelayed({
            mViewBinding?.vpTreasuryBox?.setCurrentItem(1, true)
            sliderHandler.removeCallbacks(sliderRunnable)
        }, 20)

    }

    private fun setUpObserver(viewModel: RotatingTreasureFragmentVM) {

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
                mViewBinding!!.ivRotatingMynts,
                mViewBinding!!.ivRotatingMyntsAnim
            )
            setViewVisibility(
                mViewBinding!!.ivRotatingCash,
                mViewBinding!!.ivRotatingCashAnim
            )
            setViewVisibility(
                mViewBinding!!.ivRotatingTicket,
                mViewBinding!!.ivRotatingTicketAnim
            )
        }

        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

        viewModel.remainFrequency.observe(
            viewLifecycleOwner
        ) {
            mViewBinding!!.tvRotatingAttemptsLeft.text = String.format(
                getString(R.string.attempts_left),
                rotatingTreasureVM.remainFrequency.value, rotatingTreasureVM.frequency
            )
        }

        viewModel.coinsBurned.observe(
            viewLifecycleOwner
        ) { list ->

            if (list != null) {
                viewModel.coinsBurned.postValue(null)

                rotatingTreasureVM.remainFrequency.value =
                    rotatingTreasureVM.remainFrequency.value?.minus(1)

                decreaseCountAnimation(
                    mViewBinding!!.tvRotatingMyntsCount,
                    rotatingTreasureVM.myntsDisplay!!
                )

                rotatingTreasureVM.sectionId = list.sectionId

                rotatingTreasureVM.callSpinWheelApi(list.orderNo)

            }

        }

        viewModel.spinWheelResponseList.observe(viewLifecycleOwner) {
            rotatingTreasureVM.spinWheelRotateResponseDetails = it
            val listOfBoxes = arrayListOf<TreasureAdapterUiModel>()
            listOfBoxes.add(
                TreasureAdapterUiModel(
                    R.raw.silver_box_open,
                    rotatingTreasureVM.sectionId == 1
                )
            )
            listOfBoxes.add(
                TreasureAdapterUiModel(
                    R.raw.wood_box_open,
                    rotatingTreasureVM.sectionId == 2
                )
            )
            listOfBoxes.add(
                TreasureAdapterUiModel(
                    R.raw.gold_box_open,
                    rotatingTreasureVM.sectionId == 3
                )
            )
            listOfBoxes.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
            listOfBoxes.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
            listOfBoxes.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
            adapter!!.newTreasureImages = listOfBoxes
            sliderHandler.postDelayed(sliderRunnable, 200)

        }

        viewModel.redeemCallBackResponse.observe(viewLifecycleOwner) {
            rotatingTreasureVM.sectionId = it.sectionId

            rotatingTreasureVM.callSpinWheelApi(rotatingTreasureVM.productId)

        }

    }

    private fun setTreasureViewPagerData(spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails?) {

        spinWheelRotateResponseDetails?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                setWinningTreasureData(it)
            }, 200)
        }

    }

    private fun setWinningTreasureData(
        spinWheelRotateResponseDetails: SpinWheelRotateResponseDetails
    ) {

//        mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
//        mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE

        mViewBinding!!.containerRotatingTreasureRewards.visibility = View.VISIBLE
        mViewBinding!!.containerRotatingDefaultBanner.visibility = View.INVISIBLE

        mViewBinding!!.lottieRewardConfetti.visibility = View.VISIBLE
        mViewBinding!!.lottieRewardConfetti.playAnimation()

        if (spinWheelRotateResponseDetails.sectionValue == "0") {
            if (spinWheelRotateResponseDetails.noOfJackpotTicket == null) {
                mViewBinding!!.tvWinRewardsValue.text = String.format(
                    getString(R.string.arcade_mynts),
                    spinWheelRotateResponseDetails.myntsWon
                )

                mViewBinding!!.ivRotatingRewards.setImageResource(R.drawable.ic_mynt_rewards)

                mViewBinding!!.ivRotatingMynts.visibility = View.INVISIBLE
                mViewBinding!!.ivRotatingMyntsAnim.visibility = View.VISIBLE

                arcadeSounds("MYNTS")

                if (spinWheelRotateResponseDetails.myntsWon != null) {
//                    rotatingTreasureVM.myntsCount =
//                        spinWheelRotateResponseDetails.myntsWon?.toFloat()
                    increaseCountAnimation(
                        mViewBinding!!.tvRotatingMyntsCount,
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

                mViewBinding!!.ivRotatingTicket.visibility = View.INVISIBLE
                mViewBinding!!.ivRotatingTicketAnim.visibility = View.VISIBLE

                arcadeSounds("TICKETS")

                mViewBinding!!.ivRotatingRewards.setImageResource(R.drawable.ticket_reward)

                if (spinWheelRotateResponseDetails.noOfJackpotTicket != null) {
                    increaseCountAnimation(
                        mViewBinding!!.tvRotatingTicketsCount,
                        500,
                        spinWheelRotateResponseDetails.noOfJackpotTicket!!, "Ticket"
                    )
                }
            }
        } else {
            Glide.with(this).load(R.drawable.ic_wallet_rewards)
                .into(mViewBinding!!.ivRotatingRewards)
            mViewBinding!!.tvWinRewardsValue.text = String.format(
                getString(R.string.arcade_cashback),
                Utility.convertToRs(spinWheelRotateResponseDetails.sectionValue)
            )

            arcadeSounds("CASHBACK")


            increaseCountAnimation(
                mViewBinding!!.tvRotatingCashCount, 1000,
                Utility.convertToRs(spinWheelRotateResponseDetails.sectionValue)?.toInt()!!, "Cash"
            )
        }

        if (rotatingTreasureVM.productId == null || rotatingTreasureVM.productId == "null") {
            mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                rotatingTreasureVM.isArcadeIsPlayed = true
                mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
                mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE
                findNavController().navigateUp()
            }, 1500)
        }

    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.visibility = View.VISIBLE
        invisibleImage.visibility = View.INVISIBLE
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

    private fun arcadeSounds(from: String) {
        when (from) {
            "MYNTS" -> {
                rotatingTreasureVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_mynts
                )
            }
            "TICKETS" -> {
                rotatingTreasureVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_golden_ticket
                )
            }
            "SPINNER" -> {
                rotatingTreasureVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_spinner
                )
            }
            else -> {
                rotatingTreasureVM.mp = MediaPlayer.create(
                    this.requireContext(),
                    R.raw.arcade_cashback
                )
            }
        }
        rotatingTreasureVM.mp?.start()
    }

    private fun handleState(it: RotatingTreasureFragmentVM.RotatingTreasureState?) {
        when (it) {
            RotatingTreasureFragmentVM.RotatingTreasureState.Error -> {
            }

            RotatingTreasureFragmentVM.RotatingTreasureState.Loading -> {
//                mViewBinding!!.rotatingTreasureContainer.visibility = View.INVISIBLE
            }

            is RotatingTreasureFragmentVM.RotatingTreasureState.MyntsSuccess -> {
                if (it.remainingMynts != null) {
                    mViewBinding?.loadingMynts?.clearAnimation()
                    mViewBinding?.loadingMynts?.visibility = View.INVISIBLE

                    mViewBinding?.tvRotatingMyntsCount?.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }

            is RotatingTreasureFragmentVM.RotatingTreasureState.TicketSuccess -> {
                if (it.totalTickets != null) {
                    mViewBinding?.loadingTickets?.clearAnimation()
                    mViewBinding?.loadingTickets?.visibility = View.INVISIBLE

                    mViewBinding?.tvRotatingTicketsCount?.text = "${it.totalTickets}"
                }
            }

            is RotatingTreasureFragmentVM.RotatingTreasureState.CashSuccess -> {
                mViewBinding?.loadingCash?.clearAnimation()
                mViewBinding?.loadingCash?.visibility = View.INVISIBLE

                mViewBinding?.tvRotatingCashCount?.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash.toString())
                )
            }

            is RotatingTreasureFragmentVM.RotatingTreasureState.Success -> {

                Utility.setImageUsingGlideWithShimmerPlaceholderWithoutNull(
                    this.context,
                    it.treasureBoxItem.successResourceId,
                    mViewBinding!!.ivBannerRotatingTreasures
                )

                rotatingTreasureVM.myntsDisplay = it.treasureBoxItem.appDisplayText?.toInt()

                mViewBinding?.loadingBurnMynts?.clearAnimation()
                mViewBinding?.loadingBurnMynts?.visibility = View.INVISIBLE

                mViewBinding!!.tvRotatingBurnMyntsCount.text = it.treasureBoxItem.appDisplayText

                rotatingTreasureVM.frequency = it.treasureBoxItem.frequency

                mViewBinding!!.shimmerLayoutRotatingTreasure.toInvisible()
                mViewBinding!!.frameBtnContainer.toVisible()
                mViewBinding!!.tvRotatingAttemptsLeft.toVisible()
                mViewBinding!!.vpTreasuryBox.toVisible()

                sectionArrayList = it.treasureBoxItem.sectionList as List<SectionListItem1>

                rotatingTreasureVM.remainFrequency.value =
                    it.treasureBoxItem.frequencyPlayed?.let { it1 ->
                        it.treasureBoxItem.frequency?.minus(
                            it1
                        )
                    }
            }

//            is FragmentRotatingTreasureVM.RotatingTreasureState.MyntsBurnSuccess -> {
//                rotatingTreasureVM.coinsBurned.postValue(null)
//
//                rotatingTreasureVM.remainFrequency.value =
//                    rotatingTreasureVM.remainFrequency.value?.minus(1)
//
//                decreaseCountAnimation(mViewBinding!!.tvRotatingMyntsCount, myntsDisplay!!)
//
//                rotatingTreasureVM.sectionId = it.coinsBurnedResponse.sectionId
//
//                rotatingTreasureVM.callSpinWheelApi(it.coinsBurnedResponse.orderNo)
//
//            }
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
        return R.layout.fragment_rotating_treasure
    }

    override fun getViewModel(): RotatingTreasureFragmentVM = rotatingTreasureVM

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

    private fun decreaseCountAnimation(textScore: TextView, finalCount: Int) {
        if(!textScore.text.isNullOrEmpty()){
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
                setViewVisibility(mViewBinding!!.ivRotatingMynts, mViewBinding!!.ivRotatingMyntsAnim)
            }, 1500)
        }else{
            FirebaseCrashlytics.getInstance().recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
            Utility.showToast("Please check history")
        }

    }

    private fun increaseCountAnimation(
        textScore: TextView,
        animDuration: Long,
        finalCount: Int,
        via: String
    ) {
        if(!textScore.text.isNullOrEmpty()){
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
                setViewVisibility(mViewBinding!!.ivRotatingMynts, mViewBinding!!.ivRotatingMyntsAnim)
                setViewVisibility(mViewBinding!!.ivRotatingTicket, mViewBinding!!.ivRotatingTicketAnim)
                setViewVisibility(mViewBinding!!.ivRotatingCash, mViewBinding!!.ivRotatingCashAnim)
                mViewBinding!!.lottieRewardConfetti.visibility = View.INVISIBLE
            }, animDuration)
        }
        else{
            FirebaseCrashlytics.getInstance().recordException(Throwable("Unable to decrease mynts. ${textScore.text}"))
            Utility.showToast("Please check history")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        rotatingTreasureVM.mp?.stop()
    }
}