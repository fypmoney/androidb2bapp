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
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toInvisible
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.view.arcadegames.adapter.MultipleJackpotAdapter
import com.fypmoney.view.arcadegames.adapter.MultipleJackpotUiModel
import com.fypmoney.view.arcadegames.viewmodel.MultipleJackpotFragmentVM
import kotlinx.android.synthetic.main.toolbar.*

class MultipleJackpotsFragment :
    BaseFragment<FragmentMultipleJackpotsBinding, MultipleJackpotFragmentVM>() {

    private var mViewBinding: FragmentMultipleJackpotsBinding? = null
    private val multipleJackpotVM by viewModels<MultipleJackpotFragmentVM> { defaultViewModelProviderFactory }

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

        setUpObserver()

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

    private fun setUpObserver() {
        multipleJackpotVM.state.observe(viewLifecycleOwner) {
            handleState(it)
        }

        multipleJackpotVM.stateMynts.observe(viewLifecycleOwner) {
            handleStateMynts(it)
        }

        multipleJackpotVM.stateCash.observe(viewLifecycleOwner) {
            handleStateCash(it)
        }

        multipleJackpotVM.stateCouponCount.observe(viewLifecycleOwner) {
            handleCouponCountState(it)
        }
    }

    private fun handleStateCash(it: MultipleJackpotFragmentVM.CashState?) {
        when (it) {
            is MultipleJackpotFragmentVM.CashState.Success -> {
                mViewBinding?.loadingCash?.clearAnimation()
                mViewBinding?.loadingCash?.visibility = View.INVISIBLE

                mViewBinding?.tvMultipleJackpotsCashCount?.text = String.format(
                    getString(R.string.arcade_cash_value),
                    Utility.convertToRs(it.totalCash.toString())
                )
            }
            MultipleJackpotFragmentVM.CashState.Error -> {

            }
            MultipleJackpotFragmentVM.CashState.Loading -> {

            }
            else -> {}
        }
    }

    private fun handleStateMynts(it: MultipleJackpotFragmentVM.MyntsState?) {
        when (it) {
            is MultipleJackpotFragmentVM.MyntsState.Success -> {
                if (it.remainingMynts != null) {
                    mViewBinding?.loadingMynts?.clearAnimation()
                    mViewBinding?.loadingMynts?.visibility = View.INVISIBLE

                    mViewBinding?.tvMultipleJackpotsMyntsCount?.text =
                        String.format("%.0f", it.remainingMynts)
                }
            }
            MultipleJackpotFragmentVM.MyntsState.Error -> {

            }
            MultipleJackpotFragmentVM.MyntsState.Loading -> {

            }
            else -> {}
        }
    }

    private fun handleState(it: MultipleJackpotFragmentVM.MultipleJackpotsState?) {
        when (it) {
            is MultipleJackpotFragmentVM.MultipleJackpotsState.Error -> {

            }

            is MultipleJackpotFragmentVM.MultipleJackpotsState.Success -> {

                mViewBinding?.loadingTickets?.clearAnimation()
                mViewBinding?.loadingTickets?.visibility = View.INVISIBLE
                if (it.totalTickets != null) {
                    mViewBinding?.tvMultipleJackpotsTicketsCount?.text = "${it.totalTickets}"
                }

                mViewBinding!!.shimmerLayoutMultipleJackpots.toInvisible()

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
            is MultipleJackpotFragmentVM.MultipleJackpotsState.Loading -> {

            }
            else -> {}
        }
    }

    private fun handleCouponCountState(it: MultipleJackpotFragmentVM.CouponCountState?) {
        when (it) {

            is MultipleJackpotFragmentVM.CouponCountState.CouponCountSuccess -> {
                mViewBinding?.loadingActiveBrandedCoupon?.clearAnimation()
                mViewBinding?.loadingActiveBrandedCoupon?.toGone()

                mViewBinding?.tvBrandedActiveCouponsCount?.text = it.amount?.toString()

            }
            is MultipleJackpotFragmentVM.CouponCountState.Error -> {}

            MultipleJackpotFragmentVM.CouponCountState.Loading -> {}

            null -> {}
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

            setBackgroundDrawable(
                it.chipCouponActiveView,
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

    override fun getViewModel(): MultipleJackpotFragmentVM = multipleJackpotVM

}