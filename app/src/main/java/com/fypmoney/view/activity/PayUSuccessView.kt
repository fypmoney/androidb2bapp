package com.fypmoney.view.activity


import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewPayuSuccessBinding
import com.fypmoney.model.AddMoneyStep2ResponseDetails
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.PayUSuccessViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used to handle pay u success view
* */
class PayUSuccessView : BaseActivity<ViewPayuSuccessBinding, PayUSuccessViewModel>() {
    private lateinit var mViewModel: PayUSuccessViewModel
    private lateinit var mViewBinding: ViewPayuSuccessBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_payu_success
    }

    override fun getViewModel(): PayUSuccessViewModel {
        mViewModel = ViewModelProvider(this).get(PayUSuccessViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewModel.fromWhichScreen.set(intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN))
        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {
            AppConstants.ADD_MONEY -> {
                setToolbarAndTitle(
                    context = this@PayUSuccessView,
                    toolbar = toolbar,
                    isBackArrowVisible = false
                )
                mViewModel.payUResponse.set(intent.getSerializableExtra(AppConstants.RESPONSE) as AddMoneyStep2ResponseDetails)
                mViewModel.setInitialData()
            }
            AppConstants.BANK_TRANSACTION -> {
                setToolbarAndTitle(
                    context = this@PayUSuccessView,
                    toolbar = toolbar,
                    isBackArrowVisible = true
                )
                mViewModel.bankResponse.set(intent.getSerializableExtra(AppConstants.RESPONSE) as BankTransactionHistoryResponseDetails)
                mViewModel.setInitialData()
            }

        }

        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {

    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@PayUSuccessView, aClass)
        startActivity(intent)
        finish()
    }

}
