package com.fypmoney.view.insights.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCategoryWaiseTransactionDetailsBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
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
        categoryWaiseTransactionDetailsFragmentVM.callRewardsEarned()
        categoryWaiseTransactionDetailsFragmentVM.callCashbackEarned()
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
        categoryWaiseTransactionDetailsFragmentVM.rewardsState.observe(viewLifecycleOwner){
            handelRewardState(it)
        }
        categoryWaiseTransactionDetailsFragmentVM.cashbackState.observe(viewLifecycleOwner){
            handelCashbackState(it)
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
                    Utility.setImageUsingGlideWithShimmerPlaceholder(
                        url = txnCategoryImage,
                        imageView = binding.ivSelectedCategory
                    )
                    binding.tvCategoryName.text = txnCategory
                    if(isCategoryIsEditable){
                        binding.ivEdit.toVisible()
                    }else{
                        binding.ivEdit.toGone()
                    }
                }

            }
            null -> {
            }
            CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsState.ChangeMerchantCategoryError -> {

            }
            CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsState.MerchantCategoryListError -> {

            }
            is CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsState.MerchantCategoryUpdated -> {
                Utility.setImageUsingGlideWithShimmerPlaceholder(
                    url = it.changeCategory.iconLink,
                    imageView = binding.ivSelectedCategory
                )
                binding.tvCategoryName.text = it.changeCategory.categoryCode
            }
        }
    }

    private fun handelEvent(it: CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsEvent?) {
        when(it){
            CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsEvent.ShowHelp -> {
                callFreshChat(requireContext())
            }
            null -> {}
            is CategoryWaiseTransactionDetailsFragmentVM.CategoryWaiseTxnDetailsEvent.ShowMerchantCategoryListBottomsheet ->{
                val merchantCategoryListBottomSheet = MerchantCategoryListBottomSheet(
                    categoryList = it.category,
                    onCategoryClick = {
                        categoryWaiseTransactionDetailsFragmentVM.postCategoryChangeData(it)
                    })
                merchantCategoryListBottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
                merchantCategoryListBottomSheet.show(childFragmentManager, "MonthBottomSheet")
            }
        }
    }

    private fun handelRewardState(it: CategoryWaiseTransactionDetailsFragmentVM.RewardsEarnedState?) {
        when(it){
            CategoryWaiseTransactionDetailsFragmentVM.RewardsEarnedState.Error -> {
                binding.chipMyntsView.toGone()
            }
            CategoryWaiseTransactionDetailsFragmentVM.RewardsEarnedState.Loading -> {
                binding.chipMyntsView.toVisible()
                binding.loadingMynts.toVisible()
            }
            CategoryWaiseTransactionDetailsFragmentVM.RewardsEarnedState.RewardsNotReceived -> {
                binding.chipMyntsView.toGone()
            }
            is CategoryWaiseTransactionDetailsFragmentVM.RewardsEarnedState.Success -> {
                binding.chipMyntsView.toVisible()
                binding.loadingMynts.toGone()
                binding.tvRewardsMyntsCount.text  = it.rewardsEarned
            }
            null -> {}
        }
    }
    private fun handelCashbackState(it: CategoryWaiseTransactionDetailsFragmentVM.CashbackEarnedState?) {
        when(it){
            CategoryWaiseTransactionDetailsFragmentVM.CashbackEarnedState.CashbackNotReceived -> {
                binding.chipCashView.toGone()
            }
            CategoryWaiseTransactionDetailsFragmentVM.CashbackEarnedState.Error -> {
                binding.chipCashView.toGone()
            }
            CategoryWaiseTransactionDetailsFragmentVM.CashbackEarnedState.Loading -> {
                binding.chipCashView.toVisible()
                binding.loadingCash.toVisible()
            }
            is CategoryWaiseTransactionDetailsFragmentVM.CashbackEarnedState.Success -> {
                binding.chipCashView.toVisible()
                binding.loadingCash.toGone()
                binding.tvRewardCashCount.text  = it.cashbackAmount
            }
            null -> {}
        }
    }



}