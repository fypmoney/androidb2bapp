package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.connectivity.ApiConstant
import com.fypmoney.databinding.ViewSplashBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.SplashViewModel

/*
* This class is used for show app logo and check user logged in or not
* */
class SplashView : BaseActivity<ViewSplashBinding, SplashViewModel>() {
    private lateinit var mViewModel: SplashViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_splash
    }

    override fun getViewModel(): SplashViewModel {
        mViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.moveToNextScreen.observe(this)
        {
            if (it) {
                moveToNextScreen()
                mViewModel.moveToNextScreen.value = false
            }
        }


    }

    /*
    * navigate to the login screen
    * */
    private fun goToLoginScreen() {
        val intent = Intent(this, AadhaarAccountActivationView::class.java)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
        startActivity(intent)
        finish()
    }

    /*
   * navigate to the HomeScreen
   * */
    private fun goToDashboardScreen() {
        val intent = Intent(this@SplashView, HomeView::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>, type: String? = null) {
        val intent = Intent(this@SplashView, aClass)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, type)
        startActivity(intent)
        finish()
    }

    /*
    * This method is used to move to the next screen
    * */
    private fun moveToNextScreen(delayTime: Long = AppConstants.SPLASH_TIME) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (intent.hasExtra("tag")) {
                try {
                    intentToActivity(
                        Class.forName(AppConstants.BASE_ACTIVITY_URL + intent.getStringExtra("tag")),
                        intent.getStringExtra("type")
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    intentToActivity(HomeView::class.java)
                }
                finish()
            } else {
                if (SharedPrefUtils.getBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_IS_LOGIN
                    )!!
                ) {
                    when {
                        Utility.getCustomerDataFromPreference()!!.isReferralAllowed == AppConstants.YES -> {
                            intentToActivity(ReferralCodeView::class.java)
                        }
                        Utility.getCustomerDataFromPreference()!!.isProfileCompleted == AppConstants.NO -> {
                            intentToActivity(CreateAccountView::class.java)
                        }
                        else -> {
                            intentToActivity(HomeView::class.java)
                        }
                    }

                } else {
                    goToLoginScreen()
                }


            }
        }, delayTime)
    }

}
