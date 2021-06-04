package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.viewmodel.EnterAmountForPayRequestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_add_money.*

/*
* This class is used to handle school name city
* */
class EnterAmountForPayRequestView : BaseActivity<ViewCommunityBinding, EnterAmountForPayRequestViewModel>() {
    private lateinit var mViewModel: EnterAmountForPayRequestViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_community
    }

    override fun getViewModel(): EnterAmountForPayRequestViewModel {
        mViewModel = ViewModelProvider(this).get(EnterAmountForPayRequestViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@EnterAmountForPayRequestView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
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
