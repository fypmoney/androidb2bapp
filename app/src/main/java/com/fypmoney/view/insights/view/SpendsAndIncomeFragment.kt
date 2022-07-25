package com.fypmoney.view.insights.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentSpendsAndIncomeBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.insights.adapter.SpendAndIncomeCategoryListAdapter
import com.fypmoney.view.insights.adapter.SpendAndIncomeCategoryUiModel.Companion.mapNetworkResponseToSpendAndIncomeCategoryUiModel
import com.fypmoney.view.insights.viewmodel.InsightsFragmentVM

class SpendsAndIncomeFragment : BaseFragment<FragmentSpendsAndIncomeBinding, InsightsFragmentVM>() {

    private val spendsAndIncomeFragmentVM by viewModels<InsightsFragmentVM> ({ requireParentFragment() })

    private lateinit var binding:FragmentSpendsAndIncomeBinding

    private lateinit var pageType:String

    companion object {
        fun newInstance(selectedPage:String) = SpendsAndIncomeFragment().apply {
            arguments = bundleOf("pageType" to selectedPage)
        }
    }


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
    override fun getLayoutId(): Int  = R.layout.fragment_spends_and_income

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): InsightsFragmentVM  = spendsAndIncomeFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        arguments?.let {
            pageType = it.getString("pageType")!!
        }
        setupViews()
        setupObserver()
    }

    private fun setupViews() {
        binding.rvSpendsAndIncome.adapter = SpendAndIncomeCategoryListAdapter(
            lifeCycleOwner = viewLifecycleOwner,
            onCategoryClick = {

            })
    }


    private fun setupObserver() {
        spendsAndIncomeFragmentVM.state.observe(viewLifecycleOwner){
            when(it){
                InsightsFragmentVM.InsightsState.Error -> {
                    binding.shimmerLayoutLoading.toGone()
                    binding.ivNoSpendsAndIncome.toVisible()
                    binding.tvNoSpendsAndIncome.toVisible()
                    binding.rvSpendsAndIncome.toGone()

                }
                InsightsFragmentVM.InsightsState.Loading -> {
                    binding.shimmerLayoutLoading.toVisible()
                    binding.ivNoSpendsAndIncome.toGone()
                    binding.tvNoSpendsAndIncome.toGone()
                    binding.rvSpendsAndIncome.toGone()
                }
                is InsightsFragmentVM.InsightsState.Success -> {
                    binding.shimmerLayoutLoading.toGone()
                    binding.ivNoSpendsAndIncome.toGone()
                    binding.tvNoSpendsAndIncome.toGone()
                    binding.rvSpendsAndIncome.toVisible()
                    if(pageType==getString(R.string.spends)){
                        if(it.data.spent?.category.isNullOrEmpty()){
                            binding.shimmerLayoutLoading.toGone()
                            binding.ivNoSpendsAndIncome.toVisible()
                            binding.tvNoSpendsAndIncome.toVisible()
                            binding.rvSpendsAndIncome.toGone()
                        }else{
                            binding.shimmerLayoutLoading.toGone()
                            binding.ivNoSpendsAndIncome.toGone()
                            binding.tvNoSpendsAndIncome.toGone()
                            binding.rvSpendsAndIncome.toVisible()
                            (binding.rvSpendsAndIncome.adapter as SpendAndIncomeCategoryListAdapter).submitList(
                                it.data.spent?.category?.map { categoryItem->
                                    mapNetworkResponseToSpendAndIncomeCategoryUiModel(requireContext(),categoryItem!!)
                                }
                            )
                        }

                    }else if(pageType==getString(R.string.income)){
                        if(it.data.income?.category.isNullOrEmpty()){
                            binding.shimmerLayoutLoading.toGone()
                            binding.ivNoSpendsAndIncome.toVisible()
                            binding.tvNoSpendsAndIncome.toVisible()
                            binding.rvSpendsAndIncome.toGone()
                        }else{
                            binding.shimmerLayoutLoading.toGone()
                            binding.ivNoSpendsAndIncome.toGone()
                            binding.tvNoSpendsAndIncome.toGone()
                            binding.rvSpendsAndIncome.toVisible()
                            (binding.rvSpendsAndIncome.adapter as SpendAndIncomeCategoryListAdapter).submitList(
                                it.data.income?.category?.map { categoryItem->
                                    mapNetworkResponseToSpendAndIncomeCategoryUiModel(requireContext(),categoryItem!!)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}