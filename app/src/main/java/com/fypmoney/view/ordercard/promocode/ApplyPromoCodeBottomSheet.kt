package com.fypmoney.view.ordercard.promocode


import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.fypmoney.R
import com.fypmoney.base.BaseBottomSheetFragment
import com.fypmoney.base.BaseViewModel
import com.fypmoney.databinding.BottomsheetApplyPromoCodeBinding
import com.fypmoney.view.ordercard.model.UserOfferCard

class ApplyPromoCodeBottomSheet(var promoCodeAppliedSuccessfully:(userCard: UserOfferCard)->Unit): BaseBottomSheetFragment<BottomsheetApplyPromoCodeBinding>() {
    private val applyPromoCodeBottomSheetVM by viewModels<ApplyPromoCodeBottomSheetVM> {
        defaultViewModelProviderFactory
    }
    override val baseFragmentVM: BaseViewModel
        get() = applyPromoCodeBottomSheetVM
    override val customTag: String
        get() = ApplyPromoCodeBottomSheet::class.java.simpleName
    override val layoutId: Int
        get() = R.layout.bottomsheet_apply_promo_code

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBinding()
        setupObserver()
    }

    private fun setUpBinding(){
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = applyPromoCodeBottomSheetVM
        }

        binding.applyPromoCodeTet.doOnTextChanged { text, start, before, count ->
            binding.applyPromocodeBtn.isEnabled = text?.length != 0
        }
    }
    private fun setupObserver() {
        applyPromoCodeBottomSheetVM.event.observe(viewLifecycleOwner,{
            when(it){
                ApplyPromoCodeBottomSheetVM.ApplyPromoCodeEvent.OnApplyClickedEvent ->{
                    applyPromoCodeBottomSheetVM.callCheckPromoCodeApi(binding.applyPromoCodeTet.text.toString())
                }
            }
        })
        applyPromoCodeBottomSheetVM.state.observe(viewLifecycleOwner,{
            when(it){
                is ApplyPromoCodeBottomSheetVM.ApplyPromoCodeState.InvalidPromoCode -> {
                    binding.applyPromocodeEt.error = it.errorResponseInfo.msg
                    binding.applyPromocodeBtn.setBusy(false)

                }
                ApplyPromoCodeBottomSheetVM.ApplyPromoCodeState.Loading -> {
                    binding.applyPromocodeBtn.setBusy(true)
                }
                is ApplyPromoCodeBottomSheetVM.ApplyPromoCodeState.PromoCodeAppliedSuccessfully -> {
                    promoCodeAppliedSuccessfully(it.offerCard)
                    dismiss()
                }
            }
        })
    }
}