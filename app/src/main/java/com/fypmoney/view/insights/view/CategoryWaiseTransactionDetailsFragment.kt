package com.fypmoney.view.insights.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCategoryWaiseTransactionDetailsBinding
import com.fypmoney.util.Utility
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.toolbar.*

class CategoryWaiseTransactionDetailsFragment : BaseFragment<FragmentCategoryWaiseTransactionDetailsBinding,CategoryWaiseTransactionDetailsFragmentVM>() {

    companion object {
        fun newInstance() = CategoryWaiseTransactionDetailsFragment()
    }

    private val categoryWaiseTransactionDetailsFragmentVM by viewModels<CategoryWaiseTransactionDetailsFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentCategoryWaiseTransactionDetailsBinding
    private val navArgs:CategoryWaiseTransactionDetailsFragmentArgs by navArgs()
    override fun onTryAgainClicked() {
    }

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getLayoutId(): Int  = R.layout.fragment_category_waise_transaction_details

    override fun getViewModel(): CategoryWaiseTransactionDetailsFragmentVM  = categoryWaiseTransactionDetailsFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navArgs.txnDetail?.let {
            categoryWaiseTransactionDetailsFragmentVM.txnItem = it
        }?:run {
            FirebaseCrashlytics.getInstance().recordException(Throwable("${CategoryWaiseTransactionDetailsFragment::class.java.simpleName} :Txn not found"))
            findNavController().navigateUp()
        }
        binding = getViewDataBinding()
        setupViews()
        setupObserver()
        categoryWaiseTransactionDetailsFragmentVM.showData()
    }

    private fun setupViews() {
        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "",
            titleColor = Color.WHITE,
            backArrowTint = Color.WHITE
        )
    }

    private fun setupObserver() {
        categoryWaiseTransactionDetailsFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        categoryWaiseTransactionDetailsFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsState?) {
        when(it){
            is CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsState.ShowTxnDetails -> {
                with(it.categoryTxnDetailsUiModel){
                    Utility.setImageUsingGlideWithShimmerPlaceholder(
                        url = txnCategoryImage,
                        imageView = binding.ivCategory
                    )
                    binding.tvName.text = txnName
                    binding.tvMobileNo.text = txnUserMobileNumber
                    binding.tvAmount.text = txnAmount
                    binding.tvTxnStatusAndDateAndTime.text = txnStatusDateAndTime
                    binding.tvFypTxnIdValue.text = txnFypTxnId
                    binding.tvBankTxnIdValue.text = bankTxnId

                }

            }
            null -> {
            }
        }
    }

    private fun handelEvent(it: CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsEvent?) {
        when(it){
            CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsEvent.ShowHelp -> {
                callFreshChat(requireContext())
            }
            null -> {}
        }
    }

}