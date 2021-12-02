package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr

import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewSplashBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.AppConstants.NOT_ALLOWED_MSG
import com.fypmoney.util.dynamiclinks.DynamicLinksUtil.getReferralCodeFromDynamicLink
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.home.main.homescreen.view.HomeActivity
import com.fypmoney.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.view_splash.*
import java.util.*


/*
* This class is used for show app logo and check user logged in or not
* */
class SplashView : BaseActivity<ViewSplashBinding, SplashViewModel>() {
    private lateinit var mViewModel: SplashViewModel
    private  val TAG = SplashView::class.java.simpleName
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
        getReferralCodeFromDynamicLink(activity = this,intent = intent,onReferralCodeFound = {
            SharedPrefUtils.putString(applicationContext,
            SharedPrefUtils.SF_REFERRAL_CODE_FROM_INVITE_LINK,it)
        })
        val uri: Uri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.splash)
        video.setMediaController(null)
        video.setVideoURI(uri)
        video.setOnPreparedListener { video.start() }
        trackr {
            it.name = TrackrEvent.app_launch
        }

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
                    Log.d(TAG,"0")

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
                    Log.d(TAG,"1")

                }
                SplashViewModel.AppUpdateState.NOTALLOWED -> {
                    checkUpdate.value = false
                    Utility.showToast(NOT_ALLOWED_MSG)
                    finish()
                    Log.d(TAG,NOT_ALLOWED_MSG)


                }
                SplashViewModel.AppUpdateState.NOTUPDATE -> {
                    checkUpdate.value = false
                    Log.d(TAG,"NOTUPDATE")

                }
            }

            mViewModel.moveToNextScreen.value = true

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
    private fun moveToNextScreen() {
            if (SharedPrefUtils.getBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_IS_LOGIN
                    )!!
                ) {
                    when {
                        Utility.getCustomerDataFromPreference()?.isProfileCompleted == AppConstants.NO -> {
                            intentToActivity(CreateAccountView::class.java)
                        }
                        Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.NO -> {
                            intentToActivity(AadhaarAccountActivationView::class.java)
                        }
                        else -> {
                            if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {

                                if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {

                                    intentToActivity(HomeActivity::class.java)
                                } else {
                                    intentToActivity(PermissionsActivity::class.java)
                                }
                            }
                            else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "0") {
                                when (Utility.getCustomerDataFromPreference()?.isReferralAllowed) {
                                    AppConstants.YES -> {
                                        intentToActivity(ReferralCodeView::class.java)
                                    }

                                    else -> {
                                        if (hasPermissions(
                                                this,
                                                Manifest.permission.READ_CONTACTS
                                            )
                                        ) {
                                            intentToActivity(HomeActivity::class.java)
                                        } else {
                                            intentToActivity(PermissionsActivity::class.java)
                                        }

                                    }
                                }
                            }
                            else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "90") {
                                intentToActivity(AgeAllowedActivationView::class.java)
                            }
                            else {
                                if (Utility.getCustomerDataFromPreference()?.postKycScreenCode == null &&
                                    Utility.getCustomerDataFromPreference()?.userProfile?.gender == null && mViewModel.callCustomer.value == false) {
                                    mViewModel.callGetCustomerProfileApi()

                                }
                            }
                        }
                    }

                } else {
                    goToLoginScreen()
                }
    }



    override fun onTryAgainClicked() {
        mViewModel.setUpApp()
    }



}
