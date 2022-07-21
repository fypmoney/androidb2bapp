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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
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
import com.fypmoney.view.arcadegames.adapter.CircularPagerAdapter
import com.fypmoney.view.arcadegames.model.SectionListItem1
import com.fypmoney.view.arcadegames.viewmodel.FragmentRotatingTreasureVM
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.math.abs


class RotatingTreasureFragment :
    BaseFragment<FragmentRotatingTreasureBinding, FragmentRotatingTreasureVM>() {

    private var mp: MediaPlayer? = null
    private var mViewBinding: FragmentRotatingTreasureBinding? = null
    private val rotatingTreasureVM by viewModels<FragmentRotatingTreasureVM> { defaultViewModelProviderFactory }
    private val compositePageTransformer = CompositePageTransformer()
    private var orderId: String? = null

    //    private lateinit var treasureImages: ArrayList<Int>
    var listOfFragments = arrayListOf<Fragment>()

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
            rotatingTreasureVM.btnClickCount += 1
            mViewBinding!!.lottieRotatingVP.toInvisible()

            mViewBinding!!.containerRotatingTreasureRewards.visibility = View.INVISIBLE
            mViewBinding!!.containerRotatingDefaultBanner.visibility = View.VISIBLE

            mViewBinding!!.ivBtnPlayAnimation.visibility = View.INVISIBLE
            mViewBinding!!.progressBtnPlay.visibility = View.VISIBLE
            sliderHandler.postDelayed(sliderRunnable,500)
            rotatingTreasureVM.callMyntsBurnApi(rotatingTreasureVM.productCode)
            mViewBinding?.vpTreasuryBox?.registerOnPageChangeCallback(object :
                OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if(rotatingTreasureVM.sectionId !=null && (rotatingTreasureVM.sectionId == (position % listOfFragments.size)+1)){
                        sliderHandler.removeCallbacks(sliderRunnable)
                        Log.d("Win","${rotatingTreasureVM.sectionId} and ${position % listOfFragments.size}")
                        when (rotatingTreasureVM.sectionId) {
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
                        setTreasureViewPagerData()
                    }else{
                        sliderHandler.postDelayed(sliderRunnable,500)

                    }
                }
            })

        }




    }


    private fun setTreasureInitialData() {
        listOfFragments.add(TreasureItemFragment(R.raw.silver_box_open))
        listOfFragments.add(TreasureItemFragment(R.raw.wood_box_open))
        listOfFragments.add(TreasureItemFragment(R.raw.gold_box_open))
        val adapter = CircularPagerAdapter(childFragmentManager, lifecycle,listOfFragments)
        mViewBinding?.vpTreasuryBox?.adapter = adapter
        mViewBinding?.vpTreasuryBox?.isUserInputEnabled = false
        mViewBinding?.vpTreasuryBox?.clipToPadding = false
        mViewBinding?.vpTreasuryBox?.clipChildren = false
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

                decreaseCountAnimation(mViewBinding!!.tvRotatingMyntsCount, 1500, 10)
                rotatingTreasureVM.sectionId = list.sectionId
                orderId = list.orderNo

                rotatingTreasureVM.noOfJackpotTickets = list.noOfJackpotTicket


            }
            viewModel.coinsBurned.postValue(null)

        }

        viewModel.spinWheelResponseList.observe(this.viewLifecycleOwner) {
            mp?.stop()
        }

    }

    private fun setTreasureViewPagerData() {


        sectionArrayList.forEach { item ->
            if (item.id == rotatingTreasureVM.sectionId.toString()) {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    Toast.makeText(
                        this.context,
                        "section value: " + item.sectionValue,
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        setWinningTreasureData(item.sectionValue, orderId)
                    }, 200)
                }
                return@forEach
            }
        }
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

                Glide.with(this).load(it.treasureBoxItem.successResourceId).into(
                    mViewBinding!!.ivBannerRotatingTreasures
                )

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