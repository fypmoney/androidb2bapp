package com.fypmoney.view.home.main.homescreen.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fypmoney.BR
import com.fypmoney.R
import com.fypmoney.base.BaseActivity
import com.fypmoney.bindingAdapters.loadImage
import com.fypmoney.databinding.ActivityHomeBinding
import com.fypmoney.extension.onNavDestinationSelected
import com.fypmoney.extension.toGone
import com.fypmoney.extension.toVisible
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.home.main.homescreen.viewmodel.HomeActivityVM

class HomeActivity : BaseActivity<ActivityHomeBinding,HomeActivityVM>() {

    private lateinit var binding: ActivityHomeBinding
    private val homeActivityVM by viewModels<HomeActivityVM> { defaultViewModelProviderFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setupNavController()
        observeEvents()
        trackr {
            it.name = TrackrEvent.home_screen
            it.add(
                TrackrField.user_id, SharedPrefUtils.getLong(
                    applicationContext,
                    SharedPrefUtils.SF_KEY_USER_ID
                ).toString())
        }
    }



    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
        val navHomeController = navHostFragment.navController

        binding.bottomMenu.setItemSelected(R.id.navigation_home, true)
        binding.bottomMenu.setOnItemSelectedListener { id ->
            onNavDestinationSelected(id, navHomeController)
        }
    }

    private fun observeEvents() {
        homeActivityVM.event.observe(this,{
            handelEvents(it)
        })
    }

    private fun handelEvents(it: HomeActivityVM.HomeActivityEvent?) {
        when(it){
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
    override fun getBindingVariable(): Int  = BR.viewModel

    /**
     * @return layout resource id
     */
    override fun getLayoutId(): Int  = R.layout.activity_home

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    override fun getViewModel(): HomeActivityVM  = homeActivityVM


    override fun onStart() {
        super.onStart()
        showNewMessage()

    }
    override fun onResume() {
        super.onResume()
        loadProfile(homeActivityVM.userProfileUrl)
    }

    private fun showNewMessage() {
        if (homeActivityVM.isUnreadNotificationAvailable.isNullOrEmpty()) {
            binding.newNotification.toGone()
        } else {
            binding.newNotification.toVisible()
        }
    }


    private fun loadProfile(url: String?) {
        url?.let {
            loadImage(
                binding.myProfileIv,
                it,
                ContextCompat.getDrawable(this, R.drawable.ic_profile_img),
                true)
        }
    }



}