package com.fypmoney.view.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.SelectInterestViewActivityBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.SelectInterestViewModel
import kotlinx.android.synthetic.main.toolbar_animation.*

/*
* This is used to handle interest of the user
* */
class SelectInterestView :
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
        if (intent.hasExtra(AppConstants.INTEREST_TYPE)){

            val interestScreenType:Boolean=intent.getBooleanExtra(AppConstants.INTEREST_TYPE,false)


        // this method help us to hide or un hide items
        setLottieAnimationToolBar(
            isBackArrowVisible = interestScreenType,//back arrow visibility
            isLottieAnimation = interestScreenType,// lottie animation visibility
            imageView = ivToolBarBack,//back image view
            lottieAnimationView = ivAnimationGift,
            screenName = SelectInterestView::class.java.simpleName

        )// lottie animation view
        }
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
                finish()
            }
        }
        mViewModel.onAnswerLater.observe(this) {
            if (it) {
                mViewModel.onAnswerLater.value = false
                finish()
            }
        }  }


}
