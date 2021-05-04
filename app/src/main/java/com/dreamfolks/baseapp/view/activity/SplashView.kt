package com.dreamfolks.baseapp.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewSplashBinding
import com.dreamfolks.baseapp.util.AppConstants
import com.dreamfolks.baseapp.util.SharedPrefUtils
import com.dreamfolks.baseapp.viewmodel.SplashViewModel

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
            if (SharedPrefUtils.getBoolean(applicationContext, SharedPrefUtils.SF_KEY_IS_LOGIN)!!) {
                goToDashboardScreen()
            } else {
                goToLoginScreen()
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


}
