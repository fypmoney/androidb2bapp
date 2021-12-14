package com.fypmoney.view.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.SelectInterestViewActivityBinding
import com.fypmoney.util.Utility
import com.fypmoney.view.register.PersonalisedActivity
import com.fypmoney.viewmodel.SelectInterestViewModel
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar_animation.*

/*
* This is used to handle interest of the user
* */
class ChooseInterestRegisterView :
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
        setLottieAnimationToolBar(
            isBackArrowVisible = true,//back arrow visibility
            isLottieAnimation = true,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift
        )// lottie animation view


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
                val intent = Intent(this, PersonalisedActivity::class.java)

                val bndlAnimation = ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    com.fypmoney.R.anim.slideinleft,
                    com.fypmoney.R.anim.slideinright
                ).toBundle()
                startActivity(intent, bndlAnimation)
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
