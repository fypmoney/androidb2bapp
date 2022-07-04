package com.fypmoney.view.giftcard.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCreateGiftCardPaymentProcessingBinding
import com.fypmoney.view.giftcard.viewModel.CreateGiftCardPaymentProcessingFragmentVM

class CreateGiftCardPaymentProcessingFragment : BaseFragment<FragmentCreateGiftCardPaymentProcessingBinding, CreateGiftCardPaymentProcessingFragmentVM>() {

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
    override fun getViewModel(): CreateGiftCardPaymentProcessingFragmentVM = createGiftCardPaymentProcessingFragmentVM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        createGiftCardPaymentProcessingFragmentVM.createEGiftCardModel = args.createEGiftCardModel!!
        setUpObserver()
        createGiftCardPaymentProcessingFragmentVM.updateText()
        createGiftCardPaymentProcessingFragmentVM.purchaseGiftCardRequest()
    }

    private fun setUpObserver() {
        createGiftCardPaymentProcessingFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
        createGiftCardPaymentProcessingFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
    }

    private fun handelState(it: CreateGiftCardPaymentProcessingFragmentVM.CreateGiftCardPaymentProcessingState?) {
        when(it){
            CreateGiftCardPaymentProcessingFragmentVM.CreateGiftCardPaymentProcessingState.Error -> {

            }
            CreateGiftCardPaymentProcessingFragmentVM.CreateGiftCardPaymentProcessingState.Loading -> {

            }
            is CreateGiftCardPaymentProcessingFragmentVM.CreateGiftCardPaymentProcessingState.UpdateText -> {
                binding.titleTv.text = it.title
            }
            null -> {}
        }
    }

    private fun handelEvent(it: CreateGiftCardPaymentProcessingFragmentVM.CreateGiftCardPaymentProcessingEvent?) {
        when(it){
            is CreateGiftCardPaymentProcessingFragmentVM.CreateGiftCardPaymentProcessingEvent.NavigateToStatusScreen -> {
                findNavController().navigate(CreateGiftCardPaymentProcessingFragmentDirections.actionCreateGiftCardPaymentProcessingToPurchasedGiftCardPaymentSuccess(it.purchasedGiftCardStatusUiModel))
            }
            null -> {}
        }
    }

}