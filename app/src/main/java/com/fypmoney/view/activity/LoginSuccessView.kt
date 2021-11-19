package com.fypmoney.view.activity

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.fyp.trackr.models.*
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ViewLoginSuccessBinding
import com.fypmoney.util.AppConstants
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.viewmodel.LoginSuccessViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.view_login_success.*
import java.util.*
import kotlin.concurrent.schedule
import kotlinx.android.synthetic.main.view_walk_through_one.*

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
            it.add(
                TrackrField.user_id,SharedPrefUtils.getLong(
                    applicationContext,
                    SharedPrefUtils.SF_KEY_USER_ID
                ).toString())
        }

        UserTrackr.login(SharedPrefUtils.getLong(
            applicationContext,
            SharedPrefUtils.SF_KEY_USER_ID
        ).toString())
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
                    intentToActivity(CreateAccountView::class.java)

                }
                Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.NO -> {
                    intentToActivity(AadhaarAccountActivationView::class.java)
                }
                else-> {
                    if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "1") {
                        if (hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                            intentToActivity(HomeView::class.java)
                        } else {
                            intentToActivity(PermissionsActivity::class.java)
                        }
                    } else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "0") {
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
                                    intentToActivity(HomeView::class.java)
                                } else {
                                    intentToActivity(PermissionsActivity::class.java)
                                }

                            }
                        }
                    } else if (Utility.getCustomerDataFromPreference()?.postKycScreenCode != null && Utility.getCustomerDataFromPreference()?.postKycScreenCode == "90") {
                        intentToActivity(AgeAllowedActivationView::class.java)
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
