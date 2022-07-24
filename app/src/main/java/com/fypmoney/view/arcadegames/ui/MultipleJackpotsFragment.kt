package com.fypmoney.view.arcadegames.ui

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.bindingAdapters.setBackgroundDrawable
import com.fypmoney.databinding.FragmentMultipleJackpotsBinding
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.adapter.MultipleJackpotAdapter
import com.fypmoney.view.arcadegames.adapter.MultipleJackpotUiModel
import com.fypmoney.view.arcadegames.viewmodel.FragmentMultipleJackpotVM
import kotlinx.android.synthetic.main.toolbar.*

class MultipleJackpotsFragment :
    BaseFragment<FragmentMultipleJackpotsBinding, FragmentMultipleJackpotVM>() {

    private var mViewBinding: FragmentMultipleJackpotsBinding? = null
    private val multipleJackpotVM by viewModels<FragmentMultipleJackpotVM> { defaultViewModelProviderFactory }
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

        mViewBinding?.loadingMynts?.visibility = View.VISIBLE
        mViewBinding?.loadingTickets?.visibility = View.VISIBLE
        mViewBinding?.loadingCash?.visibility = View.VISIBLE

        setUpObserver(multipleJackpotVM)

        setBackgrounds()

        setUpRecentRecyclerView()

    }

    private fun setUpRecentRecyclerView() {
        val multipleJackpotAdapter = MultipleJackpotAdapter(onJackpotClick = {
            findNavController().navigate(Uri.parse("https://www.fypmoney.in/leaderboard/${it}"))
        })

        with(mViewBinding!!.recyclerMultipleJackpots) {
            adapter = multipleJackpotAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun setUpObserver(viewModel: FragmentMultipleJackpotVM) {

        viewModel.totalRewardsResponse.observe(
            viewLifecycleOwner
        ) { list ->

            mViewBinding?.loadingCash?.clearAnimation()
            mViewBinding?.loadingCash?.visibility = View.INVISIBLE

            mViewBinding?.tvMultipleJackpotsCashCount?.text =
                getString(R.string.rupee_symbol) + Utility.convertToRs("${list.amount}")
        }

        viewModel.rewardSummaryStatus.observe(
            viewLifecycleOwner
        ) { list ->
            mViewBinding?.loadingMynts?.clearAnimation()
            mViewBinding?.loadingMynts?.visibility = View.INVISIBLE

            if (list.totalPoints != null) {
                mViewBinding?.tvMultipleJackpotsMyntsCount?.text =
                    String.format("%.0f", list.remainingPoints)
            }
        }

        multipleJackpotVM.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

    }

    private fun handleState(it: FragmentMultipleJackpotVM.MultipleJackpotsState?) {
        when (it) {
            is FragmentMultipleJackpotVM.MultipleJackpotsState.Error -> {

            }
            is FragmentMultipleJackpotVM.MultipleJackpotsState.Success -> {

                mViewBinding?.loadingTickets?.clearAnimation()
                mViewBinding?.loadingTickets?.visibility = View.INVISIBLE
                if (it.totalTickets != null) {
                    mViewBinding?.tvMultipleJackpotsTicketsCount?.text = "${it.totalTickets}"
                }

                if (it.listOfJackpotDetailsItem?.isEmpty() == true) {
                    mViewBinding!!.emptyMultipleJackpots.toVisible()
                    mViewBinding!!.recyclerMultipleJackpots.toInvisible()
                } else {
                    mViewBinding!!.emptyMultipleJackpots.toInvisible()
                    mViewBinding!!.recyclerMultipleJackpots.toVisible()
                }

                (mViewBinding!!.recyclerMultipleJackpots.adapter as MultipleJackpotAdapter).submitList(
                    it.listOfJackpotDetailsItem?.map {
                        it?.let { it1 ->
                            MultipleJackpotUiModel.fromMultipleJackpotItem(
                                requireContext(),
                                it1
                            )
                        }
                    })
            }
            is FragmentMultipleJackpotVM.MultipleJackpotsState.Loading -> {

            }
        }
    }

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

        }
    }

    override fun onTryAgainClicked() {

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_multiple_jackpots
    }

    override fun getViewModel(): FragmentMultipleJackpotVM = multipleJackpotVM

}