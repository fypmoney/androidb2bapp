package com.fypmoney.view.activity

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.SelectInterestViewActivityBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.view.register.PersonalisedOffersActivity
import com.fypmoney.viewmodel.SelectInterestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_animation.*

/*
* This is used to handle interest of the user
* */
class ChooseInterestHomeView :
    BaseActivity<SelectInterestViewActivityBinding, SelectInterestViewModel>() {
    private lateinit var mViewModel: SelectInterestViewModel
    private lateinit var mViewBinding: SelectInterestViewActivityBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.select_interest_view_activity
    }

    override fun getViewModel(): SelectInterestViewModel {
        mViewModel = ViewModelProvider(this).get(SelectInterestViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        setToolbarAndTitle(
            context = this,
            toolbar = toolbar,
            isBackArrowVisible = true, toolbarTitle = "Choose Interests",
            titleColor = Color.BLACK,
            backArrowTint = Color.BLACK
        )

        setObserver()
    }

/*
Create this method for observe the viewModel fields
 */

    private fun setObserver() {
        mViewModel.onUpdateProfileSuccess.observe(this) {
            if (it) {
                Utility.showToast("Your interest has been updated successfully")
                mViewModel.onUpdateProfileSuccess.value = false
//                val intent = Intent(this, HomeActivity::class.java)
//
//
//                startActivity(intent)
                onBackPressed()


            }
        }
        mViewModel.onAnswerLater.observe(this) {
            if (it) {
                mViewModel.onAnswerLater.value = false
                finish()
            }
        }
    }


}
