package com.fypmoney.view.activity


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewPayuSuccessBinding
import com.fypmoney.databinding.ViewSuccessCashbackBinding
import com.fypmoney.model.AddMoneyStep2ResponseDetails
import com.fypmoney.model.BankTransactionHistoryResponseDetails
import com.fypmoney.model.TransactionHistoryResponseDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.CashbackSuccessViewModel
import kotlinx.android.synthetic.main.toolbar.*


/*
* This class is used to handle pay u success view
* */
class PayUSuccessViewCashbackWon :
    BaseActivity<ViewSuccessCashbackBinding, CashbackSuccessViewModel>() {
    private lateinit var mViewModel: CashbackSuccessViewModel
    private lateinit var mViewBinding: ViewSuccessCashbackBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_success_cashback
    }

    override fun getViewModel(): CashbackSuccessViewModel {
        mViewModel = ViewModelProvider(this).get(CashbackSuccessViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()

        mViewModel.fromWhichScreen.set(intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN))
        when (intent.getStringExtra(AppConstants.FROM_WHICH_SCREEN)) {

            AppConstants.BANK_TRANSACTION -> {
                setToolbarAndTitle(
                    context = this@PayUSuccessViewCashbackWon,
                    toolbar = toolbar,
                    isBackArrowVisible = true
                )
                mViewModel.cashbankResponse.set(intent.getSerializableExtra(AppConstants.RESPONSE) as BankTransactionHistoryResponseDetails)
                mViewModel.setInitialData()
                mViewModel.cashbankResponse.get()?.paymentMode?.let {
                    if (it == "MerchantTransaction") {
                        mViewModel.callCashbackEarned()
                        mViewModel.callRewardsEarned()
                    }
                }

            }


        }

        setObserver()

        mViewBinding.getHelpButton.setOnClickListener {
            callFreshChat(applicationContext)
        }
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.cashbackEarnedError.observe(this, {
            if (it) {
                mViewBinding.cashbackCl.visibility = View.VISIBLE
            } else {
                mViewBinding.cashbackCl.visibility = View.GONE
            }
        })
        mViewModel.rewardEarnedError.observe(this, {
            if (it) {
                mViewBinding.rewardsCl.visibility = View.VISIBLE
            } else {
                mViewBinding.rewardsCl.visibility = View.GONE
            }
        })
    }


}
