package com.fypmoney.view.register

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.fyp.trackr.models.UserTrackr
import com.fyp.trackr.models.logOut
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.databinding.ActivityTimelineBinding

import com.fypmoney.util.AppConstants
import com.fypmoney.util.Utility
import com.fypmoney.view.register.viewModel.TimelineViewModel

import com.fypmoney.view.activity.LoginView


class TimeLineActivity : BaseActivity<ActivityTimelineBinding, TimelineViewModel>() {

    private lateinit var binding: ActivityTimelineBinding
    private val yourGiftVM by viewModels<TimelineViewModel> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        binding.ivToolBarBack.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
        binding.btnContinue.setOnClickListener(View.OnClickListener {
            callFreshChat(applicationContext)
        })
        binding.buttonLogout.setOnClickListener {
            yourGiftVM.callLogOutApi()
        }

        yourGiftVM.onLogoutSuccess.observe(this) {
            UserTrackr.logOut()
            intentToActivityMain(this, LoginView::class.java, isFinishAll = true)
        }

        if (Utility.getCustomerDataFromPreference()?.isProfileCompleted == AppConstants.YES) {
            binding.progressTimeline.progress = 33
            binding.textProgress.text = "Step 1"
            binding.step1Tick.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.color_task_card_green
                ), android.graphics.PorterDuff.Mode.SRC_IN
            );
        }

        if (Utility.getCustomerDataFromPreference()?.bankProfile?.isAccountActive == AppConstants.YES) {
            binding.progressTimeline.progress = 66
            binding.textProgress.text = "Step 2"
            binding.step2Tick.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.color_task_card_green
                ), android.graphics.PorterDuff.Mode.SRC_IN
            );

        }
    }


    private fun setObserver() {

    }


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    override fun getBindingVariable(): Int = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int = R.layout.activity_timeline

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): TimelineViewModel = yourGiftVM


}