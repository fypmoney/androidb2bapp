package com.fypmoney.view.giftcard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCreateGiftCardPaymentProcessingBinding

class CreateGiftCardPaymentProcessingFragment : BaseFragment<FragmentCreateGiftCardPaymentProcessingBinding,CreateGiftCardPaymentProcessingFragmentVM>() {

    companion object {
        fun newInstance() = CreateGiftCardPaymentProcessingFragment()
    }

    private val createGiftCardPaymentProcessingFragmentVM by viewModels<CreateGiftCardPaymentProcessingFragmentVM> { defaultViewModelProviderFactory }
    private val args: CreateGiftCardPaymentProcessingFragmentArgs by navArgs()
    private lateinit var binding: FragmentCreateGiftCardPaymentProcessingBinding

    override fun onTryAgainClicked() {
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.fragment_create_gift_card_payment_processing

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): CreateGiftCardPaymentProcessingFragmentVM  = createGiftCardPaymentProcessingFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        createGiftCardPaymentProcessingFragmentVM.createEGiftCardModel = args.createEGiftCardModel!!
        createGiftCardPaymentProcessingFragmentVM.updateText()
        createGiftCardPaymentProcessingFragmentVM.purchaseGiftCardRequest()
    }

}