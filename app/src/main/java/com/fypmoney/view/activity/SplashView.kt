package com.fypmoney.view.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider

import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.AppSignatureHelper
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSplashBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.NOT_ALLOWED_MSG
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.view_splash.*


/*
* This class is used for show app logo and check user logged in or not
* */
class SplashView : BaseActivity<ViewSplashBinding, SplashViewModel>() {
    private lateinit var mViewModel: SplashViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_CONTACTS
    )

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
       //val appSignatureHelper =  AppSignatureHelper(this)
        //Log.d("Data","Chutiya "+appSignatureHelper.appSignatures)
        val uri: Uri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.splash)
        video.setMediaController(null)
        video.setVideoURI(uri)
        video.setOnPreparedListener { video.start() }




    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.moveToNextScreen.observe(this)
        {
            if (it && !checkUpdate.value!!) {
                moveToNextScreen()
                mViewModel.moveToNextScreen.value = false
            }
        }

        //Todo change in single activity acrhitrcture
        checkUpdate.observe(this, {
            if(!it &&  mViewModel.moveToNextScreen.value!!){
                moveToNextScreen()
                mViewModel.moveToNextScreen.value = false
            }
        })

        mViewModel.appUpdateState.observe(this, {
            when(it){
                SplashViewModel.AppUpdateState.FLEXIBLE -> {
                    /*checkUpdate.value = true
                    updateType = 0
                    checkForAppUpdate()*/
                    PockketApplication.instance.appUpdateRequired = true
                    SharedPrefUtils.putInt(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_APP_UPDATE_TYPE,
                        0
                    )

                }
                SplashViewModel.AppUpdateState.FORCEUPDATE -> {
                    /*checkUpdate.value = true
                    updateType = 1
                    checkForAppUpdate()*/
                    PockketApplication.instance.appUpdateRequired = true
                    SharedPrefUtils.putInt(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_APP_UPDATE_TYPE,
                        1
                    )
                }
                SplashViewModel.AppUpdateState.NOTALLOWED -> {
                    checkUpdate.value = false
                    Utility.showToast(NOT_ALLOWED_MSG)

                }
                SplashViewModel.AppUpdateState.NOTUPDATE -> {
                    checkUpdate.value = false
                }
            }
        })


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
            /*if (intent.hasExtra(AppConstants.NOTIFICATION_KEY_NOTIFICATION_TYPE)) {
                startActivity(onNotificationClick(intent))
            } else{*/
            if (SharedPrefUtils.getBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_IS_LOGIN
                    )!!
                ) {
                    when {
                        Utility.getCustomerDataFromPreference()?.isProfileCompleted == AppConstants.NO -> {
                            when (Utility.getCustomerDataFromPreference()?.isReferralAllowed) {
                                AppConstants.YES -> {
                                    intentToActivity(ReferralCodeView::class.java)
                                }
                                else -> {
                                    intentToActivity(CreateAccountView::class.java)

                                }
                            }
                        }
                        Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.NO -> {
                            intentToActivity(AadhaarAccountActivationView::class.java)
                        }
                        else -> {
                            if (hasPermissions(this, *PERMISSIONS)) {
                                intentToActivity(HomeView::class.java)
                            } else {
                                intentToActivity(PermissionsActivity::class.java)
                            }

                        }
                    }

                } else {
                    goToLoginScreen()
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

    override fun onTryAgainClicked() {
        mViewModel.setUpApp()
    }

}
