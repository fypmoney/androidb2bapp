package com.fypmoney.view.Register

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.ActivityHomeBinding
import com.fypmoney.databinding.ActivityInviteParentSiblingBinding
import com.fypmoney.extension.onNavDestinationSelected
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.AddTaskActivity
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.home.main.homescreen.viewmodel.HomeActivityVM
import com.moengage.core.internal.utils.isNullOrEmpty

class InviteParentSiblingActivity :
    BaseActivity<ActivityInviteParentSiblingBinding, HomeActivityVM>() {

    private lateinit var binding: ActivityInviteParentSiblingBinding
    private val homeActivityVM by viewModels<HomeActivityVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()

        observeEvents()

        binding.continueBtn.setOnClickListener {


            if (!binding.phoneEd.text.isNullOrEmpty()) {
                if (binding.phoneEd.text.toString().length != 10) {
                    Utility.showToast("Enter a valid number")
                } else {
                    val intent = Intent(this, SelectRelationActivity::class.java)


                    intent.putExtra("phone", binding.phoneEd.text.toString())
                    val bndlAnimation = ActivityOptions.makeCustomAnimation(
                        applicationContext,
                        com.fypmoney.R.anim.slideinleft,
                        com.fypmoney.R.anim.slideinright
                    ).toBundle()


                    startActivity(intent, bndlAnimation)
                }
            } else {
                Utility.showToast("Enter mobile number")
            }


        }


    }


    private fun observeEvents() {
        homeActivityVM.event.observe(this, {
            handelEvents(it)
        })
    }

    private fun handelEvents(it: HomeActivityVM.HomeActivityEvent?) {
        when (it) {
            HomeActivityVM.HomeActivityEvent.NotificationClicked -> {
                startActivity(Intent(this, NotificationView::class.java))

            }
            HomeActivityVM.HomeActivityEvent.ProfileClicked -> {
                startActivity(Intent(this, UserProfileView::class.java))
            }

        }
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
    override fun getLayoutId(): Int = R.layout.activity_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): HomeActivityVM = homeActivityVM


}