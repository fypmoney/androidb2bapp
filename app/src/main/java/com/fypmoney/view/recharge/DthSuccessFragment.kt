package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityRechargeSuccessBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.recharge.model.OperatorResponse
import com.fypmoney.view.recharge.viewmodel.SelectedPlanDetailsRechargeFragmentVM
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class DthSuccessFragment :
    BaseFragment<ActivityRechargeSuccessBinding, SelectedPlanDetailsRechargeFragmentVM>() {
    private var operator: OperatorResponse? = null
    private lateinit var mViewModel: SelectedPlanDetailsRechargeFragmentVM
    private lateinit var mViewBinding: ActivityRechargeSuccessBinding
    private val args: DthSuccessFragmentArgs by navArgs()
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    private var mobile: String? = null
    private var amount: String? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_recharge_success
    }

    override fun getViewModel(): SelectedPlanDetailsRechargeFragmentVM {
        mViewModel = ViewModelProvider(this).get(SelectedPlanDetailsRechargeFragmentVM::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()

        amount = args.amount
        mobile = args.mobile

        mViewBinding.tvUserName.text = args.successDth?.merchantResponseMsg.toString()




        if (args.successDth != null) {
            if (args.successDth?.isPurchased == AppConstants.YES) {
                setToolbarAndTitle(
                    context = requireContext(),
                    toolbar = toolbar, backArrowTint = Color.WHITE,
                    titleColor = Color.WHITE,
                    isBackArrowVisible = true,
                    toolbarTitle = "Recharge Successful"
                )
                mViewBinding.tvUserName.text = "Your recharge of ₹$amount to\n" +
                        " $mobile is successful."
                mViewBinding.comment.visibility = View.GONE

                mViewBinding.logo.setAnimation(R.raw.success);

            } else if (args.successDth?.isPurchased == AppConstants.NO) {
                setToolbarAndTitle(
                    context = requireContext(),
                    toolbar = toolbar, backArrowTint = Color.WHITE,
                    titleColor = Color.WHITE,
                    isBackArrowVisible = true,
                    toolbarTitle = "Recharge Processing"
                )
                mViewBinding.tvUserName.text = "Processing recharge for  ₹$amount to\n" +
                        " $mobile"
                mViewBinding.comment.text = "Please, do not press back or close the app"

                mViewBinding.logo.setAnimation(R.raw.pending);
            }
        } else {
            setToolbarAndTitle(
                context = requireContext(),
                toolbar = toolbar, backArrowTint = Color.WHITE,
                titleColor = Color.WHITE,
                isBackArrowVisible = true,
                toolbarTitle = "Recharge Failed"
            )
            mViewBinding.tvUserName.text = "Your recharge of ₹$amount to\n" +
                    " $mobile is failed."
            mViewBinding.comment.text = "Any amount deducted will be\n" +
                    "refunded within 24 hours"

            mViewBinding.logo.setAnimation(R.raw.failed);
        }




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
