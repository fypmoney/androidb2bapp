package com.fypmoney.view.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewEnterAmountForPayRequestBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.view.fragment.TransactionFailBottomSheet
import com.fypmoney.viewmodel.EnterAmountForPayRequestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_enter_amount_for_pay_request.*


/*
* This class is used to handle school name city
* */
class EnterAmountForPayRequestView :
    BaseActivity<ViewEnterAmountForPayRequestBinding, EnterAmountForPayRequestViewModel>(),
    TransactionFailBottomSheet.OnBottomSheetClickListener {
    private lateinit var mViewModel: EnterAmountForPayRequestViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_enter_amount_for_pay_request
    }

    override fun getViewModel(): EnterAmountForPayRequestViewModel {
        mViewModel = ViewModelProvider(this).get(EnterAmountForPayRequestViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.setResponseAfterContactSelected(
            intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE),
            actionValue = intent.getStringExtra(AppConstants.WHICH_ACTION),
            qrCode = intent.getStringExtra(AppConstants.FUND_TRANSFER_QR_CODE)
        )

        add_money_editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (s.toString().startsWith("0")) {
                    s.clear()
                }
            }
        })
        when (mViewModel.action.get()) {
            AppConstants.PAY, AppConstants.PAY_USING_QR -> {
                setToolbarAndTitle(
                    context = this@EnterAmountForPayRequestView,
                    toolbar = toolbar,
                    isBackArrowVisible = true, toolbarTitle = getString(R.string.pay_title)
                )
            }
            AppConstants.REQUEST -> {
                setToolbarAndTitle(
                    context = this@EnterAmountForPayRequestView,
                    toolbar = toolbar,
                    isBackArrowVisible = true, toolbarTitle = getString(R.string.request_title)
                )
            }
        }

        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.setEdittextLength.observe(this) {
            if (it) {
                add_money_editext.text?.length?.let { it1 -> add_money_editext.setSelection(it1) };
                mViewModel.setEdittextLength.value = false
            }
        }

        mViewModel.onApiResponse.observe(this) {
            when (it) {
                AppConstants.API_FAIL -> {
                    callBottomSheet()
                }
                AppConstants.API_SUCCESS -> {
                    intentToActivity(HomeView::class.java)
                }
            }
        }

        mViewModel.onPayClicked.observe(this) {
            if (it) {
                askForDevicePassword()
                mViewModel.onPayClicked.value = false
            }
        }


    }

    override
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.DEVICE_SECURITY_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        runOnUiThread {
                            when (mViewModel.action.get()) {
                                AppConstants.PAY -> {
                                    mViewModel.callSendMoneyApi()

                                }
                                AppConstants.PAY_USING_QR -> {
                                    mViewModel.callSendMoneyUsingQrApi()

                                }
                                AppConstants.REQUEST -> {
                                    mViewModel.callRequestMoneyApi()

                                }
                            }

                        }

                    }

                }
            }
        }
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        startActivity(Intent(this@EnterAmountForPayRequestView, aClass))
        finishAffinity()
    }

    /*
  * This method is used to call leave member
  * */
    private fun callBottomSheet() {
        val bottomSheet =
            TransactionFailBottomSheet(
                AppConstants.PAY,
                mViewModel.amountSelected.get()!!, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        bottomSheet.show(supportFragmentManager, "TransactionFail")
    }

    override fun onBottomSheetButtonClick(type: String) {

    }

}
