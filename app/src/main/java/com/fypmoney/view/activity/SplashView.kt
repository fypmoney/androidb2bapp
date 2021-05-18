package com.fypmoney.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSplashBinding
import com.fypmoney.model.CustomerInfoResponse
import com.fypmoney.model.FeedDetails
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.viewmodel.SplashViewModel
import com.google.firebase.iid.FirebaseInstanceId

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
        Handler(Looper.getMainLooper()).postDelayed({
            if (intent.hasExtra("tag")) {
                try {
                       intentToActivity(
                            Class.forName(AppConstants.BASE_ACTIVITY_URL + intent.getStringExtra("tag")),intent.getStringExtra("type")
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
                        mViewModel.getCustomerInfoSuccess.value?.isReferralAllowed == AppConstants.YES -> {
                            intentToActivity(ReferralCodeView::class.java)
                        }
                        mViewModel.getCustomerInfoSuccess.value?.isProfileCompleted == AppConstants.NO -> {
                            intentToActivity(CreateAccountView::class.java)
                        }
                        else -> {
                            intentToActivity(HomeView::class.java)
                        }


                    }
                        goToDashboardScreen()
                } else {
                    goToLoginScreen()
                }


            }
        }, AppConstants.SPLASH_TIME)

    }

    /*
    * navigate to the login screen
    * */
    private fun goToLoginScreen() {
        val intent = Intent(this, LoginView::class.java)
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
    }

}
