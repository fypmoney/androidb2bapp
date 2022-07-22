package com.fypmoney.view.arcadegames.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentLeaderBoardBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.adapter.LeaderBoardAdapter
import com.fypmoney.view.arcadegames.adapter.LeaderBoardUiModel
import com.fypmoney.view.arcadegames.viewmodel.FragmentLeaderBoardVM
import kotlinx.android.synthetic.main.toolbar.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LeaderBoardFragment : BaseFragment<FragmentLeaderBoardBinding, FragmentLeaderBoardVM>() {

    private val leaderBoardFragmentVM by viewModels<FragmentLeaderBoardVM> { defaultViewModelProviderFactory }
    private var mViewBinding: FragmentLeaderBoardBinding? = null
    private lateinit var rulesList: List<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewBinding = getViewDataBinding()

        setBackgrounds()

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Rewards",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )

        leaderBoardFragmentVM.callLeaderBoardApi("SPIN_WHEEL_1000")

        setUpObserver(leaderBoardFragmentVM)

        setUpRecyclerView()

        mViewBinding!!.seekBarLeaderBoard.setOnTouchListener { _, _ -> true }
        mViewBinding!!.seekBarLeaderBoardThumb.setOnTouchListener { _, _ -> true }

        mViewBinding!!.seekBarLeaderBoard.setPadding(0, 0, 0, 0)
        mViewBinding!!.seekBarLeaderBoardThumb.setPadding(0, 0, 0, 0)

        mViewBinding!!.chipInfoView.setOnClickListener {
            showLeaderBoardRules(rulesList)
        }
    }

    private fun setUpRecyclerView() {
        val leaderBoardAdapter = LeaderBoardAdapter()

        with(mViewBinding!!.recyclerLeaderBoard) {
            adapter = leaderBoardAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                androidx.recyclerview.widget.RecyclerView.VERTICAL,
                false
            )
        }
    }

    private fun setUpObserver(leaderBoardFragmentVM: FragmentLeaderBoardVM) {
        leaderBoardFragmentVM.state.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(it: FragmentLeaderBoardVM.LeaderBoardState?) {
        when (it) {
            FragmentLeaderBoardVM.LeaderBoardState.Error -> {
            }

            FragmentLeaderBoardVM.LeaderBoardState.Loading -> {

            }
            is FragmentLeaderBoardVM.LeaderBoardState.Success -> {

                mViewBinding!!.tvLeaderboard1.text =
                    it.leaderBoardData?.leaderBoardList?.get(0)?.userName
                mViewBinding!!.tvLeaderboard2.text =
                    it.leaderBoardData?.leaderBoardList?.get(1)?.userName
                mViewBinding!!.tvLeaderboard3.text =
                    it.leaderBoardData?.leaderBoardList?.get(2)?.userName

                mViewBinding!!.tvSpinWheelTicketsCount.text =
                    it.leaderBoardData?.currUserGoldenTickets.toString()

                Glide.with(mViewBinding!!.ivCountDownBanner.context)
                    .load(it.leaderBoardData?.rewardProduct?.successResourceId)
                    .into(mViewBinding!!.ivCountDownBanner)

                Glide.with(mViewBinding!!.ivWinningReward.context)
                    .load(it.leaderBoardData?.rewardProduct?.detailResource)
                    .into(mViewBinding!!.ivWinningReward)

                (mViewBinding!!.recyclerLeaderBoard.adapter as LeaderBoardAdapter).submitList(
                    it.leaderBoardData?.leaderBoardList?.map {
                        it?.let { it1 ->
                            LeaderBoardUiModel.fromLeaderBoardItem(
                                requireContext(),
                                it1
                            )
                        }
                    }

                )

                val ticketsData: String =
                    it.leaderBoardData?.leaderBoardList?.get(0)?.goldenTickets?.minus(it.leaderBoardData?.currUserGoldenTickets!!)
                        .toString()

                val ticketDataFinal = "$ticketsData Golden Tickets"

                val mSpannableString = SpannableString(ticketDataFinal)

                mSpannableString.setSpan(
                    ForegroundColorSpan(Color.WHITE),
                    0,
                    ticketsData.length + 15,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                if ((it.leaderBoardData?.leaderBoardList?.get(0)?.goldenTickets?.minus(it.leaderBoardData?.currUserGoldenTickets!!))!! == 0) {
                    mViewBinding!!.seekBarLeaderBoard.toGone()
                    mViewBinding!!.ivWinningReward.toGone()
                    mViewBinding!!.tvLeaderBoardTicketsAway.toGone()
                } else {
                    mViewBinding!!.tvLeaderBoardTicketsAway.text = String.format(
                        getString(R.string.leaderboard_just_tickets),
                        mSpannableString
                    )
                    mViewBinding!!.seekBarLeaderBoard.toVisible()
                    mViewBinding!!.ivWinningReward.toVisible()
                    mViewBinding!!.tvLeaderBoardTicketsAway.toVisible()
                }

                val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
                val date = Date()
                val dateEndDate = formatter.parse(
                    Utility.parseDateTime(
                        it.leaderBoardData?.rewardProduct?.endDate,
                        AppConstants.SERVER_DATE_TIME_FORMAT1,
                        AppConstants.CHANGED_DATE_TIME_FORMAT10
                    )
                )

                val diff: Long = dateEndDate!!.time - date.time

                starTimer(diff)

                mViewBinding!!.tvTimerDays.text = "00"
                mViewBinding!!.tvTimerHours.text = "00"
                mViewBinding!!.tvTimerMinutes.text = "00"
                mViewBinding!!.tvTimerSeconds.text = "00"

                rulesList =
                    it.leaderBoardData!!.rewardProduct?.additionalInfo.toString().split(",")
                        .map { it1 -> it1.trim() }

                val percent: Int =
                    (it.leaderBoardData?.leaderBoardList?.get(0)?.goldenTickets!! / it.leaderBoardData?.currUserGoldenTickets!!)
                mViewBinding!!.seekBarLeaderBoard.progress = percent
                mViewBinding!!.seekBarLeaderBoardThumb.progress = percent

            }
        }
    }

    private fun showLeaderBoardRules(rulesList: List<String>) {
        val leaderBoardRulesBottomSheet = LeaderBoardRulesBottomSheet(rulesList)
        leaderBoardRulesBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        leaderBoardRulesBottomSheet.show(childFragmentManager, "LeaderBoardRulesSheet")
    }

    private fun starTimer(timeRemaining: Long) {

        object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val diffInDays = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                val diffInHours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                val diffInMin = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val diffInSec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

                mViewBinding!!.tvTimerDays.text = String.format("%d", diffInDays)
                mViewBinding!!.tvTimerHours.text = String.format("%02d", diffInHours % 24)
                mViewBinding!!.tvTimerMinutes.text = String.format("%02d", diffInMin % 60)
                mViewBinding!!.tvTimerSeconds.text = String.format("%02d", diffInSec % 60)

            }

            override fun onFinish() {

            }
        }.start()
    }

    override fun onTryAgainClicked() {}

    private fun setBackgrounds() {
        mViewBinding?.let {
            setBackgroundDrawable(
                it.ivLeaderboardNumber1,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.reward_golden_tickets_text),
                4f,
                true
            )

            setBackgroundDrawable(
                it.ivLeaderboardNumber2,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.rewardLeaderBoardSilver),
                4f,
                true
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
                it.ivLeaderboardNumber3,
                ContextCompat.getColor(this.requireContext(), R.color.back_surface_color),
                58f,
                ContextCompat.getColor(this.requireContext(), R.color.rewardLeaderBoardBronze),
                4f,
                true
            )

        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_leader_board
    }

    override fun getViewModel(): FragmentLeaderBoardVM = leaderBoardFragmentVM

}