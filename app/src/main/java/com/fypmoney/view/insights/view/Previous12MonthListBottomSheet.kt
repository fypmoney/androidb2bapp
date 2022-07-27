package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetLast12MonthListBinding
import com.fypmoney.view.insights.adapter.MonthListAdapter
import com.fypmoney.view.insights.viewmodel.MonthItem
import com.fypmoney.view.insights.viewmodel.Previous12MonthListBottomSheetVM


class Previous12MonthListBottomSheet(
    private val monthsList:List<MonthItem>,
    private val onMonthSelected:(position:Int)->Unit):
    BaseBottomSheetFragment<BottomsheetLast12MonthListBinding>() {

    private val previous12MonthListBottomSheetVM by viewModels<Previous12MonthListBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
        get() = previous12MonthListBottomSheetVM
    override val customTag: String
        get() = Previous12MonthListBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottomsheet_last_12_month_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setupObserver()
        previous12MonthListBottomSheetVM.monthList = monthsList
        previous12MonthListBottomSheetVM.showMonthList()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = previous12MonthListBottomSheetVM
        }
        binding.rvMonthList.adapter = MonthListAdapter(lifecycleOwner = viewLifecycleOwner,
            onMonthClick = {
                onMonthSelected(it)
                dismiss()
            })
    }
    private fun setupObserver() {
        previous12MonthListBottomSheetVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: Previous12MonthListBottomSheetVM.Previous12MonthState?) {
        when(it){
            is Previous12MonthListBottomSheetVM.Previous12MonthState.ShowMonths -> {
                (binding.rvMonthList.adapter as MonthListAdapter).submitList(it.monthList)
            }
            null -> {}
        }
    }
}