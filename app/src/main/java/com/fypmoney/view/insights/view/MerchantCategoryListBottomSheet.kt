package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetMerchantCategoryListBinding
import com.fypmoney.view.insights.adapter.MerchantCategoryListAdapter
import com.fypmoney.view.insights.model.MerchantCategoryUiModel
import com.fypmoney.view.insights.viewmodel.MerchantCategoryListBottomSheetVM

class MerchantCategoryListBottomSheet(val categoryList:List<MerchantCategoryUiModel>, val onCategoryClick:(categoryCode:String)->Unit):BaseBottomSheetFragment<BottomsheetMerchantCategoryListBinding>() {
    private val merchantCategoryListBottomSheetVM by viewModels<MerchantCategoryListBottomSheetVM> { defaultViewModelProviderFactory }

    override val baseFragmentVM: BaseViewModel
        get() = merchantCategoryListBottomSheetVM
    override val customTag: String
        get() = MerchantCategoryListBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottomsheet_merchant_category_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        merchantCategoryListBottomSheetVM.listOfCategory = categoryList
        setUpBinding()
        setupObserver()
        merchantCategoryListBottomSheetVM.showCategories()
    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = merchantCategoryListBottomSheetVM
        }

        binding.rvCategoryList.adapter = MerchantCategoryListAdapter(lifecycleOwner = viewLifecycleOwner,
            onCategoryClick = {
                onCategoryClick(it)
                dismiss()
            })
    }

    private fun setupObserver() {
        merchantCategoryListBottomSheetVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: MerchantCategoryListBottomSheetVM.MerchantCategoryListState?) {
        when(it){
            is MerchantCategoryListBottomSheetVM.MerchantCategoryListState.ShowCategoryList -> {
                (binding.rvCategoryList.adapter as MerchantCategoryListAdapter).submitList(it.list)
            }
            null -> {

            }
        }
    }
}