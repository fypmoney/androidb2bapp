package com.fypmoney.view.recharge


import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseFragment
import com.fypmoney.databinding.ActivityDthRechargeBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.recharge.viewmodel.DthViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used as Home Screen
* */
class DthRechargeFragment :
    BaseFragment<ActivityDthRechargeBinding, DthViewModel>() {
    private val args: DthRechargeFragmentArgs by navArgs()
    private lateinit var mViewModel: DthViewModel
    private lateinit var mViewBinding: ActivityDthRechargeBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_dth_recharge
    }

    override fun getViewModel(): DthViewModel {
        mViewModel = ViewModelProvider(this).get(DthViewModel::class.java)
        return mViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewBinding = getViewDataBinding()


        mViewModel.Selectedoperator.value = args.operator

        setToolbarAndTitle(
            context = requireContext(),
            toolbar = toolbar, backArrowTint = Color.WHITE,
            titleColor = Color.WHITE,
            isBackArrowVisible = true,
            toolbarTitle = "Dth Recharge"
        )

        setObserver()
        setBindings()


    }

    private fun setBindings() {
        mViewBinding.fetchdetails.setOnClickListener {
            if (!mViewBinding.dthNumber.text.isNullOrEmpty() && mViewBinding.dthNumber.text.length >= 8) {
                mViewModel.callGetDthInfo(mViewBinding.dthNumber.text.toString())
            } else {
                Utility.showToast("Enter correct number")
            }
        }


        mViewBinding.dthNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewBinding.continueBtn.isEnabled = !s.isNullOrEmpty() && s.length <= 10
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        mViewBinding.continueBtn.setOnClickListener {
            if (!mViewBinding.amount.text.isNullOrEmpty()) {
                mViewModel.callMobileRecharge(
                    mViewModel.Selectedoperator.value,
                    mViewModel.opertaorList.value,
                    mViewBinding.dthNumber.text.toString(),
                    mViewBinding.amount.text.toString()
                )
            } else {
                Utility.showToast("Enter bill amount")

            }


        }
    }

    override fun onTryAgainClicked() {

    }


    private fun setObserver() {
        mViewModel.paymentResponse.observe(viewLifecycleOwner) {
            var directions = DthRechargeFragmentDirections.actionGoToDthSuccess(
                successDth = it,
                selectedOperator = mViewModel.operatorResponse.get()
            )
            findNavController().navigate(directions)
        }

        mViewModel.opertaorList.observe(viewLifecycleOwner) {
//            (mViewBinding.rvOperator.adapter as OperatorSelectionAdapter).submitList(it)
            mViewBinding.amount.setText(it.amount?.toDoubleOrNull()?.toInt().toString())


        }
    }


}
