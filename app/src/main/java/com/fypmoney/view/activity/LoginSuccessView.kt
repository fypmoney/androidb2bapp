package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginSuccessBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.kycagent.ui.KycAgentActivity
import com.fypmoney.view.register.InviteParentSiblingActivity
import com.fypmoney.view.register.PanAdhaarSelectionActivity
import com.fypmoney.view.register.PendingRequestActivity
import com.fypmoney.viewmodel.LoginSuccessViewModel
import kotlinx.android.synthetic.main.view_login_success.*
import java.util.*
import kotlin.concurrent.schedule

/*
* This class is used for show login success message
* */
class LoginSuccessView : BaseActivity<ViewLoginSuccessBinding, LoginSuccessViewModel>() {
    private lateinit var mViewModel: LoginSuccessViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_login_success
    }

    override fun getViewModel(): LoginSuccessViewModel {
        mViewModel = ViewModelProvider(this).get(LoginSuccessViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObserver()

        trackr {
            it.name = TrackrEvent.phone_verification
        }
        image.gifResource = R.raw.phone_verified
        Timer().schedule(1000) {
            runOnUiThread {
                image.pause()
            }
        }
        val mp: MediaPlayer = MediaPlayer.create(
            this,
            R.raw.tick
        )
        mp.start()

    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {
        mViewModel.onApiSuccess.observe(this) {
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
                                if (userInterest != null && userInterest?.size > 0) {
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
                                if (userInterest != null && userInterest?.size > 0) {
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
                    } else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "90") {
                        if (!Utility.getCustomerDataFromPreference()?.isInvited.isNullOrEmpty() && Utility.getCustomerDataFromPreference()?.isInvited == AppConstants.YES) {
                            if (Utility.getCustomerDataFromPreference()?.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_INVITED) {
                                intentToActivity(PendingRequestActivity::class.java)
                            } else if (Utility.getCustomerDataFromPreference()?.inviteReqStatus == AppConstants.ADD_MEMBER_STATUS_APPROVED) {
                                val userInterest =
                                    SharedPrefUtils.getArrayList(
                                        getApplication(),
                                        SharedPrefUtils.SF_KEY_USER_INTEREST
                                    )
                                if (userInterest != null && userInterest?.size > 0) {
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


                }

            }


        }

    }


    /**
     * Method to navigate to the different activity
     */
    private fun intentToActivity(aClass: Class<*>) {
        val intent = Intent(this@LoginSuccessView, aClass)
        intent.putExtra(
            AppConstants.IS_PROFILE_COMPLETED,
            Utility.getCustomerDataFromPreference()?.isProfileCompleted
        )
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finishAffinity()
    }
}
