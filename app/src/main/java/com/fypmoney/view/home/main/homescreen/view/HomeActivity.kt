package com.fypmoney.view.home.main.homescreen.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
import com.fypmoney.listener.LocationListenerClass
import com.fypmoney.util.AppConstants.YES
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.util.SharedPrefUtils.Companion.SF_SHOW_MY_ORDERS
import com.fypmoney.util.Utility
import com.fypmoney.view.activity.NotificationView
import com.fypmoney.view.activity.UserProfileView
import com.fypmoney.view.home.main.home.view.HomeFragment
import com.fypmoney.view.home.main.homescreen.viewmodel.HomeActivityVM
import java.util.concurrent.atomic.AtomicBoolean

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeActivityVM>(),
    LocationListenerClass.GetCurrentLocationListener {

    private lateinit var binding: ActivityHomeBinding
    private val homeActivityVM by viewModels<HomeActivityVM> { defaultViewModelProviderFactory }
    private val doubleBackPressed = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setupNavController()
        observeEvents()
        binding.help.setOnClickListener {
            callFreshChat(applicationContext)
        }
        trackr {
            it.name = TrackrEvent.home_screen
            it.add(
                TrackrField.user_id, SharedPrefUtils.getLong(
                    applicationContext,
                    SharedPrefUtils.SF_KEY_USER_ID
                ).toString()
            )
        }

        LocationListenerClass(
            this, this
        ).permissions()
        checkAndAskPermission()

    }



    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment
        val navHomeController = navHostFragment.navController
        navController = navHomeController
        binding.bottomMenu.setItemSelected(R.id.navigation_home, true)

        binding.bottomMenu.setOnItemSelectedListener { id ->
            onNavDestinationSelected(id, navHomeController)
        }

        navHomeController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.navigation_home -> {
                    trackr {
                        it.name = TrackrEvent.tab_home_click
                    }
                    binding.bottomMenu.setItemSelected(R.id.navigation_home, true)
                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.white))
                    binding.toolbarTitleTv.setTextColor(resources.getColor(R.color.black))
                    homeActivityVM.toolbarTitle.value =
                        "Hey ${Utility.getCustomerDataFromPreference()?.firstName},"
                    binding.help.toVisible()
                    binding.framne.toVisible()
                    binding.transactionHistoryAiv.toGone()
                    binding.giftVoucherHistoryTv.toGone()
                    binding.myProfileIv.toVisible()
                    binding.toolbarTitleTv.toVisible()
                    showToolbar()
                    showBottomNavigation()
                }
                R.id.navigation_fyper -> {
                    trackr {
                        it.name = TrackrEvent.tab_family_click
                    }
                    binding.bottomMenu.setItemSelected(R.id.navigation_fyper, true)

                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.white))
                    binding.toolbarTitleTv.setTextColor(resources.getColor(R.color.black))
                    homeActivityVM.toolbarTitle.value = getString(R.string.fyper_txt)
                    binding.help.toVisible()
                    binding.framne.toVisible()
                    binding.giftVoucherHistoryTv.toGone()
                    binding.transactionHistoryAiv.toGone()
                    binding.myProfileIv.toGone()
                    showToolbar()
                    showBottomNavigation()
                }
                R.id.navigation_rewards -> {
                    trackr {
                        it.name = TrackrEvent.tab_reward_click
                    }
                    binding.bottomMenu.setItemSelected(R.id.navigation_rewards, true)

                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.reward_background))
                    binding.toolbarTitleTv.setTextColor(resources.getColor(R.color.white))
                    homeActivityVM.toolbarTitle.value = getString(R.string.rewards)
                    binding.help.toGone()
                    binding.framne.toGone()
                    binding.giftVoucherHistoryTv.toGone()
                    binding.transactionHistoryAiv.toVisible()
                    binding.myProfileIv.toGone()
                    showToolbar()
                    showBottomNavigation()
                }
                R.id.navigation_explore -> {
                    trackr {
                        it.name = TrackrEvent.tab_explore_click
                    }
                    binding.bottomMenu.setItemSelected(R.id.navigation_explore, true)
                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.reward_background))
                    binding.toolbarTitleTv.setTextColor(resources.getColor(R.color.white))
                    homeActivityVM.toolbarTitle.value = getString(R.string.explore)
                    binding.help.toGone()
                    binding.framne.toGone()
                    binding.myProfileIv.toGone()
                    SharedPrefUtils.getString(this,SF_SHOW_MY_ORDERS)?.let {
                        if(it==YES){
                            binding.giftVoucherHistoryTv.toVisible()
                        }else{
                            binding.giftVoucherHistoryTv.toGone()
                        }
                    }

                    binding.transactionHistoryAiv.toGone()
                    showToolbar()
                    showBottomNavigation()
                }
                else -> {
                    hideToolbar()
                    hideBottomNavigation()
                }
            }
        }

    }

    private fun hideToolbar(){
        binding.appBar.toGone()
    }
    private fun showToolbar(){
        binding.appBar.toVisible()
    }
    private fun showBottomNavigation(){
        binding.bottomMenu.toVisible()
    }
    private fun hideBottomNavigation(){
        binding.bottomMenu.toGone()

    }

    private fun observeEvents() {
        homeActivityVM.event.observe(this) {
            handelEvents(it)
        }
    }

    private fun handelEvents(it: HomeActivityVM.HomeActivityEvent?) {
        when(it){
            HomeActivityVM.HomeActivityEvent.NotificationClicked -> {
                startActivity(Intent(this, NotificationView::class.java))

            }
            HomeActivityVM.HomeActivityEvent.ProfileClicked -> {
                startActivity(Intent(this, UserProfileView::class.java))
            }
            HomeActivityVM.HomeActivityEvent.TransactionHistoryClicked ->{
                //startActivity(Intent(this, BankTransactionHistoryView::class.java))
                findNavController(R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_rewards_history)
            }
            HomeActivityVM.HomeActivityEvent.GiftVoucherHistoryClicked -> {
                findNavController(R.id.nav_host_fragment_activity_home).navigate(R.id.navigation_gift_card_history)
            }
            null -> {

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
                true
            )
        }
    }

    private fun getCurrentBottomFragment(): Fragment? {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home)
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    override fun onBackPressed() {
        if (getCurrentBottomFragment() !is HomeFragment) {
            super.onBackPressed()
            return
        }
        if (doubleBackPressed.get()) {
            finish()
            return
        }
        doubleBackPressed.compareAndSet(false, true)
        Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackPressed.set(false) }, 2000)
    }


    /*
    * This method is used to check for permissions
    * */
    private fun checkAndAskPermission() {
        when (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            false -> {
                homeActivityVM.postLatlong(
                    "", "", SharedPrefUtils.getLong(
                        application, key = SharedPrefUtils.SF_KEY_USER_ID
                    )
                )
            }
            else -> {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    override fun getCurrentLocation(
        isInternetConnected: Boolean?,
        latitude: Double,
        Longitude: Double
    ) {
        SharedPrefUtils.getLong(
            application, key = SharedPrefUtils.SF_KEY_USER_ID
        ).let {
            homeActivityVM.postLatlong("$latitude", "$Longitude", it)
        }
    }
}