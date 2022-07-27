package com.fypmoney.view.insights.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentInsightsBinding
import com.fypmoney.view.activity.BankTransactionHistoryView
import com.fypmoney.view.insights.adapter.SpendAndIncomeStateAdapter
import com.fypmoney.view.insights.viewmodel.InsightsFragmentVM
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@FlowPreview
class InsightsFragment : BaseFragment<FragmentInsightsBinding,InsightsFragmentVM>() {

    companion object {
        fun newInstance() = InsightsFragment()
    }


    private  val insightsFragmentVM by viewModels<InsightsFragmentVM> { defaultViewModelProviderFactory }

    private lateinit var binding: FragmentInsightsBinding


    override fun onTryAgainClicked() {
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int =R.layout.fragment_insights

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): InsightsFragmentVM  = insightsFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        setUpViews()
        setupObserver()
    }

    private fun setUpViews() {
        binding.pager.adapter  = SpendAndIncomeStateAdapter(this, arrayOf(getString(R.string.spends),getString(R.string.income)))
        TabLayoutMediator(binding.tabLayoutSpendsIncome, binding.pager) { tab, position ->
            when(position){
                0->{
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_up_arrow)
                    tab.text = getString(R.string.spends)
                }
                1->{
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_down_arrow)
                    tab.text = getString(R.string.income)
                }
            }
        }.attach()
    }


    private fun setupObserver() {
        insightsFragmentVM.state.observe(viewLifecycleOwner) {
            handelState(it)
        }
        insightsFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: InsightsFragmentVM.InsightsState?) {
        when(it){
            InsightsFragmentVM.InsightsState.Error -> {

            }
            InsightsFragmentVM.InsightsState.Loading -> {

            }
            is InsightsFragmentVM.InsightsState.Success -> {
                binding.tvMonth.text = it.currentMonth
                it.data.spent?.total?.let {
                    binding.tvTotalSpendsValue.text = String.format(getString(R.string.amount_with_currency),it)

                }?: kotlin.run{
                    binding.tvTotalSpendsValue.text = String.format(getString(R.string.amount_with_currency),"0.00")
                }
                it.data.income?.total?.let {
                    binding.tvTotalIncomeValue.text = String.format(getString(R.string.amount_with_currency),it)
                }?: kotlin.run {
                    binding.tvTotalIncomeValue.text = String.format(getString(R.string.amount_with_currency),"0.00")
                }
            }
            null -> {

            }
            is InsightsFragmentVM.InsightsState.SelectNextMonth -> {
                binding.tvMonth.text = it.nextMonth

            }
            is InsightsFragmentVM.InsightsState.SelectPreviousMonth -> {
                binding.tvMonth.text = it.prevMonth

            }
        }
    }

    private fun handelEvent(it: InsightsFragmentVM.InsightsEvent?) {
        when(it){
            InsightsFragmentVM.InsightsEvent.ShowTransactionHistory -> {
                startActivity(Intent(requireActivity(),BankTransactionHistoryView::class.java))
            }
            null -> {

            }
            is InsightsFragmentVM.InsightsEvent.ShowMonthListClickEvent -> {
                val previous12MonthListBottomSheet = Previous12MonthListBottomSheet(
                    monthsList = it.listOfMonth,
                    onMonthSelected = {
                        insightsFragmentVM.selectedMonth.value = it
                    })
                previous12MonthListBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                previous12MonthListBottomSheet.show(childFragmentManager, "MonthBottomSheet")
            }
        }
    }
}