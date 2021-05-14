package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewCommunityBinding
import com.fypmoney.viewmodel.CommunityViewModel
import kotlinx.android.synthetic.main.toolbar.*

/*
* This class is used to handle school name city
* */
class CommunityView : BaseActivity<ViewCommunityBinding, CommunityViewModel>() {
    private lateinit var mViewModel: CommunityViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_community
    }

    override fun getViewModel(): CommunityViewModel {
        mViewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndTitle(
            context = this@CommunityView,
            toolbar = toolbar,
            isBackArrowVisible = true
        )
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onContinueClicked.observe(this) {
            intentToActivity()
        }


    }


    /*
   * navigate to the HomeScreen
   * */
    private fun intentToActivity() {
        val intent = Intent(this@CommunityView, CreateAccountSuccessView::class.java)
        startActivity(intent)
        finish()
    }

}
