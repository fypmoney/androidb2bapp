package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewAddMoneyUpiDebitBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.DialogUtils
import com.fypmoney.util.PayUGpayResponse
import com.fypmoney.viewmodel.AddMoneyUpiDebitViewModel
import com.payu.gpay.GPay
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_aadhaar_account_activation.*

/*
* This class is used to handle school name city
* */
class AddMoneyUpiDebitView :
    BaseActivity<ViewAddMoneyUpiDebitBinding, AddMoneyUpiDebitViewModel>(),
    PayUGpayResponse.OnGpayResponseListener {
    private lateinit var mViewModel: AddMoneyUpiDebitViewModel
    lateinit var progressBar: ProgressBar

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
        progressBar = ProgressBar(this)
        setObserver()
        mViewModel.amountToAdd.set(intent.getStringExtra(AppConstants.AMOUNT))
        mViewModel.callAddMoneyStep1Api()


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
        mViewModel.onUpiClicked.observe(this) {
            when (mViewModel.clickedPositionForUpi.get()) {
                0 -> {
                    callGooglePayIntent()
                }
            }
        }


    }


    /*
   * navigate to the HomeScreen
   * */
    private fun intentToActivity() {
        val intent = Intent(this@AddMoneyUpiDebitView, CreateAccountSuccessView::class.java)
        startActivity(intent)
        finish()
    }

    private fun callGooglePayIntent() {

        var callback = PayUGpayResponse(this)

        Log.d(
            "post data",
            mViewModel.requestData.get()!!
        )
        Log.d(
            "merchany_key",
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).merchantKey.toString()
        )

        Log.d(
            "hash",
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).paymentHash.toString()
        )

        GPay.getInstance().checkForPaymentAvailability(
            this@AddMoneyUpiDebitView,
            callback,
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).paymentHash,
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).merchantKey,
            "default"
        )
        GPay.getInstance().makePayment(
            this@AddMoneyUpiDebitView,
            mViewModel.requestData.get(),
            callback,
            mViewModel.parseResponseOfStep1(mViewModel.requestData.get()).merchantKey,
            progressBar
        )

    }


    override fun onGpayResponseListener(payuResponse: String?) {
        Log.d("gpay_responseeee", payuResponse.toString())
    }

    /*
    * Used to call in case of phone pay
    * */


    fun callPhonePayIntent() {

    }

}
