package com.fypmoney.view.arcadegames.ui

import android.animation.ValueAnimator
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentRotatingTreasureBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.adapter.TreasureAdapterUiModel
import com.fypmoney.view.arcadegames.adapter.TreasurePagerAdapter
import com.fypmoney.view.arcadegames.model.SectionListItem1
import com.fypmoney.view.arcadegames.viewmodel.FragmentRotatingTreasureVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.abs


class RotatingTreasureFragment :
    BaseFragment<FragmentRotatingTreasureBinding, FragmentRotatingTreasureVM>() {

    private var mp: MediaPlayer? = null
    private var mViewBinding: FragmentRotatingTreasureBinding? = null
    private val rotatingTreasureVM by viewModels<FragmentRotatingTreasureVM> { defaultViewModelProviderFactory }
    private val sliderHandler: Handler = Handler(Looper.getMainLooper())
    private val compositePageTransformer = CompositePageTransformer()
    private var orderId: String? = null

    //    private lateinit var treasureImages: ArrayList<Int>
    private var sectionId: Int? = null

    private var currentRotateCount = 0
    private val navArgs by navArgs<RotatingTreasureFragmentArgs>()

    companion object {
        var sectionArrayList: List<SectionListItem1> = ArrayList()
    }

    private val sliderRunnable =
        Runnable {
            mViewBinding?.vpTreasuryBox?.currentItem = mViewBinding?.vpTreasuryBox!!.currentItem + 1
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

        rotatingTreasureVM.productCode = navArgs.productCode

        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page: View, position: Float ->
            val r = 1 - abs(position)
            page.scaleY = 0.6f + r * 0.4f
        }

        Glide.with(this).load(R.drawable.play_button)
            .into(mViewBinding!!.ivBtnPlayAnimation)

        setUpObserver(rotatingTreasureVM)

        setTreasureInitialData()

        setBackgrounds()

        mViewBinding!!.ivBtnPlayAnimation.setOnClickListener {
//            if (rotatingTreasureVM.remainFrequency.value!! > 0) {

            rotatingTreasureVM.btnClickCount += 1

            mViewBinding!!.lottieRotatingVP.toInvisible()
            mViewBinding!!.containerRotatingTreasureRewards.visibility = View.INVISIBLE
            mViewBinding!!.containerRotatingDefaultBanner.visibility = View.VISIBLE

            mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE

            rotatingTreasureVM.callMyntsBurnApi(rotatingTreasureVM.productCode)

//            }
        }

//        mViewBinding?.chipCashView?.setOnClickListener {
//            val intent = Intent(requireContext(), CashBackWonHistoryActivity::class.java)
//            startActivity(intent)
//        }
//
//        mViewBinding?.chipMyntsView?.setOnClickListener {
//            findNavController().navigate(R.id.navigation_rewards_history)
//        }
//
//        mViewBinding?.chipTicketView?.setOnClickListener {
//            findNavController().navigate(R.id.navigation_multiple_jackpots)
//        }

    }


    private fun setTreasureInitialData() {
        val treasureImages: ArrayList<TreasureAdapterUiModel> = ArrayList()
        treasureImages.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
        treasureImages.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
        treasureImages.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))

        val adapter = TreasurePagerAdapter()
        mViewBinding?.vpTreasuryBox?.adapter = adapter

        adapter.setList(treasureImages)

        Handler(Looper.getMainLooper()).postDelayed({
            mViewBinding?.vpTreasuryBox?.currentItem = 1
        }, 20)

        mViewBinding?.vpTreasuryBox?.isUserInputEnabled = false
        mViewBinding?.vpTreasuryBox?.clipToPadding = false
        mViewBinding?.vpTreasuryBox?.clipChildren = false
        mViewBinding?.vpTreasuryBox?.offscreenPageLimit = 3
        mViewBinding?.vpTreasuryBox?.getChildAt(0)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        mViewBinding?.vpTreasuryBox?.setPageTransformer(compositePageTransformer)

    }

    private fun setUpObserver(viewModel: FragmentRotatingTreasureVM) {

        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

        viewModel.totalRewardsResponse.observe(
            viewLifecycleOwner
        ) { list ->
            mViewBinding?.tvRotatingCashCount?.text = String.format(
                getString(R.string.arcade_cash_value),
                Utility.convertToRs(list.amount.toString())
            )
        }

        viewModel.rewardSummaryStatus.observe(
            viewLifecycleOwner
        ) { list ->
            if (list.totalPoints != null) {
                mViewBinding?.tvRotatingMyntsCount?.text =
                    String.format("%.0f", list.remainingPoints)
            }
        }

//        viewModel.totalJackpotAmount.observe(
//            viewLifecycleOwner
//        ) { list ->
//            if (list.count != null) {
//                mViewBinding?.tvRotatingTicketsCount?.text = "${list.count}"
//            }
//        }

        viewModel.stateRotTicket.observe(viewLifecycleOwner) {
            when (it) {
                is FragmentRotatingTreasureVM.RotatingTicket.Error -> {

                }

                is FragmentRotatingTreasureVM.RotatingTicket.Success -> {
                    if (it.totalTickets != null) {
                        mViewBinding?.tvRotatingTicketsCount?.text = "${it.totalTickets}"
                    }
                }

                is FragmentRotatingTreasureVM.RotatingTicket.Loading -> {

                }
            }

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
//            arcadeSounds("SPINNER")

            if (list != null) {

                rotatingTreasureVM.remainFrequency.value =
                    rotatingTreasureVM.remainFrequency.value?.minus(1)

                currentRotateCount = 0
                decreaseCountAnimation(mViewBinding!!.tvRotatingMyntsCount, 1500, 10)
                viewModel.coinsBurned.postValue(null)
                sectionId = list.sectionId
                orderId = list.orderNo

                rotatingTreasureVM.noOfJackpotTickets = list.noOfJackpotTicket

                Toast.makeText(this.context, "section id: $sectionId", Toast.LENGTH_SHORT).show()

                try {
//                    if (rotatingTreasureVM.btnClickCount > 1)
                    // (mViewBinding!!.vpTreasuryBox.adapter as TreasurePagerAdapter).clearList()
                    val treasureImages: ArrayList<TreasureAdapterUiModel> = ArrayList()
//                    if (rotatingTreasureVM.btnClickCount < 2) {
                    treasureImages.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.silver_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.wood_box_open, false))
                    treasureImages.add(TreasureAdapterUiModel(R.raw.gold_box_open, false))
                    treasureImages.add(
                        TreasureAdapterUiModel(
                            R.raw.silver_box_open,
                            sectionId == 1
                        )
                    )
                    treasureImages.add(
                        TreasureAdapterUiModel(
                            R.raw.wood_box_open,
                            sectionId == 2
                        )
                    )
                    treasureImages.add(
                        TreasureAdapterUiModel(
                            R.raw.gold_box_open,
                            sectionId == 3
                        )
                    )

                    (mViewBinding!!.vpTreasuryBox.adapter as TreasurePagerAdapter).setList(
                        treasureImages
                    )

//                    }

                    mViewBinding?.vpTreasuryBox?.registerOnPageChangeCallback(object :
                        OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            if (!treasureImages[position].isSelected) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    mViewBinding?.vpTreasuryBox?.currentItem =
                                        mViewBinding?.vpTreasuryBox!!.currentItem + 1
                                }, 200)
                            } else {
                                when (sectionId) {
                                    1 -> {
                                        mViewBinding!!.lottieRotatingVP.setAnimation(R.raw.silver_box_open)
                                    }
                                    2 -> {
                                        mViewBinding!!.lottieRotatingVP.setAnimation(R.raw.wood_box_open)
                                    }
                                    3 -> {
                                        mViewBinding!!.lottieRotatingVP.setAnimation(R.raw.gold_box_open)

                                    }
                                }
                                mViewBinding!!.lottieRotatingVP.toVisible()
                                mViewBinding!!.lottieRotatingVP.playAnimation()

                                setTreasureViewPagerData(position)

                            }
                        }
                    })

                } catch (e: Exception) {
                    Toast.makeText(this.requireContext(), "Ex: $e", Toast.LENGTH_SHORT).show()
                    Log.d("Spin", "Ex: $e")
                }
            }

        }

        viewModel.spinWheelResponseList.observe(this.viewLifecycleOwner) {
            mp?.stop()
            Toast.makeText(this.context, "Run", Toast.LENGTH_SHORT).show()

            //Update mynts and ticket values on finish
//            setResult(23)
//            finish()

        }

    }

    private fun setTreasureViewPagerData(position: Int) {

//        if ((currentRotateCount > 6) && (position % 3 == sectionId!! - 1)) {

        sectionArrayList.forEach { item ->
            if (item.id == sectionId.toString()) {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    Toast.makeText(
                        this.context,
                        "section value: " + item.sectionValue,
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        setWinningTreasureData(item.sectionValue, orderId)
                    }, 200)
//                        val treasureImages: ArrayList<TreasureAdapterUiModel> = ArrayList()

//                        treasureImages.add(TreasureAdapterUiModel(R.raw.silver_box_open,
//                            sectionId == 1
//                        ))
//                        treasureImages.add(TreasureAdapterUiModel(R.raw.wood_box_open, sectionId == 2))
//                        treasureImages.add(TreasureAdapterUiModel(R.raw.gold_box_open, sectionId == 3))

//                        (mViewBinding!!.vpTreasuryBox.adapter as TreasurePagerAdapter).setWinningList(treasureImages)

                }
                return@forEach
            }
        }

        Log.i("currentRotateCount:- ", currentRotateCount.toString())
    }

    private fun setWinningTreasureData(
        sectionValue: String?,
        orderNo: String?
    ) {
        if (sectionValue == "0") {
            mViewBinding!!.tvWinRewardsValue.text = String.format(
                getString(R.string.arcade_golden_tickets),
                rotatingTreasureVM.noOfJackpotTickets
            )

            mViewBinding!!.ivRotatingTicket.visibility = View.INVISIBLE
            mViewBinding!!.ivRotatingTicketAnim.visibility = View.VISIBLE

            arcadeSounds("TICKETS")

            if (rotatingTreasureVM.noOfJackpotTickets != null) {
                increaseCountAnimation(
                    mViewBinding!!.tvRotatingTicketsCount,
                    500,
                    rotatingTreasureVM.noOfJackpotTickets!!, "Ticket"
                )
            }

//            Handler(Looper.getMainLooper()).postDelayed({
//                setViewVisibility(
//                    mViewBinding!!.ivRotatingTicket,
//                    mViewBinding!!.ivRotatingTicketAnim
//                )
//            }, 1000)
        } else {
            Glide.with(this).load(R.drawable.ic_wallet_rewards)
                .into(mViewBinding!!.ivRotatingRewards)
            mViewBinding!!.tvWinRewardsValue.text = String.format(
                getString(R.string.arcade_cashback),
                Utility.convertToRs(sectionValue)
            )

            arcadeSounds("CASHBACK")


            increaseCountAnimation(
                mViewBinding!!.tvRotatingCashCount, 1000,
                Utility.convertToRs(sectionValue)?.toInt()!!, "Cash"
            )

//            Handler(Looper.getMainLooper()).postDelayed({
//                setViewVisibility(
//                    mViewBinding!!.ivRotatingCash,
//                    mViewBinding!!.ivRotatingCashAnim
//                )
//            }, 2000)
        }

        mViewBinding!!.ivBtnPlayAnimation.visibility = View.VISIBLE
        mViewBinding!!.progressBtnPlay.visibility = View.INVISIBLE

        mViewBinding!!.containerRotatingTreasureRewards.visibility = View.VISIBLE
        mViewBinding!!.containerRotatingDefaultBanner.visibility = View.INVISIBLE

        mViewBinding!!.lottieRewardConfetti.visibility = View.VISIBLE
        mViewBinding!!.lottieRewardConfetti.playAnimation()

        Toast.makeText(this.requireContext(), "Order No: $orderNo", Toast.LENGTH_SHORT).show()
        rotatingTreasureVM.callSpinWheelApi(orderNo)
    }

    private fun setViewVisibility(visibleImage: ImageView, invisibleImage: ImageView) {
        visibleImage.visibility = View.VISIBLE
        invisibleImage.visibility = View.INVISIBLE
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

    private fun handleState(it: FragmentRotatingTreasureVM.RotatingTreasureState?) {
        when (it) {
            FragmentRotatingTreasureVM.RotatingTreasureState.Error -> {
            }

            FragmentRotatingTreasureVM.RotatingTreasureState.Loading -> {
                mViewBinding!!.rotatingTreasureContainer.visibility = View.INVISIBLE
            }

            is FragmentRotatingTreasureVM.RotatingTreasureState.Success -> {

                mViewBinding!!.rotatingTreasureContainer.visibility = View.VISIBLE

//                Glide.with(this).load(it.treasureBoxItem.successResourceId).into(
//                    mViewBinding!!.ivBannerRotatingTreasures
//                )

                Utility.setImageUsingGlideWithShimmerPlaceholder(this.context, it.treasureBoxItem.successResourceId, mViewBinding!!.ivBannerRotatingTreasures)

                rotatingTreasureVM.noOfJackpotTickets = it.treasureBoxItem.noOfJackpotTicket

                mViewBinding!!.tvRotatingBurnMyntsCount.text = it.treasureBoxItem.appDisplayText

                rotatingTreasureVM.frequency = it.treasureBoxItem.frequency

                sectionArrayList = it.treasureBoxItem.sectionList as List<SectionListItem1>
                rotatingTreasureVM.remainFrequency.value =
                    it.treasureBoxItem.frequencyPlayed?.let { it1 ->
                        it.treasureBoxItem.frequency?.minus(
                            it1
                        )
                    }
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
        return R.layout.fragment_rotating_treasure
    }

    override fun getViewModel(): FragmentRotatingTreasureVM = rotatingTreasureVM

    private fun setBackgrounds() {
        mViewBinding?.let {
            setBackgroundDrawable(
                it.chipMyntsView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                38f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipTicketView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                38f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipCashView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                38f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )

            setBackgroundDrawable(
                it.chipMyntsBurnView,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                38f,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                0f,
                false
            )
        }
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
            setViewVisibility(mViewBinding!!.ivRotatingMynts, mViewBinding!!.ivRotatingMyntsAnim)
            setViewVisibility(mViewBinding!!.ivRotatingTicket, mViewBinding!!.ivRotatingTicketAnim)
            setViewVisibility(mViewBinding!!.ivRotatingCash, mViewBinding!!.ivRotatingCashAnim)
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
            setViewVisibility(mViewBinding!!.ivRotatingMynts, mViewBinding!!.ivRotatingMyntsAnim)
            setViewVisibility(mViewBinding!!.ivRotatingTicket, mViewBinding!!.ivRotatingTicketAnim)
            setViewVisibility(mViewBinding!!.ivRotatingCash, mViewBinding!!.ivRotatingCashAnim)
        }, animDuration)
    }


}