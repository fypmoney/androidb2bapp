package com.fypmoney.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.MediaController
import androidx.lifecycle.ViewModelProvider
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSplashBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.view_splash.*

/*
* This class is used for show app logo and check user logged in or not
* */
class SplashView : BaseActivity<ViewSplashBinding, SplashViewModel>() {
    private lateinit var mViewModel: SplashViewModel
    private var mediaControl: MediaController? = null

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
        val uri: Uri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.splash)
        mediaControl = MediaController(this)
        video.setMediaController(mediaControl)
        video.setVideoURI(uri)
        video.start()

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
        val intent = Intent(this, WalkThroughMainView::class.java)
        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, "")
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
            if (intent.hasExtra(AppConstants.NOTIFICATION_KEY_NOTIFICATION_TYPE)) {
                startActivity(onNotificationClick(intent))
            } else {
                if (SharedPrefUtils.getBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_IS_LOGIN
                    )!!
                ) {
                    when {
                        Utility.getCustomerDataFromPreference()!!.isProfileCompleted == AppConstants.NO -> {
                            when (Utility.getCustomerDataFromPreference()!!.isReferralAllowed) {
                                AppConstants.YES -> {
                                    intentToActivity(ReferralCodeView::class.java)
                                }
                                else -> {
                                    intentToActivity(CreateAccountView::class.java)

                                }
                            }
                        }
                        Utility.getCustomerDataFromPreference()!!.bankProfile?.isAccountActive == AppConstants.NO -> {
                            intentToActivity(HomeView::class.java)
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


    private fun onNotificationClick(intent: Intent): Intent {
        when (intent.getStringExtra(AppConstants.NOTIFICATION_KEY_NOTIFICATION_TYPE)) {
            AppConstants.NOTIFICATION_TYPE_IN_APP_DIRECT -> {

                when (intent.getStringExtra(AppConstants.NOTIFICATION_KEY_TYPE)) {
                    AppConstants.TYPE_APP_SLIDER_NOTIFICATION -> {
                        val intent = Intent(applicationContext, SplashView::class.java)
                        intent.putExtra(AppConstants.FROM_WHICH_SCREEN, AppConstants.NOTIFICATION)
                        intent.putExtra(
                            AppConstants.NOTIFICATION_APRID,
                            intent.getStringExtra(AppConstants.NOTIFICATION_KEY_APRID)
                        )
                        return intent

                    }
                    AppConstants.TYPE_NONE_NOTIFICATION -> {
                        try {
                            return Intent(
                                applicationContext,
                                Class.forName(
                                    AppConstants.BASE_ACTIVITY_URL + intent.getStringExtra(
                                        "url"
                                    )
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                            intentToActivity(SplashView::class.java)
                        }

                    }
                }

            }
        }
        return Intent(applicationContext, SplashView::class.java)
    }


}
