package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.RechargeSuccessFragmentBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.viewmodel.RechargeSuccessfulFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


class DthSuccessFragment : BaseFragment<RechargeSuccessFragmentBinding, RechargeSuccessfulFragmentVM>() {

    private  val dthSuccessFragmentVM by viewModels<RechargeSuccessfulFragmentVM> { defaultViewModelProviderFactory }
    private lateinit var binding: RechargeSuccessFragmentBinding

    private val args: DthSuccessFragmentArgs by navArgs()

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }
    override fun getLayoutId(): Int {
        return R.layout.recharge_success_fragment
    }

    override fun getViewModel(): RechargeSuccessfulFragmentVM {
        return dthSuccessFragmentVM
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()
        dthSuccessFragmentVM.amount = args.amount
        dthSuccessFragmentVM.mobile = args.mobile
        binding.statusTitleTv.text = args.successDth?.merchantResponseMsg.toString()

        if (args.successDth != null) {
            if (args.successDth?.isPurchased == AppConstants.YES) {
                setToolbarAndTitle(
                    context = requireContext(),
                    toolbar = toolbar, backArrowTint = Color.WHITE,
                    titleColor = Color.WHITE,
                    isBackArrowVisible = true,
                    toolbarTitle = getString(R.string.recharge_successful)
                )
                binding.statusTitleTv.text = String.format(getString(R.string.your_recharge_is_successful),
                    Utility.convertToRs(dthSuccessFragmentVM.amount),dthSuccessFragmentVM.mobile)

                binding.comment.visibility = View.GONE

                binding.logo.setAnimation(R.raw.success);

            } else if (args.successDth?.isPurchased == AppConstants.NO) {
                setToolbarAndTitle(
                    context = requireContext(),
                    toolbar = toolbar, backArrowTint = Color.WHITE,
                    titleColor = Color.WHITE,
                    isBackArrowVisible = true,
                    toolbarTitle = getString(R.string.recharge_processing)
                )
                binding.statusTitleTv.text = String.format(getString(R.string.processing_recharge_of),Utility.convertToRs(dthSuccessFragmentVM.amount),dthSuccessFragmentVM.mobile)
                binding.comment.text = getString(R.string.please_do_not_press_back_or_close_the_app)
                binding.logo.setAnimation(R.raw.pending);
            }
        } else {
            setToolbarAndTitle(
                context = requireContext(),
                toolbar = toolbar, backArrowTint = Color.WHITE,
                titleColor = Color.WHITE,
                isBackArrowVisible = true,
                toolbarTitle = getString(R.string.recharge_failed)
            )
            binding.statusTitleTv.text = String.format(getString(R.string.your_recharge_is_failed),Utility.convertToRs(dthSuccessFragmentVM.amount),dthSuccessFragmentVM.mobile)
            binding.comment.text = getString(R.string.any_amount_detucetd)
            binding.continueBtn.setText(getString(R.string.retry_btn_text))
            binding.logo.setAnimation(R.raw.failed);
        }
        setObserver()


    }

    override fun onTryAgainClicked() {

    }

    private fun setObserver() {
        dthSuccessFragmentVM.event.observe(viewLifecycleOwner){
            handleEvent(it)
        }
    }

    private fun handleEvent(it: RechargeSuccessfulFragmentVM.RechargeSuccessfulEvent?) {
        when(it){
            RechargeSuccessfulFragmentVM.RechargeSuccessfulEvent.OnDoneAndRetryEvent ->{
                val direction = DthSuccessFragmentDirections.navigateFromPaymentSuccessToHome()
                findNavController().navigate(direction)
            }
            null -> TODO()
        }
    }


}
