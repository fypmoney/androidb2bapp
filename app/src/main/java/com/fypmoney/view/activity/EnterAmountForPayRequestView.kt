package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.databinding.ViewEnterAmountForPayRequestBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.viewmodel.EnterAmountForPayRequestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*

/*
* This class is used to handle school name city
* */
class EnterAmountForPayRequestView : BaseActivity<ViewEnterAmountForPayRequestBinding, EnterAmountForPayRequestViewModel>() {
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
        mViewModel.setResponseAfterContactSelected(intent.getParcelableExtra(AppConstants.CONTACT_SELECTED_RESPONSE),actionValue = intent.getStringExtra(AppConstants.WHICH_ACTION))

        when(mViewModel.action.get()){
            AppConstants.PAY->{
                setToolbarAndTitle(
                    context = this@EnterAmountForPayRequestView,
                    toolbar = toolbar,
                    isBackArrowVisible = true,toolbarTitle = getString(R.string.pay_title)
                )
            }
            else->{
                setToolbarAndTitle(
                    context = this@EnterAmountForPayRequestView,
                    toolbar = toolbar,
                    isBackArrowVisible = true,toolbarTitle = getString(R.string.request_title)
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
                add_money_editext.setSelection(add_money_editext.text.length);
                mViewModel.setEdittextLength.value = false
            }
        }

        mViewModel.onPayClicked.observe(this) {
            if (it) {

                mViewModel.onPayClicked.value = false
            }
        }


    }


    /*
   * navigate to the HomeScreen
   * */
    private fun intentToActivity() {
        val intent = Intent(this@EnterAmountForPayRequestView, CreateAccountSuccessView::class.java)
        startActivity(intent)
        finish()
    }

}
