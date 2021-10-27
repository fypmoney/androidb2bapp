package com.fypmoney.view.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.util.roundOfAmountToCeli
import com.fypmoney.viewmodel.AddMoneyViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.bottom_sheet_redeem_coins.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*
import kotlinx.android.synthetic.main.view_add_money.add_money_editext
import kotlinx.android.synthetic.main.view_add_money.btnSendOtp
import kotlinx.android.synthetic.main.view_enter_amount_for_pay_request.*
import java.lang.NumberFormatException

/*
* This class is used to add money
* */
class AddMoneyView : BaseActivity<ViewAddMoneyBinding, AddMoneyViewModel>(){
    private lateinit var mViewModel: AddMoneyViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_money
    }

    override fun getViewModel(): AddMoneyViewModel {
        mViewModel = ViewModelProvider(this).get(AddMoneyViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AddMoneyView,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = getString(R.string.add_money_screen_title)
        )
        val amount = intent.getStringExtra("amountshouldbeadded")
        setObserver()
        if (!amount.isNullOrEmpty()) {
            add_money_editext.setText(roundOfAmountToCeli(amount))
        }
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        add_money_editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try{
                    if (s.toString().startsWith("0")) {
                        s.clear()
                    } else {
                        if (s.toString().isNotEmpty()) {
                            btnSendOtp.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.text_color_dark))
                            btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    this@AddMoneyView,
                                    R.color.white
                                )
                            )
                            if (!mViewModel.remainingLoadLimitAmount.get()
                                    .isNullOrEmpty() && s.toString()
                                    .toInt() > mViewModel.remainingLoadLimitAmount.get()!!.toInt() / 100
                            ) {
                                add_money_editext.setText(
                                    (mViewModel.remainingLoadLimitAmount.get()!!
                                        .toInt() / 100).toString()
                                )
                                add_money_editext.setSelection(add_money_editext.text.length)
                            }

                        } else {
                            btnSendOtp.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.cb_grey))
                            btnSendOtp.setTextColor(
                                ContextCompat.getColor(
                                    this@AddMoneyView,
                                    R.color.grey_heading
                                )
                            )

                        }
                    }
                }catch (e:NumberFormatException){
                    mViewModel.remainingLoadLimit.get()?.let {
                        Utility.showToast(mViewModel.remainingLoadLimit.get())
                        FirebaseCrashlytics.getInstance()
                            .setCustomKey("load_amount", it)
                        FirebaseCrashlytics.getInstance().recordException(e)
                        add_money_editext.setText("")
                    }

                }

            }
        })
        mViewModel.setEdittextLength.observe(this) {
            if (it) {
                add_money_editext.setSelection(add_money_editext.text.length)
                mViewModel.setEdittextLength.value = false
                }
            }

            mViewModel.onAddClicked.observe(this) {
                if (it) {
                    intentToActivity(AddMoneyUpiDebitView::class.java)
                    // callBottomSheet(mViewModel.amountSelected.get()!!, getString(R.string.dummy_amount))
                    mViewModel.onAddClicked.value = false
                }
            }
        }

        override fun onTryAgainClicked() {

        }




        /**
         * Method to navigate to the different activity
         */
        private fun intentToActivity(aClass: Class<*>) {
            val intent = Intent(applicationContext, aClass)
            intent.putExtra(AppConstants.AMOUNT, mViewModel.amountSelected.get())
            startActivity(intent)
        }
    

}
