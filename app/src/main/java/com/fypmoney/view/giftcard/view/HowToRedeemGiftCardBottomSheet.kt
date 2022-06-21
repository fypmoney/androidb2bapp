package com.fypmoney.view.giftcard.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetHowToRedeemGiftCardBinding
import com.fypmoney.extension.SimpleTextAdapter
import com.fypmoney.view.giftcard.viewModel.HowToRedeemGiftCardBottomSheetVM
import com.fypmoney.view.home.main.home.view.UpiComingSoonBottomSheet

class HowToRedeemGiftCardBottomSheet(var redeemTxt:String,var redeemLink:String,var onRedeemNowClick:(link:String)->Unit): BaseBottomSheetFragment<BottomsheetHowToRedeemGiftCardBinding>() {

    private val howToRedeemGiftCardBottomSheetVM by viewModels<HowToRedeemGiftCardBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
    get() = howToRedeemGiftCardBottomSheetVM
    override val customTag: String
    get() = UpiComingSoonBottomSheet::class.java.simpleName
    override val layoutId: Int
    get() = R.layout.bottomsheet_how_to_redeem_gift_card

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        howToRedeemGiftCardBottomSheetVM.howToRedeemLink = redeemLink
        howToRedeemGiftCardBottomSheetVM.howToRedeemTxt = redeemTxt
        setupRecylerView()
        setupObserver()
        howToRedeemGiftCardBottomSheetVM.showHowToRedeemSteps()
    }

    private fun setupRecylerView() {
        binding.howToRedeemStepsRv.adapter = SimpleTextAdapter(viewLifecycleOwner)
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = howToRedeemGiftCardBottomSheetVM
        }
    }
    private fun setupObserver() {
        howToRedeemGiftCardBottomSheetVM.event.observe(viewLifecycleOwner) {
            when (it) {
                is HowToRedeemGiftCardBottomSheetVM.HowToRedeemGiftCardEvent.NavigateToBrandShoppingScreen -> {

                }
            }
        }
        howToRedeemGiftCardBottomSheetVM.state.observe(viewLifecycleOwner) {
            when (it) {
                is HowToRedeemGiftCardBottomSheetVM.HowToRedeemGiftCardState.HowToRedeemSteps -> {
                    (binding.howToRedeemStepsRv.adapter as SimpleTextAdapter).submitList(it.steps)
                }
            }
        }
    }
}