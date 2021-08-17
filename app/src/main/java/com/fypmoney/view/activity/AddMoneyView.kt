package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMemberBinding
import com.fypmoney.databinding.ViewInviteRejectedBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.AddMoneyViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*

/*
* This class is used to add money
* */
class AddMoneyView : BaseActivity<ViewAddMemberBinding, AddMoneyViewModel>(){
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
            isBackArrowVisible = true,toolbarTitle =getString(R.string.add_money_screen_title)
        )
        setObserver()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        add_money_editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (s.toString().startsWith("0")) {
                    s.clear()
                }
            }
        })
        mViewModel.setEdittextLength.observe(this) {
            if (it) {
                add_money_editext.setSelection(add_money_editext.text.length);
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
