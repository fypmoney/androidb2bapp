package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyUpiDebitBinding
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*

/*
* This class is used to handle school name city
* */
class AddMoneyUpiDebitView : BaseActivity<ViewAddMoneyUpiDebitBinding, AddMoneyUpiDebitViewModel>() {
    private lateinit var mViewModel: AddMoneyUpiDebitViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_money_upi_debit
    }

    override fun getViewModel(): AddMoneyUpiDebitViewModel {
        mViewModel = ViewModelProvider(this).get(AddMoneyUpiDebitViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@AddMoneyUpiDebitView,
            toolbar = toolbar,
            isBackArrowVisible = true,
            toolbarTitle = getString(R.string.add_money_screen_title)
        )
        setObserver()

      /*  val ss = SpannableString(getString(R.string.account_verification_sub_title))
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(applicationContext, R.color.text_color_dark)
            }
        }
        ss.setSpan(clickableSpan, 49, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvSubTitle.text = ss
        tvSubTitle.movementMethod = LinkMovementMethod.getInstance()
*/
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
     /*   mViewModel.onContinueClicked.observe(this) {
            intentToActivity()
        }
*/

    }


    /*
   * navigate to the HomeScreen
   * */
    private fun intentToActivity() {
        val intent = Intent(this@AddMoneyUpiDebitView, CreateAccountSuccessView::class.java)
        startActivity(intent)
        finish()
    }

}
