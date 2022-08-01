package com.fypmoney.view.insights.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCategoryWaiseTransactionListBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.view.insights.adapter.CategoryWiseTxnListAdapter
import com.fypmoney.view.insights.model.AllTxnItem
import kotlinx.android.synthetic.main.toolbar.*

class CategoryWaiseTransactionListFragment : BaseFragment<FragmentCategoryWaiseTransactionListBinding,CategoryWaiseTransactionListFragmentVM>() {

    companion object {
        fun newInstance() = CategoryWaiseTransactionListFragment()
    }


    private  val categoryWaiseTransactionListFragmentVM by viewModels<CategoryWaiseTransactionListFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding:FragmentCategoryWaiseTransactionListBinding

    override fun onTryAgainClicked() {
    }

    override fun getBindingVariable(): Int  = BR.viewModel

    override fun getLayoutId(): Int  = R.layout.fragment_category_waise_transaction_list

    override fun getViewModel(): CategoryWaiseTransactionListFragmentVM  = categoryWaiseTransactionListFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        arguments?.getString("categoryName")?.let {
            categoryWaiseTransactionListFragmentVM.categoryName = it

        }
        arguments?.getParcelableArrayList<AllTxnItem>("transactionList")?.let {
            categoryWaiseTransactionListFragmentVM.categoryWiseTxnList = it.toMutableList()
        }
        setupViews()
        setupObserver()
        categoryWaiseTransactionListFragmentVM.populateTxnList()
    }

    private fun setupViews() {
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = categoryWaiseTransactionListFragmentVM.categoryName,
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
        binding.rvTransactionList.adapter = CategoryWiseTxnListAdapter(
            lifecycleOwner =viewLifecycleOwner,
            onCategoryItemClick = {
                val item = categoryWaiseTransactionListFragmentVM.categoryWiseTxnList.find { allTxnItem->
                    allTxnItem.accReferenceNumber==it
                }
                val directions = CategoryWaiseTransactionListFragmentDirections.actionCategoryTxnListToTxnDetails(
                    txnDetail = item
                )
                findNavController().navigate(directions)
            })

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("category_updated")
            ?.observe(
                viewLifecycleOwner
            ) { result ->
                categoryWaiseTransactionListFragmentVM.categoryUpdated = result
            }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                if(categoryWaiseTransactionListFragmentVM.categoryUpdated){
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "category_updated",
                        true
                    )
                }
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun setupObserver() {
        categoryWaiseTransactionListFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: CategoryWaiseTransactionListFragmentVM.CategoryWiseTxnListState) {
        when(it){
            is CategoryWaiseTransactionListFragmentVM.CategoryWiseTxnListState.TxnFound -> {
                binding.shimmerLayoutLoading.toGone()
                binding.clEmptyState.toGone()
                binding.rvTransactionList.toVisible()
                (binding.rvTransactionList.adapter as CategoryWiseTxnListAdapter).submitList(it.categoriesWiseTransactionUiModel)
            }
            CategoryWaiseTransactionListFragmentVM.CategoryWiseTxnListState.TxnIsEmpty -> {
                binding.shimmerLayoutLoading.toGone()
                binding.clEmptyState.toVisible()
                binding.rvTransactionList.toGone()
            }
        }
    }

}