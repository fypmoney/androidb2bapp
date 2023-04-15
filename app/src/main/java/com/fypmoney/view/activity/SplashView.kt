package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
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
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.util.dynamiclinks.DynamicLinksUtil.getReferralCodeFromDynamicLink
import com.fypmoney.view.kycagent.ui.KycAgentActivity
import com.fypmoney.view.register.InviteParentSiblingActivity
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.register.PendingRequestActivity
import com.fypmoney.viewmodel.SplashViewModel


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
        checkUpdate.observe(this) {
            if (!it && mViewModel.moveToNextScreen.value!!) {
                moveToNextScreen()
                mViewModel.moveToNextScreen.value = false
            }
        }

        mViewModel.appUpdateState.observe(this) {
            mViewModel.moveToNextScreen.value = true

//            when (it) {
//                SplashViewModel.AppUpdateState.FLEXIBLE -> {
//                    PockketApplication.instance.appUpdateRequired = true
//                    SharedPrefUtils.putInt(
//                        applicationContext,
//                        SharedPrefUtils.SF_KEY_APP_UPDATE_TYPE,
//                        0
//                    )
//
//                }
//                SplashViewModel.AppUpdateState.FORCEUPDATE -> {
//                    PockketApplication.instance.appUpdateRequired = true
//                    SharedPrefUtils.putInt(
//                        applicationContext,
//                        SharedPrefUtils.SF_KEY_APP_UPDATE_TYPE,
//                        1
//                    )
//
//
//                }
//                SplashViewModel.AppUpdateState.NOTALLOWED -> {
//                    checkUpdate.value = false
//                    Utility.showToast(NOT_ALLOWED_MSG)
//                    finish()
//
//
//                }
//                SplashViewModel.AppUpdateState.NOTUPDATE -> {
//                    checkUpdate.value = false
//
//
//                }
//            }
//
//            mViewModel.moveToNextScreen.value = true

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
    private fun moveToNextScreen() {

            if (SharedPrefUtils.getBoolean(
                        applicationContext,
                        SharedPrefUtils.SF_KEY_IS_LOGIN
                    )!!
                ) {
                when {
                    Utility.getCustomerDataFromPreference()?.isProfileCompleted == AppConstants.NO -> {
                        //intentToActivity(UserTypeOnLoginView::class.java)
                        val intent = Intent(this, CreateAccountView::class.java)
                        intent.putExtra(AppConstants.USER_TYPE_NEW, true)
                        intent.putExtra(AppConstants.USER_TYPE, "Teenager")
                        startActivity(intent)
                    }
                    Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.NO -> {
                        intentToActivity(PanAdhaarSelectionActivity::class.java)
                    }
                    Utility.getCustomerDataFromPreference()?.isHomeViewed == AppConstants.YES -> {
                        if (hasPermissions(
                                this,
                                Manifest.permission.READ_CONTACTS
                            )
                        ) {
                            intentToActivity(KycAgentActivity::class.java)
                        } else {
                            intentToActivity(PermissionsActivity::class.java)
                        }
                    }
                    else -> {
                        if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {

                            if (!Utility.getCustomerDataFromPreference()?.isInvited.isNullOrEmpty() && Utility.getCustomerDataFromPreference()?.isInvited == AppConstants.YES) {
                                if (Utility.getCustomerDataFromPreference()?.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_INVITED) {
                                    intentToActivity(PendingRequestActivity::class.java)
                                } else if (Utility.getCustomerDataFromPreference()?.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_APPROVED) {
                                    val userInterest =
                                        SharedPrefUtils.getArrayList(
                                            getApplication(),
                                            SharedPrefUtils.SF_KEY_USER_INTEREST
                                        )
                                    if (userInterest != null && userInterest?.size!! > 0) {
                                        if (hasPermissions(
                                                this,
                                                Manifest.permission.READ_CONTACTS
                                            )
                                        ) {
                                            intentToActivity(KycAgentActivity::class.java)
                                        } else {
                                            intentToActivity(PermissionsActivity::class.java)
                                        }
                                    } else {
                                        intentToActivity(ChooseInterestRegisterView::class.java)
                                    }


                                } else {
                                    intentToActivity(InviteParentSiblingActivity::class.java)
                                }
                            }
                            else {
                                val intent =
                                    Intent(this@SplashView, InviteParentSiblingActivity::class.java)
                                intent.putExtra(AppConstants.USER_TYPE, "1")
                                startActivity(intent)
                                finish()
                            }

                        } else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "0") {
                                when (Utility.getCustomerDataFromPreference()?.isReferralAllowed) {
                                    AppConstants.YES -> {
                                        intentToActivity(ReferralCodeView::class.java)
                                    }
                                    else -> {
                                        val userInterest =
                                            SharedPrefUtils.getArrayList(
                                                getApplication(),
                                                SharedPrefUtils.SF_KEY_USER_INTEREST
                                            )
                                        if (userInterest != null && userInterest?.size!! > 0) {
                                            if (hasPermissions(
                                                    this,
                                                    Manifest.permission.READ_CONTACTS
                                                )
                                            ) {
                                                intentToActivity(KycAgentActivity::class.java)
                                            } else {
                                                intentToActivity(PermissionsActivity::class.java)
                                            }
                                        } else {
                                            intentToActivity(ChooseInterestRegisterView::class.java)
                                        }

                                    }
                                }
                            }
                            else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "90") {
                            if (!Utility.getCustomerDataFromPreference()?.isInvited.isNullOrEmpty() && Utility.getCustomerDataFromPreference()?.isInvited == AppConstants.YES) {
                                if (Utility.getCustomerDataFromPreference()?.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_INVITED) {
                                    intentToActivity(PendingRequestActivity::class.java)
                                } else if (Utility.getCustomerDataFromPreference()?.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_APPROVED) {
                                    val userInterest =
                                        SharedPrefUtils.getArrayList(
                                            getApplication(),
                                            SharedPrefUtils.SF_KEY_USER_INTEREST
                                        )
                                    if (userInterest != null && userInterest?.size!! > 0) {
                                        if (hasPermissions(
                                                this,
                                                Manifest.permission.READ_CONTACTS
                                            )
                                        ) {
                                            intentToActivity(KycAgentActivity::class.java)
                                        } else {
                                            intentToActivity(PermissionsActivity::class.java)
                                        }
                                    } else {
                                        intentToActivity(ChooseInterestRegisterView::class.java)
                                    }
                                } else {
                                    intentToActivity(InviteParentSiblingActivity::class.java)
                                }

                            } else {
                                val intent = Intent(this, InviteParentSiblingActivity::class.java)
                                intent.putExtra(AppConstants.USER_TYPE, "90")
                                startActivity(intent)
                                finish()


                            }
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
