package com.fypmoney.view.giftcard.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.FragmentCreateGiftCardPaymentStatusBinding
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.Utility
import com.fypmoney.util.VOCHER_DETAILS_STATUS_NULL
import com.fypmoney.view.giftcard.viewModel.CreateGiftCardPaymentStatusFragmentVM
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CreateGiftCardPaymentStatusFragment : BaseFragment<FragmentCreateGiftCardPaymentStatusBinding, CreateGiftCardPaymentStatusFragmentVM>() {

    companion object {
        fun newInstance() = CreateGiftCardPaymentStatusFragment()
    }

    private val createGiftCardPaymentSuccessFragmentVM by viewModels<CreateGiftCardPaymentStatusFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: FragmentCreateGiftCardPaymentStatusBinding
    private val args:CreateGiftCardPaymentStatusFragmentArgs by navArgs()

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
    override fun getLayoutId(): Int  = R.layout.fragment_create_gift_card_payment_status

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): CreateGiftCardPaymentStatusFragmentVM = createGiftCardPaymentSuccessFragmentVM


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        args.purchasedGiftCardStatusUiModel?.let {
            createGiftCardPaymentSuccessFragmentVM.purchasedGiftCardStatusUiModel = it
            setUpObserver()
            createGiftCardPaymentSuccessFragmentVM.checkForStatus()
        }?: kotlin.run {
            FirebaseCrashlytics.getInstance().recordException(Throwable(VOCHER_DETAILS_STATUS_NULL))
            Utility.showToast(getString(R.string.please_try_again_after_some_time))
        }

    }

    private fun setUpObserver() {
        createGiftCardPaymentSuccessFragmentVM.state.observe(viewLifecycleOwner){
            handelState(it)
        }
        createGiftCardPaymentSuccessFragmentVM.event.observe(viewLifecycleOwner){
            handelEvent(it)
        }
    }

    private fun handelState(it: CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessState?) {
        when(it){
            is CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessState.Failed -> {
                    it.purchasedGiftCardStatusUiModel.run {
                        binding.comment.text = subTitle
                        binding.statusTitleTv.text = String.format(getString(R.string.your_gift_card_prurchase_of_amount_failed),amount)
                        binding.logo.setAnimation(statusAnimRes)
                        binding.reccivedMyntsFl.toGone()
                        if(actionButtonVisibility){
                            binding.continueBtn.toVisible()
                            binding.continueBtn.setText(getString(R.string.retry_btn_text))
                        }
                    }

            }
            is CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessState.Pending -> {
                it.purchasedGiftCardStatusUiModel.run {
                    binding.comment.text = subTitle
                    binding.statusTitleTv.text = String.format(getString(R.string.your_gift_card_prurchase_of_amount_pending),amount)
                    binding.logo.setAnimation(statusAnimRes)
                    binding.reccivedMyntsFl.toGone()
                    if(actionButtonVisibility){
                        binding.continueBtn.toVisible()
                        binding.continueBtn.setText(getString(R.string.continue_btn_text))
                    }
                }
            }
            is CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessState.Success -> {
                it.purchasedGiftCardStatusUiModel.run {
                    binding.comment.text = subTitle
                    binding.statusTitleTv.text = String.format(getString(R.string.your_gift_card_prurchase_of_amount_successful),amount)
                    binding.logo.setAnimation(statusAnimRes)
                    if(myntsVisibility){
                        binding.reccivedMyntsFl.toVisible()
                        binding.reccivedMyntsTv.text = String.format(getString(R.string.you_have_won_mynts),myntsEarned,amount)
                    }else{
                        binding.reccivedMyntsFl.toGone()
                    }
                    binding.continueBtn.toGone()
                }
             }
            null -> TODO()
        }
    }
    private fun handelEvent(it: CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessEvent?) {
        when(it){
            is CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessEvent.NavigateToGiftCardDetails -> {
                findNavController().navigate(CreateGiftCardPaymentStatusFragmentDirections.actionPurchasedGiftCardPaymentSuccessToGiftCardDetails(it.giftCardId))
            }
            CreateGiftCardPaymentStatusFragmentVM.CreateGiftCardPaymentSuccessEvent.NavigateToHome -> {
                findNavController().navigate(CreateGiftCardPaymentStatusFragmentDirections.navigationPurchasedGiftCardPaymentSuccessToHome())
            }
            null -> TODO()
        }
    }

}