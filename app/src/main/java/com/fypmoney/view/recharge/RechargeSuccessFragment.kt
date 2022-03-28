package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityRechargeSuccessBinding
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.PayAndRechargeViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class RechargeSuccessFragment :
    BaseFragment<ActivityRechargeSuccessBinding, PayAndRechargeViewModel>() {
    private var operator: OperatorResponse? = null
    private lateinit var mViewModel: PayAndRechargeViewModel
    private lateinit var mViewBinding: ActivityRechargeSuccessBinding
    private val args: RechargeSuccessFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_recharge_success
    }

    override fun getViewModel(): PayAndRechargeViewModel {
        mViewModel = ViewModelProvider(this).get(PayAndRechargeViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()



        mViewBinding.tvUserName.text = args.successResponse?.merchantResponseMsg.toString()

        Glide.with(requireContext()).load(args?.selectedOperator?.icon).into(mViewBinding.logo)





        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Recharge Successful"
        )

        setObserver()


    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        mViewBinding.continueBtn.setOnClickListener {
            findNavController().popBackStack()
        }


    }


}
